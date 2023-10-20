package dev.molkars.jsl.essentials;

@FunctionalInterface
public interface TriFunction<A, B, C, O> {
    O apply(A a, B b, C c);
}
