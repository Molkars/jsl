package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class UnaryIntInstruction implements Instruction {
    final int opcode;

    private UnaryIntInstruction(int opcode) {
        this.opcode = opcode;
    }

    public static UnaryIntInstruction negate() {
        return new UnaryIntInstruction(Opcodes.INEG);
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        final TypeRef intType = context.getTypeFor(int.class);
        return new TypeRef[]{intType};
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        final TypeRef intType = context.getTypeFor(int.class);
        return new TypeRef[]{intType};
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(opcode);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("NEGATE int -> int");
    }
}
