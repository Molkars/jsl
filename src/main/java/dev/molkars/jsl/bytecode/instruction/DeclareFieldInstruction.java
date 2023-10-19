package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class DeclareFieldInstruction implements Instruction {
    final TypeRef owner;
    final TypeRef type;
    final String name;

    public DeclareFieldInstruction(TypeRef owner, TypeRef type, String name) {
        this.owner = owner;
        this.type = type;
        this.name = name;
    }

    @Override
    public void display(StringBuffer b) {
        b.append("DECLARE FIELD ");
        owner.display(b);
        b.append(".").append(name);
        b.append(" : ");
        type.display(b);
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
        classVisitor.visitField(Opcodes.ACC_PUBLIC, name, type.getDescriptor(), null, null);
    }
}
