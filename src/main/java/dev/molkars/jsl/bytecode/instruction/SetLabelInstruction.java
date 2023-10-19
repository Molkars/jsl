package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.Label;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class SetLabelInstruction implements Instruction {
    final Label label;

    public SetLabelInstruction(Label label) {
        this.label = label;
    }

    @Override
    public void display(StringBuffer b) {
        b.append("label '").append(label.getName()).append("'");
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitLabel(label.getInner());
    }
}
