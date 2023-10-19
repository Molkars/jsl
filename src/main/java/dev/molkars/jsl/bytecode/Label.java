package dev.molkars.jsl.bytecode;

public class Label {
    final String name;
    final org.objectweb.asm.Label inner = new org.objectweb.asm.Label();

    Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public org.objectweb.asm.Label getInner() {
        return inner;
    }

    @Override
    public String toString() {
        return "Label[" + name + "]";
    }
}
