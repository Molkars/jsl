package dev.molkars.jsl.bytecode;

public interface MethodGeneratorFacade {
    boolean hasReturned();

    void addPushConstantInstruction(String value);

    void addPushConstantInstruction(int value);

    void addPushConstantInstruction(float value);

    void addPushConstantInstruction(double value);

    void addPushConstantInstruction(boolean value);

    void addPushConstantInstruction(long value);

    void addPushNullInstruction();

    void addIntAddInstruction();

    void addIntSubInstruction();

    void addIntMulInstruction();

    void addIntDivInstruction();

    void addIntRemInstruction();

    void addIfNeInstruction(Label label);

    void addGotoInstruction(Label label);

    void addCallInstruction(TypeRef owner, String methodName, TypeRef resultTypeRef, TypeRef... argumentTypeRefs);

    void addCallInstruction(Class<?> owner, String methodName, Class<?> resultType, Class<?>... argumentTypes);

    /**
     * Adds an instruction to call this.{name}
     *
     * @param methodName    the name of the method to call
     * @param resultType    the type of the result
     * @param argumentTypes the types of the arguments
     */
    void addCallInstruction(String methodName, Class<?> resultType, Class<?>... argumentTypes);

    void addConstructorCallInstruction(Class<?> clazz, Class<?>... arguments);

    void addConstructorCallInstruction(TypeRef clazz, TypeRef... arguments);

    void addReturnInstruction();

    void addIntReturnInstruction();

    void addObjectReturnInstruction();

    void addLoadClassPointerInstruction();

    void addIntNegInstruction();

    void addNewInstruction(Class<?> clazz);

    void addNewInstruction(TypeRef clazz);

    void addDuplicateInstruction();

    void addPopInstruction();

    int createUnnamedSlot(TypeRef type);

    int createUnnamedSlot(Class<?> type);

    int createNamedSlot(String name, TypeRef type);

    int createNamedSlot(String name, Class<?> type);

    Integer getSlot(String name);

    TypeRef getSlotType(String name);

    TypeRef getSlotType(int slot);

    Label createLabel(String name);

    void useLabel(Label label);

    void addObjectEqInstruction(Label ifTrue);

    void addObjectNeInstruction(Label ifTrue);

    void addIntCmpLtInstruction(Label ifTrue);

    void addIntCmpLeInstruction(Label ifTrue);

    void addIntCmpGtInstruction(Label ifTrue);

    void addIntCmpGeInstruction(Label ifTrue);

    void addStoreInstruction(int slot);

    void addLoadInstruction(int slot);

    void addIntCmpNeInstruction(Label ifTrue);

    void addIntCmpEqInstruction(Label ifTrue);

    void addCheckcastInstruction(Class<?> type);

    void addCheckcastInstruction(TypeRef type);

    void debugSlots();

    void addGetFieldInstruction(String name);

    void addGetFieldInstruction(Class<?> clazz, String name);

    void addGetFieldInstruction(TypeRef type, String name);

    void addPutFieldInstruction(String name);

    boolean hasSlot(String name);

}
