package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class NewInstruction implements Instruction {
    final Object type;

    public NewInstruction(TypeRef type) {
        this.type = type;
    }

    public NewInstruction(Class<?> type) {
        this.type = type;
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        return new TypeRef[0];
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        TypeRef ref;
        if (type instanceof TypeRef other)
            ref = other;
        else
            ref = context.getTypeFor((Class<?>) type);
        return new TypeRef[]{ref};
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        TypeRef type;
        if (this.type instanceof TypeRef other)
            type = other;
        else
            type = context.getTypeFor((Class<?>) this.type);
        methodVisitor.visitTypeInsn(Opcodes.NEW, type.getQualifiedName());
    }

    @Override
    public void display(StringBuffer b) {
        b.append("NEW ");
        if (type instanceof TypeRef other)
            b.append(other.getInternalName());
        else
            b.append(((Class<?>) type).getName());
    }
}
