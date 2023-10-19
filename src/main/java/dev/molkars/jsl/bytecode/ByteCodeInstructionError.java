package dev.molkars.jsl.bytecode;

import java.io.PrintStream;
import java.util.Arrays;

public class ByteCodeInstructionError extends RuntimeException {
    final InstructionBuilder instructionStack;

    public ByteCodeInstructionError(String message) {
        this(message, null, null);
    }

    public ByteCodeInstructionError(String message, InstructionBuilder stack) {
        this(message, null, stack);
    }

    public ByteCodeInstructionError(String message, Exception cause) {
        this(message, cause, null);
    }

    public ByteCodeInstructionError(String message, Exception cause, InstructionBuilder stack) {
        super(message, cause);
        this.instructionStack = stack;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        int off;
        for (off = 0; off < stackTrace.length && !stackTrace[off].getClassName().contains("dev.molkars.jsl.bytecode"); off++) {
            System.out.println(stackTrace[off].getClassName());
        }
        off -= 1;
        return Arrays.stream(stackTrace)
                .skip(off)
                .toArray(StackTraceElement[]::new);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public String toString() {
        return "BCE: " + super.toString();
    }
}
