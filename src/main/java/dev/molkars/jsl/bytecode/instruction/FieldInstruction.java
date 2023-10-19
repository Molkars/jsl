package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Stack;

public class FieldInstruction implements Instruction {
    final int opcode;
    final String name;
    final TypeRef classType;
    final TypeRef fieldType;

    FieldInstruction(int opcode, String name, TypeRef classType, TypeRef fieldType) {
        this.opcode = opcode;
        this.name = name;
        this.classType = classType;
        this.fieldType = fieldType;
    }

    public static FieldInstruction get(String name, TypeRef classType, TypeRef fieldType) {
        return new FieldInstruction(Opcodes.GETFIELD, name, classType, fieldType);
    }

    public static FieldInstruction put(String name, TypeRef classType, TypeRef fieldType) {
        return new FieldInstruction(Opcodes.PUTFIELD, name, classType, fieldType);
    }

    @Override
    public void display(StringBuffer b) {
        if (opcode == Opcodes.PUTFIELD) {
            b.append("PUTFIELD ").append(classType).append(".").append(name).append(" : ").append(fieldType);
        } else {
            b.append("GETFIELD ").append(classType).append(".").append(name).append(" : ").append(fieldType);
        }
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        if (opcode == Opcodes.PUTFIELD) {
            return new TypeRef[]{classType, fieldType};
        } else {
            return new TypeRef[]{classType};
        }
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        if (opcode == Opcodes.PUTFIELD) {
            return new TypeRef[0];
        } else {
            return new TypeRef[]{fieldType};
        }
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(opcode, classType.getStandaloneDescriptor(), name, fieldType.getDescriptor());
    }
}
