package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class ReturnInstruction implements Instruction {
    final TypeRef returnType;

    public ReturnInstruction(TypeRef returnType) {
        this.returnType = returnType;
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        if (returnType.isVoid()) return new TypeRef[0];
        else if (returnType.is(int.class) || returnType.is(boolean.class)) return new TypeRef[]{returnType};
        else return new TypeRef[]{returnType};
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        int opcode;
        if (returnType.isVoid()) opcode = Opcodes.RETURN;
        else if (returnType.is(int.class) || returnType.is(boolean.class)) opcode = Opcodes.IRETURN;
        else opcode = Opcodes.ARETURN;
        methodVisitor.visitInsn(opcode);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("RETURN");
    }
}
