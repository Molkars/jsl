package dev.molkars.jsl.bytecode;

import java.util.HashMap;

public interface IClassGeneratorDelegate extends IClassGenerator {
    IClassGenerator getClassGenerator();

    default MethodGenerator2 addMethod(String name, TypeRef returnTypeRef, TypeRef... argumentTypeRefs) {
        return getClassGenerator().addMethod(name, returnTypeRef, argumentTypeRefs);
    }

    default MethodGenerator2 addMethod(String name, Class<?> returnType, Class<?>... argumentTypes) {
        return getClassGenerator().addMethod(name, returnType, argumentTypes);
    }

    @Override
    default void addField(String name, Class<?> type) {
        getClassGenerator().addField(name, type);
    }

    @Override
    default void addField(String name, TypeRef type) {
        getClassGenerator().addField(name, type);
    }

    @Override
    default HashMap<String, TypeRef> getFields() {
       return getClassGenerator().getFields();
    }
}
