package dev.molkars.jsl.runtime;

public interface JSLProgram {
    void print(Object value);

    void execute();

    String getOutput();
}

