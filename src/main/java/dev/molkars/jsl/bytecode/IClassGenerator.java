package dev.molkars.jsl.bytecode;

import java.util.HashMap;

public interface IClassGenerator {
    MethodGenerator2 addMethod(String name, TypeRef returnTypeRef, TypeRef... argumentTypeRefs);

    MethodGenerator2 addMethod(String name, Class<?> returnType, Class<?>... argumentTypes);


    void addField(String name, Class<?> type);

    void addField(String name, TypeRef type);

    HashMap<String, TypeRef> getFields();
}
