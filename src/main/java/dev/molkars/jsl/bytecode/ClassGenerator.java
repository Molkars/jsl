package dev.molkars.jsl.bytecode;

import dev.molkars.jsl.Essentials;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ClassGenerator implements MethodGeneratorDelegate, IClassGenerator, Display {
    final BytecodeContext context;
    final TypeRef.Generated generatedType;
    final Stack<MethodGenerator2> methodStack = new Stack<>();
    final List<MethodGenerator2> methods = new LinkedList<>();
    final HashMap<String, TypeRef> fields = new HashMap<>();

    ClassGenerator(BytecodeContext context, String module, String name, TypeRef superType, TypeRef... superTypes) {
        this.context = context;
        this.generatedType = context.addType(module, name, superType, superTypes);
    }

    public static String formatClassBytes(byte[] bytes) {
        StringWriter writer = new StringWriter();
        var visitor = new TraceClassVisitor(new PrintWriter(writer));
        CheckClassAdapter checkAdapter = new CheckClassAdapter(visitor);
        ClassReader reader = new ClassReader(bytes);
        reader.accept(checkAdapter, 0);
        return writer.getBuffer().toString();
    }

    @Override
    public MethodGeneratorFacade getMethodGenerator() throws IllegalStateException {
        if (methodStack.isEmpty())
            throw new IllegalStateException("no method on stack, cannot add instructions");
        return methodStack.peek();
    }

    public TypeRef.Generated getType() {
        return generatedType;
    }

    public void close() {
        if (!methodStack.isEmpty())
            throw new IllegalStateException("cannot close class, some methods are still open");
    }

    public void closeMethod() {
        if (methodStack.isEmpty())
            throw new IllegalStateException("no method on stack, cannot close method");
        MethodGenerator2 method = methodStack.peek();
        if (!method.hasReturned())
            throw new IllegalStateException("method is not terminated, cannot close method");
        method.close();
        methodStack.pop();
        methods.add(method);
    }

    public String getBytecodeString() {
        StringWriter writer = new StringWriter();
        var visiter = new TraceClassVisitor(new PrintWriter(writer));
        CheckClassAdapter checkAdapter = new CheckClassAdapter(visiter);
        ClassReader reader = new ClassReader(generateBytecode());
        reader.accept(checkAdapter, 0);
        return writer.toString();
    }

    public byte[] generateBytecode() {
        if (!methodStack.isEmpty())
            throw new IllegalStateException("cannot generate bytecode, some methods are still open");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        String[] interfaces = Essentials.arrayMap(generatedType.getInterfaces(), TypeRef::getQualifiedName, String[]::new);
        writer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, generatedType.getQualifiedName(), null,
                generatedType.getSuperType().getQualifiedName(), interfaces);

        for (var field : getFields().entrySet()) {
            writer.visitField(Opcodes.ACC_PUBLIC, field.getKey(), field.getValue().getDescriptor(), null, null);
        }

        for (MethodGenerator2 method : methods)
            method.compile(writer);
        writer.visitEnd();
        return writer.toByteArray();
    }

    @Override
    public MethodGenerator2 addMethod(String name, TypeRef returnTypeRef, TypeRef... argumentTypeRefs) {
        MethodGenerator2 method = new MethodGenerator2(context, generatedType, false, name, returnTypeRef, argumentTypeRefs, this);
        methodStack.push(method);
        return method;
    }

    @Override
    public MethodGenerator2 addMethod(String name, Class<?> returnType, Class<?>... argumentTypes) {
        TypeRef returnTypeRef = context.getTypeFor(returnType);
        TypeRef[] argumentTypeRefs = new TypeRef[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            argumentTypeRefs[i] = context.getTypeFor(argumentTypes[i]);
        }
        return addMethod(name, returnTypeRef, argumentTypeRefs);
    }


    @Override
    public void addField(String name, Class<?> type) {
        var fields = getFields();
        if (fields.containsKey(name)) {
            throw new IllegalStateException("field %s already exists".formatted(name));
        }
        fields.put(name, context.getTypeFor(type));
    }

    @Override
    public void addField(String name, TypeRef type) {
        var fields = getFields();
        if (fields.containsKey(name)) {
            throw new IllegalStateException("field %s already exists".formatted(name));
        }
        fields.put(name, type);
    }

    @Override
    public HashMap<String, TypeRef> getFields() {
        return fields;
    }

    @Override
    public void display(StringBuffer b) {
        b.append("class ");
        generatedType.display(b);
        b.append(" {\n");
        for (MethodGenerator2 method : methods) {
            method.display(b);
            b.append("\n");
        }
        b.append("}\n");
    }
}
