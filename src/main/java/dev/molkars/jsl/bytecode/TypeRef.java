package dev.molkars.jsl.bytecode;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public interface TypeRef extends Display {
    static String formatType(TypeRef ref) {
        String name = ref.getQualifiedName();
        int lastDot = name.lastIndexOf('/');
        if (lastDot == -1) return name;
        return "L" + Arrays.stream(name.substring(0, lastDot)
                        .split("/"))
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining("/"))
                .concat(name.substring(lastDot)) + ";";
    }

    static String formatType(Class<?> type) {
        String name = type.getName();
        int lastSlash = name.lastIndexOf('.');
        if (lastSlash == -1) return name;
        return Arrays.stream(name.substring(0, lastSlash)
                        .split("\\."))
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining("/"))
                .concat(name.substring(lastSlash));
    }

    static String formatType(Object type) {
        if (type instanceof Class<?> clazz)
            return formatType(clazz);
        else if (type instanceof TypeRef ref)
            return formatType(ref);
        return Objects.toString(type);
    }

    String getModule();

    String getTypeName();

    String getDescriptor();

    String getStandaloneDescriptor();

    String getInternalName();

    boolean isPrimitive();

    boolean is(Class<?> type);

    int getLoadOpcode();

    int getStoreOpcode();

    boolean inheritsFrom(TypeRef potentialSuperClass);

    boolean isAssignableTo(TypeRef resultTypeRef);

    // path/to/module/ClassName
    String getQualifiedName();

    default boolean isVoid() {
        return is(void.class) || is(Void.class);
    }

    boolean isInterface();

    class Defined implements TypeRef {
        Class<?> clazz;

        public Defined(Class<?> clazz) {
            this.clazz = clazz;
        }


        @Override
        public String getModule() {
            return clazz.getModule().getName();
        }

        @Override
        public String getTypeName() {
            return clazz.getTypeName();
        }

        @Override
        public String getDescriptor() {
            if (clazz == void.class || clazz == Void.class)
                return "V";
            if (clazz == int.class)
                return "I";
            if (clazz == boolean.class)
                return "Z";
            if (clazz == byte.class)
                return "B";
            if (clazz == char.class)
                return "C";
            if (clazz == short.class)
                return "S";
            if (clazz == long.class)
                return "J";
            if (clazz == float.class)
                return "F";
            if (clazz == double.class)
                return "D";
            if (clazz.isArray())
                return new Defined(clazz.getComponentType()).getDescriptor() + "[]";
            return "L" + clazz.getName().replace(".", "/") + ";";
        }

        @Override
        public String getStandaloneDescriptor() {
            if (clazz == void.class || clazz == Void.class)
                return "V";
            if (clazz == int.class)
                return "I";
            if (clazz == boolean.class)
                return "Z";
            if (clazz == byte.class)
                return "B";
            if (clazz == char.class)
                return "C";
            if (clazz == short.class)
                return "S";
            if (clazz == long.class)
                return "J";
            if (clazz == float.class)
                return "F";
            if (clazz == double.class)
                return "D";
            if (clazz.isArray())
                return new Defined(clazz.getComponentType()).getDescriptor() + "[]";
            return clazz.getName().replace(".", "/");
        }

        @Override
        public String getInternalName() {
            return clazz.getName();
        }

        @Override
        public boolean isPrimitive() {
            return clazz.isPrimitive();
        }

        @Override
        public int getLoadOpcode() {
            if (clazz == int.class || clazz == boolean.class || clazz == byte.class || clazz == char.class || clazz == short.class) {
                return Opcodes.ILOAD;
            } else if (clazz == long.class) {
                return Opcodes.LLOAD;
            } else if (clazz == float.class) {
                return Opcodes.FLOAD;
            } else if (clazz == double.class) {
                return Opcodes.DLOAD;
            } else if (clazz == Void.class || clazz == void.class) {
                throw new IllegalStateException("Cannot load void");
            } else {
                return Opcodes.ALOAD;
            }
        }

        @Override
        public int getStoreOpcode() {
            if (clazz == int.class || clazz == boolean.class || clazz == byte.class || clazz == char.class || clazz == short.class) {
                return Opcodes.ISTORE;
            } else if (clazz == long.class) {
                return Opcodes.LSTORE;
            } else if (clazz == float.class) {
                return Opcodes.FSTORE;
            } else if (clazz == double.class) {
                return Opcodes.DSTORE;
            } else if (clazz == Void.class || clazz == void.class) {
                throw new IllegalStateException("Cannot store void");
            } else {
                return Opcodes.ASTORE;
            }
        }

        @Override
        public boolean inheritsFrom(TypeRef potentialSuperClass) {
            if (potentialSuperClass instanceof Defined defined) {
                return defined.clazz.isAssignableFrom(clazz);
            } else {
                return false;
            }
        }

        @Override
        public boolean isAssignableTo(TypeRef resultTypeRef) {
            if (resultTypeRef instanceof Defined defined) {
                // special case for int and boolean - there is an implicit conversion since the jvm repr is the same
                if (defined.clazz.equals(int.class) || defined.clazz.equals(boolean.class)) {
                    return clazz.equals(int.class) || clazz.equals(boolean.class);
                }
                return defined.clazz.isAssignableFrom(clazz);
            } else {
                return resultTypeRef.inheritsFrom(this);
            }
        }

        @Override
        public String getQualifiedName() {
            return clazz.getName().replaceAll("\\.", "/");
        }

        @Override
        public boolean isInterface() {
            return clazz.isInterface();
        }

        @Override
        public boolean is(Class<?> type) {
            return clazz == type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Defined defined)) return false;
            return Objects.equals(clazz, defined.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }

        @Override
        public String toString() {
            return getInternalName();
        }

        @Override
        public void display(StringBuffer buffer) {
            for (String piece : getInternalName().split("/")) {
                buffer.append(piece.charAt(0)).append("/");
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }
    }

    class Generated implements TypeRef {
        String module;
        String typeName;
        TypeRef superType;
        TypeRef[] interfaces;

        public Generated(String module, String typeName, TypeRef superType, TypeRef... interfaces) {
            this.module = module;
            this.typeName = typeName;
            this.superType = superType;
            this.interfaces = interfaces;
        }


        @Override
        public String getModule() {
            return module;
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        public TypeRef getSuperType() {
            return superType;
        }

        public TypeRef[] getInterfaces() {
            return interfaces;
        }

        @Override
        public String getDescriptor() {
            String name = module + "/" + typeName;
            return "L" + name + ";";
        }

        @Override
        public String getStandaloneDescriptor() {
            return module + "/" + typeName;
        }

        @Override
        public String getInternalName() {
            return module.replaceAll("/", ".") + "." + typeName;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public boolean is(Class<?> type) {
            return false;
        }

        @Override
        public boolean inheritsFrom(TypeRef potentialSuperClass) {
            if (potentialSuperClass.is(Object.class)) return true;
            if (potentialSuperClass instanceof Generated) {
                if (this == potentialSuperClass) return true;
                if (superType.inheritsFrom(potentialSuperClass)) return true;
                for (TypeRef superType : interfaces) {
                    if (superType.inheritsFrom(potentialSuperClass)) return true;
                }
            } else {
                if (superType.inheritsFrom(potentialSuperClass)) return true;
                for (TypeRef superType : interfaces) {
                    if (superType.inheritsFrom(potentialSuperClass)) return true;
                }
            }
            return false;
        }

        @Override
        public boolean isAssignableTo(TypeRef resultTypeRef) {
            return this.inheritsFrom(resultTypeRef);
        }

        @Override
        public String getQualifiedName() {
            return module + "/" + typeName;
        }

        @Override
        public boolean isInterface() {
            return false;
        }

        @Override
        public int getLoadOpcode() {
            return Opcodes.ALOAD;
        }

        @Override
        public int getStoreOpcode() {
            return Opcodes.ASTORE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Generated generated)) return false;
            return Objects.equals(module, generated.module) && Objects.equals(typeName, generated.typeName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(module, typeName);
        }

        @Override
        public String toString() {
            return module + "." + typeName;
        }

        @Override
        public void display(StringBuffer buffer) {
            String name = getQualifiedName();
            if (name.indexOf('/') == -1) {
                buffer.append(name);
                return;
            }
            for (String piece : name.split("/")) {
                buffer.append(piece.charAt(0)).append('/');
            }
            buffer.append(typeName);
        }
    }
}
