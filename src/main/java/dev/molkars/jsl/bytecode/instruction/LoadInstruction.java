package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class LoadInstruction implements Instruction {
    final int opcode;
    final int slot;
    final Object type;

    private LoadInstruction(int opcode, int slot, TypeRef type) {
        this.opcode = opcode;
        this.slot = slot;
        this.type = type;
    }

    public static LoadInstruction aload(int slot, TypeRef type) {
        return new LoadInstruction(Opcodes.ALOAD, slot, type);
    }

    public static LoadInstruction iload(int slot, TypeRef type) {
        return new LoadInstruction(Opcodes.ILOAD, slot, type);
    }

    public static LoadInstruction dload(int slot, TypeRef type) {
        return new LoadInstruction(Opcodes.DLOAD, slot, type);
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        if (type instanceof TypeRef ref) {
            return new TypeRef[]{ref};
        } else if (type instanceof Class<?> clazz) {
            return new TypeRef[]{context.getTypeFor(clazz)};
        } else {
            throw new IllegalStateException("Unknown type " + type);
        }
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(opcode, slot);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("LOAD ").append(slot).append(" -> ").append(type);
    }
}
