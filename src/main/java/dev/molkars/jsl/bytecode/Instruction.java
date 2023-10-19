package dev.molkars.jsl.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public interface Instruction extends Display {
    TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack);

    TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack);

    default boolean isTerminal() {
        return false;
    }

    void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor);

    default Label getJumpTargetLabel() {
        return null;
    }
}
