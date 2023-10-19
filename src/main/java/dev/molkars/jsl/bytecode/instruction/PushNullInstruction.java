package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class PushNullInstruction implements Instruction {
    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[]{context.getTypeFor(void.class)};
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(Opcodes.ACONST_NULL);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("PUSH null");
    }
}
