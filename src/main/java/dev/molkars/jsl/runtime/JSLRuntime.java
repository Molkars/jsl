package dev.molkars.jsl.runtime;

public class JSLRuntime {
    private static JSLRuntime INSTANCE;
    final JSLProgram program;
    final TypedStore store = new TypedStore();

    public JSLRuntime(JSLProgram program) {
        this.program = program;
    }

    public static JSLRuntime getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("JSLRuntime not initialized");
        }
        return INSTANCE;
    }

    public static void initialize(JSLProgram program) {
        if (INSTANCE != null) {
            throw new IllegalStateException("JSLRuntime already initialized");
        }
        INSTANCE = new JSLRuntime(program);
    }

    public void print(Object value) {
        program.print(value);
    }
}
