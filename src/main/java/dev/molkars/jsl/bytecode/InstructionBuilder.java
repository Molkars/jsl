package dev.molkars.jsl.bytecode;

import dev.molkars.jsl.Essentials;
import dev.molkars.jsl.bytecode.instruction.SetLabelInstruction;

import java.util.*;

// you can only set a label if something jumps to it
// all jumpers must agree on a similar stack
// these two things enforce the fact that a block can only produce one stack and that stack is known
// since a label cannot be added until something jumps to it, we have a definite incoming stack
// if a block jumps to itself, then the stack must be empty otherwise we grow the heap to infinity
public class InstructionBuilder {
    final BytecodeContext context;
    final LinkedList<Instruction> instructions = new LinkedList<>();
    final HashSet<Label> usedLabels = new HashSet<>();
    final HashMap<Label, LinkedList<LabelStack>> labelStacks = new HashMap<>();
    Stack<TypeRef> typeStack = new Stack<>();
    Label label = null;

    public InstructionBuilder(BytecodeContext context) {
        this.context = context;
    }

    public void addInstruction(Instruction instruction) {
        if (hasReturned()) {
            if (instruction.isTerminal()) return;
            else throw new IllegalStateException("cannot add instruction: method has returned");
        }

        Label label = instruction.getJumpTargetLabel();
        if (label != null) {
            if (!labelStacks.containsKey(label))
                throw new IllegalStateException("cannot add instruction: jumps to foreign label");
            if (label == this.label && typeStack.size() != 0) {
                throw new IllegalStateException("cannot add instruction: jumps to same block with non-zero size stack - a recursive jump requires that the current stack be empty");
            }
        }

        TypeRef[] consumes = instruction.consumes(context, typeStack);
        TypeRef[] produces = instruction.produces(context, typeStack);

        for (TypeRef consume : Essentials.reverse(consumes)) {
            if (typeStack.isEmpty()) {
                throw new IllegalStateException("invalid instruction: empty stack, tried to consume %s".formatted(consume));
            }
            TypeRef type = typeStack.pop();
            if (type.isVoid()) {
                if (consume.isPrimitive()) {
                    throw new IllegalStateException("invalid instruction: tried to consume primitive, instead found null");
                }
            } else if (!type.isAssignableTo(consume)) {
                throw new IllegalStateException("invalid instruction: tried to consume %s, instead found %s".formatted(consume, type));
            }
        }

        if (label != null) {
            labelStacks.get(label).push(new LabelStack(this.label, typeStack.toArray(TypeRef[]::new)));
        }

        typeStack.addAll(Arrays.asList(produces));

        if (label == null && instruction.isTerminal() && typeStack.size() != 0) {
            // this is a return instruction!
            throw new IllegalStateException("cannot add instruction: this instruction returns/jumps absolutely but the stack is not empty!");
        }

        instructions.add(instruction);
    }

    public void setLabel(Label label) {
        if (labelStacks.get(label) == null)
            throw new IllegalStateException("cannot set label: it does not belong to this builder");
        if (!isTerminal())
            labelStacks.get(label).push(new LabelStack(this.label, typeStack.toArray(TypeRef[]::new)));
        if (usedLabels.contains(label))
            throw new IllegalStateException("cannot set label: the label has already been built");

        if (!labelStacks.get(label).isEmpty()) {
            LinkedList<LabelStack> incomingStacks = labelStacks.get(label);
            LinkedList<TypeRef> newStack = null;
            for (LabelStack stack : incomingStacks) {
                if (newStack == null) {
                    newStack = new LinkedList<>(Arrays.asList(stack.stack));
                } else if (stack.stack.length != newStack.size()) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("cannot set label: predecessors have inconsistent stacks!").append('\n');
                    builder.append("  incoming stack (from `").append(stack.label).append("`): ").append(Arrays.toString(stack.stack)).append('\n');
                    builder.append("  expected stack (from `").append(incomingStacks.getFirst().label).append("`): ").append(newStack).append('\n');
                    throw new IllegalStateException(builder.toString());
                } else {
                    int i = 0;
                    for (TypeRef top : newStack) {
                        TypeRef other = stack.stack[i];
                        if (!other.isAssignableTo(top)) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("cannot set label: predecessors have inconsistent stacks!").append('\n');
                            builder.append("  incoming stack (from `").append(stack.label).append("`): ").append(Arrays.toString(stack.stack)).append('\n');
                            builder.append("  expected stack (from `").append(incomingStacks.getFirst().label).append("`): ").append(newStack).append('\n');
                            throw new IllegalStateException(builder.toString());
                        }
                        i += 1;
                    }
                }
            }

            typeStack = new Stack<>();
            typeStack.addAll(newStack);
        }
        instructions.add(new SetLabelInstruction(label));

        this.label = label;
        usedLabels.add(label);
    }

    Instruction[] getInstructions() {
        return instructions.toArray(Instruction[]::new);
    }

    Label createLabel(String name) {
        String newName = name;
        for (int i = 0; labelStacks.containsKey(new Label(newName)); i++) {
            newName = "name.%d".formatted(i);
        }
        Label label = new Label(newName);
        labelStacks.put(label, new LinkedList<>());
        return label;
    }

    boolean hasReturned() {
        boolean labelIsUnvisited = label != null && labelStacks.get(label).isEmpty();
        return labelIsUnvisited || !instructions.isEmpty() && instructions.getLast().isTerminal() && instructions.getLast().getJumpTargetLabel() == null;
    }

    boolean isTerminal() {
        return !instructions.isEmpty() && instructions.getLast().isTerminal();
    }

    record LabelStack(Label label, TypeRef[] stack) {
    }
}
