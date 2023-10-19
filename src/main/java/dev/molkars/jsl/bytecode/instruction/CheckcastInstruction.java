package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class CheckcastInstruction implements Instruction {
    final TypeRef type;

    public CheckcastInstruction(TypeRef type) {
        this.type = type;
    }

    @Override
    public void display(StringBuffer b) {
        b.append("CHECKCAST ");
        type.display(b);
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("cannot checkcast empty stack");
        }
        TypeRef type = stack.peek();
        return new TypeRef[]{type};
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[]{type};
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, type.getStandaloneDescriptor());
    }
}
