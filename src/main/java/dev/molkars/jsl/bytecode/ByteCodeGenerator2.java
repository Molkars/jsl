package dev.molkars.jsl.bytecode;

import dev.molkars.jsl.Essentials;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;

public class ByteCodeGenerator2 implements MethodGeneratorDelegate, IClassGeneratorDelegate, Display {
    final BytecodeContext context = new BytecodeContext();
    Stack<ClassGenerator> classStack = new Stack<>();
    LinkedHashSet<ClassGenerator> classes = new LinkedHashSet<>();

    public ByteCodeGenerator2() {
    }

    @Override
    public MethodGeneratorFacade getMethodGenerator() throws IllegalStateException {
        if (classStack.isEmpty())
            throw new IllegalStateException("no class on stack, cannot add instructions");
        return classStack.peek();
    }

    @Override
    public IClassGenerator getClassGenerator() {
        if (classStack.isEmpty())
            throw new IllegalStateException("no class on stack, cannot add instructions");
        return classStack.peek();
    }

    public void closeClass() {
        if (classStack.isEmpty())
            throw new IllegalStateException("no class on stack, cannot close method");
        ClassGenerator gen = classStack.peek();
        gen.close();
        classStack.pop();
        classes.add(gen);
        System.out.println(gen.display());
    }

    public void closeMethod() {
        if (classStack.isEmpty())
            throw new IllegalStateException("no class on stack, cannot close method");
        classStack.peek().closeMethod();
    }

    public void close() {
        if (!classStack.isEmpty())
            throw new IllegalStateException("cannot close generator, some classes are still open");
    }

    @Override
    public void display(StringBuffer b) {
        b.append("-- ByteCodeGenerator2 Results --\n");
        for (ClassGenerator classGenerator : classes) {
            classGenerator.display(b);
            b.append('\n');
        }
        b.append("--- Molkars says goodbye ---");
    }

    public ClassLoader compile() {
        if (!classStack.isEmpty())
            throw new IllegalStateException("cannot compile generator, some classes are still open");
        DynamicClassLoader loader = new DynamicClassLoader();
        for (ClassGenerator classGenerator : classes) {
            byte[] bytes = classGenerator.generateBytecode();
            System.out.println(ClassGenerator.formatClassBytes(bytes));
            loader.defineClass(classGenerator.getType().getInternalName(), bytes);
        }

        return loader;
    }

    public <T> T compile(TypeRef.Generated type) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassLoader loader = compile();
        System.out.println("loading " + type.getInternalName() + " from " + loader);
        Class<?> clazz = loader.loadClass(type.getInternalName());
        return (T) clazz.getConstructor().newInstance();
    }

    public ClassGenerator addClass(String module, String name, TypeRef superType, TypeRef... interfaces) {
        if (superType == null || superType.isInterface()) {
            superType = context.getTypeFor(Object.class);
        }
        for (TypeRef interfaceType : interfaces) {
            if (!interfaceType.isInterface())
                throw new IllegalArgumentException("interface type expected");
        }

        ClassGenerator classGenerator = new ClassGenerator(context, module, name, superType, interfaces);
        classStack.push(classGenerator);
        return classGenerator;
    }

    public ClassGenerator addClass(String module, String name, Class<?> superType, Class<?>... interfaces) {
        TypeRef superTypeRef = context.getTypeFor(superType);
        TypeRef[] interfaceRefs = Essentials.arrayMap(interfaces, context::getTypeFor, TypeRef[]::new);
        return addClass(module, name, superTypeRef, interfaceRefs);
    }

    public ClassGenerator addClass(String module, String name) {
        return addClass(module, name, context.getTypeFor(Object.class));
    }

    public MethodGenerator2 addConstructor(Class<?>... arguments) {
        return addMethod("<init>", void.class, arguments);
    }

    public MethodGenerator2 addConstructor(TypeRef... arguments) {
        return addMethod("<init>", context.getTypeFor(void.class), arguments);
    }

    public MethodGenerator2 addConstructor() {
        return addMethod("<init>", context.getTypeFor(void.class));
    }

    @Override
    public String toString() {
        if (classStack.isEmpty()) return "<no active class>";
        if (classStack.peek().methodStack.isEmpty()) return "<no active method>";
        Stack<TypeRef> stack = classStack.peek().methodStack.peek().builder.typeStack;
        if (stack.isEmpty()) return "<empty stack>";
        return "stack: " + stack.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    public TypeRef getType(String module, String name) {
        for (ClassGenerator classGenerator : classes) {
            var type = classGenerator.getType();
            if (type.getModule().equals(module) && type.getTypeName().equals(name))
                return type;
        }
        for (ClassGenerator classGenerator : classStack) {
            var type = classGenerator.getType();
            if (type.getModule().equals(module) && type.getTypeName().equals(name))
                return type;
        }
        return null;
    }

    public TypeRef getType(Class<?> clazz) {
        return context.getTypeFor(clazz);
    }

    public TypeRef getType(String lex) {
        for (var clazz : classes) {
            if (clazz.getType().getTypeName().equals(lex))
                return clazz.getType();
        }
        for (var clazz : classStack) {
            if (clazz.getType().getTypeName().equals(lex))
                return clazz.getType();
        }
        throw new IllegalArgumentException("unknown type: " + lex);
    }

    public void focusClass(TypeRef.Generated generated) {
        for (var clazz : classes) {
            if (clazz.getType().equals(generated)) {
                classStack.push(clazz);
                return;
            }
        }
        for (var clazz : classStack) {
            if (clazz.getType().equals(generated)) {
                classStack.push(clazz);
                return;
            }
        }
        throw new IllegalArgumentException("unknown type: " + generated);
    }

    private static class DynamicClassLoader extends ClassLoader {
        public void defineClass(String name, byte[] bytes) {
            defineClass(name, bytes, 0, bytes.length);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }
}
