package dev.molkars.jsl.bytecode;

public interface MethodGeneratorDelegate extends MethodGeneratorFacade {
    MethodGeneratorFacade getMethodGenerator();

    @Override
    default boolean hasReturned() {
        return getMethodGenerator().hasReturned();
    }

    @Override
    default void addPushConstantInstruction(String value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushConstantInstruction(int value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushConstantInstruction(float value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushConstantInstruction(double value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushConstantInstruction(boolean value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushConstantInstruction(long value) {
        getMethodGenerator().addPushConstantInstruction(value);
    }

    @Override
    default void addPushNullInstruction() {
        getMethodGenerator().addPushNullInstruction();
    }


    @Override
    default void addIntAddInstruction() {
        getMethodGenerator().addIntAddInstruction();
    }

    @Override
    default void addIntSubInstruction() {
        getMethodGenerator().addIntSubInstruction();
    }

    @Override
    default void addIntMulInstruction() {
        getMethodGenerator().addIntMulInstruction();
    }

    @Override
    default void addIntDivInstruction() {
        getMethodGenerator().addIntDivInstruction();
    }

    @Override
    default void addIntRemInstruction() {
        getMethodGenerator().addIntRemInstruction();
    }

    @Override
    default void addIfNeInstruction(Label label) {
        getMethodGenerator().addIfNeInstruction(label);
    }

    @Override
    default void addGotoInstruction(Label label) {
        getMethodGenerator().addGotoInstruction(label);
    }

    @Override
    default void addCallInstruction(TypeRef owner, String methodName, TypeRef resultTypeRef, TypeRef... argumentTypeRefs) {
        getMethodGenerator().addCallInstruction(owner, methodName, resultTypeRef, argumentTypeRefs);
    }

    @Override
    default void addCallInstruction(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        getMethodGenerator().addCallInstruction(owner, methodName, resultType, argumentTypes);
    }

    @Override
    default void addCallInstruction(String methodName, Class<?> resultType, Class<?>... argumentTypes) {
        getMethodGenerator().addCallInstruction(methodName, resultType, argumentTypes);
    }

    @Override
    default void addConstructorCallInstruction(Class<?> clazz, Class<?>... arguments) {
        getMethodGenerator().addConstructorCallInstruction(clazz, arguments);
    }

    @Override
    default void addConstructorCallInstruction(TypeRef clazz, TypeRef... arguments) {
        getMethodGenerator().addConstructorCallInstruction(clazz, arguments);
    }


    @Override
    default void addReturnInstruction() {
        getMethodGenerator().addReturnInstruction();
    }

    @Override
    default void addIntReturnInstruction() {
        getMethodGenerator().addIntReturnInstruction();
    }

    @Override
    default void addObjectReturnInstruction() {
        getMethodGenerator().addObjectReturnInstruction();
    }

    @Override
    default void addLoadClassPointerInstruction() {
        getMethodGenerator().addLoadClassPointerInstruction();
    }


    @Override
    default void addIntNegInstruction() {
        getMethodGenerator().addIntNegInstruction();
    }

    @Override
    default void addNewInstruction(Class<?> clazz) {
        getMethodGenerator().addNewInstruction(clazz);
    }

    @Override
    default void addNewInstruction(TypeRef clazz) {
        getMethodGenerator().addNewInstruction(clazz);
    }

    @Override
    default void addDuplicateInstruction() {
        getMethodGenerator().addDuplicateInstruction();
    }

    @Override
    default void addPopInstruction() {
        getMethodGenerator().addPopInstruction();
    }

    @Override
    default int createUnnamedSlot(TypeRef type) {
        return getMethodGenerator().createUnnamedSlot(type);
    }

    @Override
    default int createUnnamedSlot(Class<?> type) {
        return getMethodGenerator().createUnnamedSlot(type);
    }

    @Override
    default int createNamedSlot(String name, TypeRef type) {
        return getMethodGenerator().createNamedSlot(name, type);
    }

    @Override
    default int createNamedSlot(String name, Class<?> type) {
        return getMethodGenerator().createNamedSlot(name, type);
    }

    @Override
    default Integer getSlot(String name) {
        return getMethodGenerator().getSlot(name);
    }

    @Override
    default TypeRef getSlotType(String name) {
        return getMethodGenerator().getSlotType(name);
    }

    @Override
    default TypeRef getSlotType(int slot) {
        return getMethodGenerator().getSlotType(slot);
    }

    @Override
    default Label createLabel(String name) {
        return getMethodGenerator().createLabel(name);
    }

    @Override
    default void useLabel(Label label) {
        getMethodGenerator().useLabel(label);
    }

    @Override
    default void addObjectEqInstruction(Label ifTrue) {
        getMethodGenerator().addObjectEqInstruction(ifTrue);
    }

    @Override
    default void addObjectNeInstruction(Label ifTrue) {
        getMethodGenerator().addObjectNeInstruction(ifTrue);
    }

    @Override
    default void addIntCmpLtInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpLtInstruction(ifTrue);
    }

    @Override
    default void addIntCmpLeInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpLeInstruction(ifTrue);
    }

    @Override
    default void addIntCmpGtInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpGtInstruction(ifTrue);
    }

    @Override
    default void addIntCmpGeInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpGeInstruction(ifTrue);
    }

    @Override
    default void addStoreInstruction(int slot) {
        getMethodGenerator().addStoreInstruction(slot);
    }

    @Override
    default void addLoadInstruction(int slot) {
        getMethodGenerator().addLoadInstruction(slot);
    }

    @Override
    default void addIntCmpNeInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpNeInstruction(ifTrue);
    }

    @Override
    default void addIntCmpEqInstruction(Label ifTrue) {
        getMethodGenerator().addIntCmpEqInstruction(ifTrue);
    }

    @Override
    default void addCheckcastInstruction(Class<?> type) {
        getMethodGenerator().addCheckcastInstruction(type);
    }

    @Override
    default void addCheckcastInstruction(TypeRef type) {
        getMethodGenerator().addCheckcastInstruction(type);
    }

    @Override
    default void debugSlots() {
        getMethodGenerator().debugSlots();
    }

    @Override
    default void addGetFieldInstruction(String name) {
        getMethodGenerator().addGetFieldInstruction(name);
    }

    @Override
    default void addGetFieldInstruction(Class<?> clazz, String name) {
        getMethodGenerator().addGetFieldInstruction(clazz, name);
    }

    @Override
    default void addGetFieldInstruction(TypeRef type, String name) {
        getMethodGenerator().addGetFieldInstruction(type, name);
    }

    @Override
    default void addPutFieldInstruction(String name) {
        getMethodGenerator().addPutFieldInstruction(name);
    }

    @Override
    default boolean hasSlot(String name) {
        return getMethodGenerator().hasSlot(name);
    }

    default TypeRef getStackElement(int i) {
        return getMethodGenerator().getStackElement(i);
    }
}
