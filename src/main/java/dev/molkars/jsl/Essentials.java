package dev.molkars.jsl;

import dev.molkars.jsl.bytecode.TypeRef;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Essentials {
    private Essentials() {
    }

    public static String readToString(String path) {
        return nicely(() -> Files.readString(Paths.get(path)), "Error reading file: " + path);
    }

    public static Iterable<TypeRef> reverse(TypeRef[] consumes) {
        return new Iterable<TypeRef>() {
            @Override
            public Iterator<TypeRef> iterator() {
                return new Iterator<TypeRef>() {
                    int index = consumes.length - 1;

                    @Override
                    public boolean hasNext() {
                        return index >= 0;
                    }

                    @Override
                    public TypeRef next() {
                        return consumes[index--];
                    }
                };
            }

            ;
        };
    }

    public static <I, O> O[] arrayMap(I[] input, Function<I, O> mapper, IntFunction<O[]> collector) {
        O[] output = collector.apply(input.length);
        for (int i = 0; i < input.length; i++) {
            output[i] = mapper.apply(input[i]);
        }
        return output;
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

    public static <T> T nicely(NiceRunnable<T> runnable) {
        return nicely(runnable, null);
    }

    public static void nicely(Runnable runnable) {
        nicely(() -> {
            runnable.run();
            return null;
        });
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

    public static String debug(char c) {
        return debug(String.valueOf(c));
    }

    @FunctionalInterface
    public interface SafeRunnable<T> {
        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface NiceRunnable<T> {
        T run() throws Throwable;
    }
}
