package dev.molkars.jsl.bytecode;

import dev.molkars.jsl.bytecode.instruction.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class MethodGenerator2 implements MethodGeneratorFacade, Display {
    final BytecodeContext context;
    final TypeRef.Generated classType;
    final boolean isStatic;
    final String methodName;
    final TypeRef returnTypeRef;
    final TypeRef[] argumentTypeRefs;
    final IClassGenerator classGenerator;
    final InstructionBuilder builder;
    final SlotProvider slots;

    MethodGenerator2(BytecodeContext context, TypeRef.Generated classType, boolean isStatic, String name,
                     TypeRef returnTypeRef, TypeRef[] argumentTypeRefs, IClassGenerator generator) {
        this.context = context;
        this.builder = new InstructionBuilder(context);
        this.classType = classType;
        this.isStatic = isStatic;
        this.methodName = name;
        this.returnTypeRef = returnTypeRef;
        this.argumentTypeRefs = argumentTypeRefs;
        this.slots = new SlotProvider();
        if (!isStatic)
            slots.createUnnamedSlot(classType);
        for (TypeRef argumentTypeRef : argumentTypeRefs) {
            slots.createUnnamedSlot(argumentTypeRef);
        }
        this.classGenerator = generator;
    }

    @Override
    public boolean hasReturned() {
        return builder.hasReturned();
    }

    public void close() {
        if (!hasReturned())
            throw new IllegalStateException("method is not terminated, cannot close method");
    }

    public void compile(ClassWriter classWriter) {
        if (!hasReturned())
            throw new IllegalStateException("method is not terminated, cannot compile method");

        int access = Opcodes.ACC_PUBLIC;
        if (isStatic) access |= Opcodes.ACC_STATIC;
        StringBuilder descriptor = new StringBuilder("(");
        for (TypeRef argumentTypeRef : argumentTypeRefs) {
            descriptor.append(argumentTypeRef.getDescriptor());
        }
        descriptor.append(")").append(returnTypeRef.getDescriptor());

        var methodWriter = classWriter.visitMethod(access, methodName, descriptor.toString(), null, null);
        methodWriter.visitCode();
        for (Instruction inst : builder.getInstructions())
            inst.compile(context, classWriter, methodWriter);
        methodWriter.visitMaxs(0, 0);
        methodWriter.visitEnd();
    }

    void addInstruction(Instruction instruction) {
        builder.addInstruction(instruction);
    }

    @Override
    public void addPushConstantInstruction(String value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushConstantInstruction(int value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushConstantInstruction(float value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushConstantInstruction(double value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushConstantInstruction(boolean value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushConstantInstruction(long value) {
        addInstruction(new PushConstantInstruction(value));
    }

    @Override
    public void addPushNullInstruction() {
        addInstruction(new PushNullInstruction());
    }

    @Override
    public void addIntAddInstruction() {
        addInstruction(BinaryIntInstruction.add());
    }

    @Override
    public void addIntSubInstruction() {
        addInstruction(BinaryIntInstruction.sub());
    }

    @Override
    public void addIntMulInstruction() {
        addInstruction(BinaryIntInstruction.mul());
    }

    @Override
    public void addIntDivInstruction() {
        addInstruction(BinaryIntInstruction.div());
    }

    @Override
    public void addIntRemInstruction() {
        addInstruction(BinaryIntInstruction.rem());
    }

    @Override
    public void addIfNeInstruction(Label label) {
        addInstruction(ComparisonInstruction.ifne(label));
    }

    @Override
    public void addGotoInstruction(Label label) {
        addInstruction(ComparisonInstruction.goto_(label));
    }

    @Override
    public void addCallInstruction(TypeRef owner, String methodName, TypeRef resultTypeRef, TypeRef... argumentTypeRefs) {
        addInstruction(CallInstruction.method(owner, methodName, resultTypeRef, argumentTypeRefs));
    }

    @Override
    public void addCallInstruction(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        Method method;
        try {
            method = owner.getMethod(methodName, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No such method " + methodName + " on " + owner);
        }

        if (!method.getReturnType().isAssignableFrom(resultType))
            throw new RuntimeException("Method " + methodName + " on " + owner + " does not return " + resultType);

        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (!isStatic) {
            boolean isInterface = Modifier.isInterface(owner.getModifiers());
            if (isInterface) {
                addInstruction(CallInstruction.interfaceMethod(owner, methodName, resultType, argumentTypes));
            } else {
                addInstruction(CallInstruction.method(owner, methodName, resultType, argumentTypes));
            }
        } else
            addInstruction(CallInstruction.staticMethod(owner, methodName, resultType, argumentTypes));
    }

    @Override
    public void addCallInstruction(String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        TypeRef resultTypeRef = context.getTypeFor(resultType);
        TypeRef[] argumentTypeRefs = new TypeRef[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++)
            argumentTypeRefs[i] = context.getTypeFor(argumentTypes[i]);
        addInstruction(CallInstruction.method(classType, methodName, resultTypeRef, argumentTypeRefs));
    }

    @Override
    public void addConstructorCallInstruction(Class<?> clazz, Class<?>... arguments) {
        addInstruction(CallInstruction.constructor(clazz, arguments));
    }

    @Override
    public void addConstructorCallInstruction(TypeRef clazz, TypeRef... arguments) {
        addInstruction(CallInstruction.constructor(clazz, arguments));
    }

    @Override
    public void addReturnInstruction() {
        addInstruction(new ReturnInstruction(returnTypeRef));
    }

    @Override
    public void addIntReturnInstruction() {
        addInstruction(new ReturnInstruction(returnTypeRef));
    }

    @Override
    public void addObjectReturnInstruction() {
        addInstruction(new ReturnInstruction(returnTypeRef));
    }

    @Override
    public void addLoadClassPointerInstruction() {
        if (isStatic)
            throw new IllegalStateException("cannot load this pointer in a static method");

        addInstruction(LoadInstruction.aload(0, classType));
    }

    @Override
    public void addIntNegInstruction() {
        addInstruction(UnaryIntInstruction.negate());
    }

    @Override
    public void addNewInstruction(Class<?> clazz) {
        addInstruction(new NewInstruction(clazz));
    }

    @Override
    public void addNewInstruction(TypeRef clazz) {
        addInstruction(new NewInstruction(clazz));
    }

    @Override
    public void addDuplicateInstruction() {
        addInstruction(new DuplicateInstruction());
    }

    @Override
    public void addPopInstruction() {
        addInstruction(new PopInstruction());
    }

    @Override
    public int createUnnamedSlot(TypeRef type) {
        return slots.createUnnamedSlot(type);
    }

    @Override
    public int createUnnamedSlot(Class<?> type) {
        return slots.createUnnamedSlot(context.getTypeFor(type));
    }

    @Override
    public int createNamedSlot(String name, TypeRef type) {
        return slots.createNamedSlot(name, type);
    }

    @Override
    public int createNamedSlot(String name, Class<?> type) {
        return slots.createNamedSlot(name, context.getTypeFor(type));
    }

    @Override
    public Integer getSlot(String name) {
        return slots.getSlot(name);
    }

    @Override
    public TypeRef getSlotType(String name) {
        return slots.getSlotType(name);
    }

    @Override
    public TypeRef getSlotType(int slot) {
        return slots.getSlotType(slot);
    }

    @Override
    public Label createLabel(String name) {
        return builder.createLabel(name);
    }

    @Override
    public void useLabel(Label label) {
        builder.setLabel(label);
    }

    @Override
    public void addObjectEqInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.objectEq(ifTrue));
    }

    @Override
    public void addObjectNeInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.objectNe(ifTrue));
    }

    @Override
    public void addIntCmpLtInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intLt(ifTrue));
    }

    @Override
    public void addIntCmpLeInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intLe(ifTrue));
    }

    @Override
    public void addIntCmpGtInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intGt(ifTrue));
    }

    @Override
    public void addIntCmpGeInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intGe(ifTrue));
    }

    @Override
    public void addStoreInstruction(int slot) {
        TypeRef slotType = slots.getSlotType(slot);
        if (slotType == null) {
            throw new IllegalStateException("no slot %d".formatted(slot));
        }
        addInstruction(SlotInstruction.store(slotType, slot));
    }

    @Override
    public void addLoadInstruction(int slot) {
        TypeRef slotType = slots.getSlotType(slot);
        if (slotType == null) {
            throw new IllegalStateException("no slot %d".formatted(slot));
        }
        addInstruction(SlotInstruction.load(slotType, slot));
    }

    @Override
    public void addIntCmpNeInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intNe(ifTrue));
    }

    @Override
    public void addIntCmpEqInstruction(Label ifTrue) {
        addInstruction(ComparisonInstruction.intEq(ifTrue));
    }

    @Override
    public void addCheckcastInstruction(Class<?> type) {
        addCheckcastInstruction(context.getTypeFor(type));
    }

    @Override
    public void addCheckcastInstruction(TypeRef type) {
        addInstruction(new CheckcastInstruction(type));
    }

    @Override
    public void debugSlots() {
        HashMap<Integer, String> reverseNames = new HashMap<>();
        slots.namedSlots.forEach((name, slot) -> reverseNames.put(slot, name));
        StringBuilder message = new StringBuilder();
        message.append("slots:\n");
        for (int i = 0; i < slots.slots.size(); i++) {
            message.append("    ");
            message.append(i);
            message.append(": ");
            if (reverseNames.containsKey(i)) {
                message
                        .append('"')
                        .append(reverseNames.get(i))
                        .append('"')
                        .append(' ');
            }
            message.append(slots.slots.get(i).getInternalName());
            message.append("\n");
        }
        System.out.println(message);
    }

    @Override
    public void addGetFieldInstruction(String name) {
        var fields = classGenerator.getFields();
        if (!fields.containsKey(name)) {
            throw new IllegalStateException("no field %s".formatted(name));
        }
        addInstruction(FieldInstruction.get(name, classType, fields.get(name)));
    }

    @Override
    public void addGetFieldInstruction(Class<?> clazz, String name) {
        Field field;
        try {
            field = clazz.getField(name);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("no field %s".formatted(name));
        }

        addInstruction(FieldInstruction.get(name, context.getTypeFor(clazz), context.getTypeFor(field.getType())));
    }

    @Override
    public void addGetFieldInstruction(TypeRef type, String name) {
        var fields = classGenerator.getFields();
        if (!fields.containsKey(name)) {
            throw new IllegalStateException("no field %s".formatted(name));
        }
        addInstruction(FieldInstruction.get(name, type, fields.get(name)));
    }

    @Override
    public void addPutFieldInstruction(String name) {
        var fields = classGenerator.getFields();
        if (!fields.containsKey(name)) {
            throw new IllegalStateException("no field %s".formatted(name));
        }
        addInstruction(FieldInstruction.put(name, classType, fields.get(name)));
    }

    @Override
    public boolean hasSlot(String name) {
        return slots.namedSlots.containsKey(name);
    }

    @Override
    public void display(StringBuffer b) {
        b.append("method ");
        b.append(methodName);

        if (argumentTypeRefs.length != 0) {
            b.append("(");
            for (int i = 0; i < argumentTypeRefs.length; i++) {
                if (i != 0) b.append(", ");
                argumentTypeRefs[i].display(b);
            }
            b.append(")");
        }

        b.append(" -> ");
        returnTypeRef.display(b);
        b.append(" {\n");

        for (Instruction instruction : builder.getInstructions()) {
            b.append("    ");
            instruction.display(b);
            b.append("\n");
        }
        b.append("}\n");
    }
}
