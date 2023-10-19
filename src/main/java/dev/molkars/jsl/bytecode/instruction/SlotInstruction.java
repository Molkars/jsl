package dev.molkars.jsl.bytecode.instruction;

import dev.molkars.jsl.bytecode.BytecodeContext;
import dev.molkars.jsl.bytecode.Instruction;
import dev.molkars.jsl.bytecode.TypeRef;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

public class SlotInstruction implements Instruction {
    final TypeRef typeRef;
    final int slot;
    final boolean load;

    SlotInstruction(TypeRef typeRef, int slot, boolean load) {
        this.typeRef = typeRef;
        this.slot = slot;
        this.load = load;
    }

    public static SlotInstruction store(TypeRef ty, int slot) {
        return new SlotInstruction(ty, slot, false);
    }

    public static SlotInstruction load(TypeRef ty, int slot) {
        return new SlotInstruction(ty, slot, true);
    }

    @Override
    public void display(StringBuffer b) {
        if (load) b.append("LOAD ").append(slot).append(" -> ");
        else b.append("STORE ").append(slot).append(" <- ");
        typeRef.display(b);
    }

    @Override
    public TypeRef[] consumes(BytecodeContext context, Stack<TypeRef> stack) {
        if (load) {
            return new TypeRef[0];
        } else {
            return new TypeRef[]{typeRef};
        }
    }

    @Override
    public TypeRef[] produces(BytecodeContext context, Stack<TypeRef> stack) {
        if (load) {
            return new TypeRef[]{typeRef};
        } else {
            return new TypeRef[0];
        }
    }

    @Override
    public void compile(BytecodeContext context, ClassVisitor classVisitor, MethodVisitor methodVisitor) {
        if (load) {
            methodVisitor.visitVarInsn(typeRef.getLoadOpcode(), slot);
        } else {
            methodVisitor.visitVarInsn(typeRef.getStoreOpcode(), slot);
        }
    }
}
