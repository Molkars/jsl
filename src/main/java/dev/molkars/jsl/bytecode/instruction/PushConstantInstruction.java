package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.Essentials;
import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class PushConstantInstruction implements Instruction {
    Object value;

    public PushConstantInstruction(Object value) {
        this.value = value;
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        TypeRef type = switch (value) {
            case Integer i -> context.getTypeFor(int.class);
            case Double v -> context.getTypeFor(double.class);
            case String s -> context.getTypeFor(String.class);
            case Boolean b -> context.getTypeFor(boolean.class);
            case Long l -> context.getTypeFor(long.class);
            case Float v -> context.getTypeFor(float.class);
            case null, default -> throw new IllegalStateException("Unknown constant type " + value.getClass());
        };
        return new TypeRef[]{type};
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(value);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("PUSH ").append(Essentials.debug(value));
    }
}
