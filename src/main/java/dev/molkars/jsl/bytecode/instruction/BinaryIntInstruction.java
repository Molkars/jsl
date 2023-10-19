package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class BinaryIntInstruction implements Instruction {
    final int opcode;

    private BinaryIntInstruction(int opcode) {
        this.opcode = opcode;
    }

    public static BinaryIntInstruction add() {
        return new BinaryIntInstruction(Opcodes.IADD);
    }

    public static BinaryIntInstruction sub() {
        return new BinaryIntInstruction(Opcodes.ISUB);
    }

    public static BinaryIntInstruction mul() {
        return new BinaryIntInstruction(Opcodes.IMUL);
    }

    public static BinaryIntInstruction div() {
        return new BinaryIntInstruction(Opcodes.IDIV);
    }

    public static BinaryIntInstruction rem() {
        return new BinaryIntInstruction(Opcodes.IREM);
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        TypeRef intType = context.getTypeFor(int.class);
        return new TypeRef[]{intType, intType};
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        TypeRef intType = context.getTypeFor(int.class);
        return new TypeRef[]{intType};
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(opcode);
    }

    @Override
    public void display(StringBuffer b) {
        switch (opcode) {
            case Opcodes.IADD -> b.append("ADD");
            case Opcodes.ISUB -> b.append("SUB");
            case Opcodes.IMUL -> b.append("MUL");
            case Opcodes.IDIV -> b.append("DIV");
            case Opcodes.IREM -> b.append("REM");
        }
        b.append(" int, int -> int");
    }
}
