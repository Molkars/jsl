package dev.molkars.jsl.essentials;

import java.util.Collection;
import java.util.function.Predicate;

public final class Predicates {
    private Predicates() {}

    public static <T> boolean all(Collection<T> collection, Predicate<T> test) {
        for (var element : collection) {
            if (!test.test(element)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Collection<T> collection, Predicate<T> test) {
        for (var element : collection) {
            if (test.test(element)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean every(Collection<T> collection, Predicate<T> test) {
        return all(collection, test);
    }

    public static <T> boolean exists(Collection<T> collection, Predicate<T> test) {
        return any(collection, test);
    }

    public static <T> Predicate<T> notNull() {
        return new Predicate<>() {
            @Override
            public boolean test(T o) {
                return o != null;
            }
        };
    }
}
