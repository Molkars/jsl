package dev.molkars.jsl.bytecode;

import java.util.HashMap;

public class BytecodeContext {
    HashMap<Object, TypeRef> typeStore = new HashMap<>();

    public BytecodeContext() {
    }

    public TypeRef getTypeFor(Class<?> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("Cannot get type for null");
        if (typeStore.containsKey(clazz))
            return typeStore.get(clazz);

        TypeRef.Defined defined = new TypeRef.Defined(clazz);
        typeStore.put(clazz, defined);
        return defined;
    }

    public TypeRef.Generated addType(String module, String name, TypeRef superType, TypeRef... interfaces) {
        String key = "%s.%s".formatted(module, name);
        if (typeStore.containsKey(key))
            throw new IllegalStateException("Type %s already exists".formatted(key));
        TypeRef.Generated generated = new TypeRef.Generated(module, name, superType, interfaces);
        typeStore.put(key, generated);
        return generated;
    }
}
