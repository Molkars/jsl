package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.Label;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Stack;

public class ComparisonInstruction implements Instruction {
    final int opcode;
    final int operands;
    final boolean primitives;
    final Label target;

    private ComparisonInstruction(int opcode, int operands, boolean primitives, Label target) {
        this.opcode = opcode;
        this.operands = operands;
        this.primitives = primitives;
        this.target = target;
    }

    public static ComparisonInstruction ifne(Label target) {
        return new ComparisonInstruction(Opcodes.IFNE, 1, true, target);
    }

    public static ComparisonInstruction goto_(Label target) {
        return new ComparisonInstruction(Opcodes.GOTO, 0, true, target);
    }

    public static ComparisonInstruction objectEq(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ACMPEQ, 2, false, target);
    }

    public static ComparisonInstruction objectNe(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ACMPNE, 2, false, target);
    }


    public static ComparisonInstruction intLt(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPLT, 2, true, target);
    }

    public static ComparisonInstruction intLe(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPLE, 2, true, target);
    }

    public static ComparisonInstruction intGt(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPGT, 2, true, target);
    }

    public static ComparisonInstruction intGe(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPGE, 2, true, target);
    }

    public static ComparisonInstruction intEq(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPEQ, 2, true, target);
    }

    public static ComparisonInstruction intNe(Label target) {
        return new ComparisonInstruction(Opcodes.IF_ICMPNE, 2, true, target);
    }

    @Override
    public void display(StringBuffer b) {
        switch (opcode) {
            case Opcodes.IFNE -> b.append("IFNE");
            case Opcodes.GOTO -> b.append("GOTO");
            case Opcodes.IF_ACMPEQ -> b.append("IF_ACMPEQ");
            case Opcodes.IF_ACMPNE -> b.append("IF_ACMPNE");
            case Opcodes.IF_ICMPLT -> b.append("IF_ICMPLT");
            case Opcodes.IF_ICMPLE -> b.append("IF_ICMPLE");
            case Opcodes.IF_ICMPGT -> b.append("IF_ICMPGT");
            case Opcodes.IF_ICMPGE -> b.append("IF_ICMPGE");
            case Opcodes.IF_ICMPEQ -> b.append("IF_ICMPEQ");
            case Opcodes.IF_ICMPNE -> b.append("IF_ICMPNE");
            default -> throw new RuntimeException("Invalid opcode for comparison instruction");
        }
        if (primitives) {
            for (int i = 0; i < operands; i++) {
                if (i != 0) b.append(" ");
                b.append("int");
            }
        } else {
            for (int i = 0; i < operands; i++) {
                if (i != 0) b.append(" ");
                b.append("j.l.Object");
            }
        }
        b.append(" -> int");
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        TypeRef[] out = new TypeRef[operands];
        TypeRef value = context.getTypeFor(primitives ? int.class : Object.class);
        Arrays.fill(out, value);
        return out;
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public boolean isTerminal() {
        return opcode == Opcodes.GOTO;
    }

    @Override
    public dev.molkars.jsl.bytecode.Label getJumpTargetLabel() {
        return target;
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitJumpInsn(opcode, target.getInner());
    }
}
