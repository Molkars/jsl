package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class CallInstruction implements Instruction {
    private int opcode;
    private boolean isStatic;
    private String methodName;
    private Object owner;
    private Object resultType;
    private Object[] argumentTypes;

    private CallInstruction() {
    }

    public static CallInstruction method(TypeRef owner, String methodName, TypeRef resultType, TypeRef... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKEVIRTUAL;
        instruction.isStatic = false;
        instruction.owner = owner;
        instruction.methodName = methodName;
        instruction.resultType = resultType;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    public static CallInstruction method(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKEVIRTUAL;
        instruction.isStatic = false;
        instruction.owner = owner;
        instruction.methodName = methodName;
        instruction.resultType = resultType;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    public static CallInstruction staticMethod(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKESTATIC;
        instruction.isStatic = true;
        instruction.owner = owner;
        instruction.methodName = methodName;
        instruction.resultType = resultType;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    public static CallInstruction interfaceMethod(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKEINTERFACE;
        instruction.isStatic = false;
        instruction.owner = owner;
        instruction.methodName = methodName;
        instruction.resultType = resultType;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    public static CallInstruction constructor(Class<?> owner, Class<?>... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKESPECIAL;
        instruction.isStatic = false;
        instruction.owner = owner;
        instruction.methodName = "<init>";
        instruction.resultType = null;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    public static CallInstruction constructor(TypeRef owner, TypeRef... argumentTypes) {
        CallInstruction instruction = new CallInstruction();
        instruction.opcode = Opcodes.INVOKESPECIAL;
        instruction.isStatic = false;
        instruction.owner = owner;
        instruction.methodName = "<init>";
        instruction.resultType = null;
        instruction.argumentTypes = argumentTypes;
        return instruction;
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        int length = argumentTypes.length;
        if (!isStatic) length += 1;

        int index = 0;
        TypeRef[] out = new TypeRef[length];
        if (!isStatic) {
            if (owner instanceof TypeRef typeRef) {
                out[0] = typeRef;
            } else {
                out[0] = context.getTypeFor((Class<?>) owner);
            }
            index += 1;
        }

        for (Object arg : argumentTypes) {
            TypeRef type;
            if (arg instanceof TypeRef ref) {
                type = ref;
            } else {
                type = context.getTypeFor((Class<?>) arg);
            }
            out[index] = type;
            index += 1;
        }
        return out;
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        if (resultType == null) return new TypeRef[0];

        TypeRef type;
        if (resultType instanceof TypeRef typeRef) type = typeRef;
        else type = context.getTypeFor((Class<?>) resultType);

        if (type.isVoid()) return new TypeRef[0];
        return new TypeRef[]{type};
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        StringBuilder descriptor = new StringBuilder("(");
        for (Object argumentType : argumentTypes) {
            if (argumentType instanceof TypeRef ref) {
                descriptor.append(ref.getDescriptor());
            } else {
                descriptor.append(context.getTypeFor((Class<?>) argumentType).getDescriptor());
            }
        }
        descriptor.append(")");
        if (resultType == null) {
            descriptor.append("V");
        } else if (resultType instanceof TypeRef ref) {
            descriptor.append(ref.getDescriptor());
        } else {
            descriptor.append(context.getTypeFor((Class<?>) resultType).getDescriptor());
        }
        TypeRef owner;
        if (this.owner instanceof TypeRef typeRef) {
            owner = typeRef;
        } else {
            owner = context.getTypeFor((Class<?>) this.owner);
        }
        methodVisitor.visitMethodInsn(opcode, owner.getQualifiedName(), methodName, descriptor.toString(), opcode == Opcodes.INVOKEINTERFACE);
    }

    @Override
    public void display(StringBuffer b) {
        b
                .append("INVOKE ")
                .append(TypeRef.formatType(owner))
                .append(".")
                .append(methodName);
        for (Object argumentType : argumentTypes) {
            b.append(" ").append(TypeRef.formatType(argumentType));
        }
        b.append(" -> ").append(resultType == null ? "void" : TypeRef.formatType(resultType));

    }
}
