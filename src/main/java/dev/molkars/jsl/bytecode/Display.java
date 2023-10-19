package dev.molkars.jsl.bytecode;

public interface Display {
    static <T extends Display> void appendList(StringBuffer buffer, T[] items, String delim) {
        for (int i = 0; i < items.length; i++) {
            items[i].display(buffer);
            if (i < items.length - 1) {
                buffer.append(delim);
            }
        }
    }

    void display(StringBuffer b);

    default String display() {
        StringBuffer buffer = new StringBuffer();
        display(buffer);
        return buffer.toString();
    }
}
