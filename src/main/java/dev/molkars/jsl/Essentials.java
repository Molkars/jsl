package dev.molkars.jsl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public final class Essentials {
    private Essentials() {
    }

    public static String readToString(String path) {
        return nicely(() -> Files.readString(Paths.get(path)), "Error reading file: " + path);
    }


    @FunctionalInterface
    public interface SafeRunnable<T> {
        T run() throws Throwable;
    }

    public static <T> Optional<T> safely(SafeRunnable<T> runnable) {
        try {
            return Optional.of(runnable.run());
        } catch (Throwable throwable) {
            Lumber.on(runnable.getClass())
                    .error(throwable, "Error: " + throwable.getMessage());
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface NiceRunnable<T> {
        T run() throws Throwable;
    }

    public static <T> T nicely(NiceRunnable<T> runnable) {
        return nicely(runnable, null);
    }

    public static <T> T nicely(NiceRunnable<T> runnable, String message) {
        try {
            return runnable.run();
        } catch (Throwable throwable) {
            Lumber.on(runnable.getClass())
                    .error(throwable, message);
            System.exit(1);
            return null;
        }
    }

    public static String debug(String input) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 32 && c <= 126) {
                builder.append(c);
            } else if (c == '\r') {
                builder.append("\\r");
            } else if (c == '\n') {
                builder.append("\\n");
            } else if (c == '\t') {
                builder.append("\\t");
            } else {
                builder.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return builder.toString();
    }
}
