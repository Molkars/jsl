package dev.molkars.jsl.parser;

import dev.molkars.jsl.essentials.Pair;
import dev.molkars.jsl.essentials.TriFunction;
import dev.molkars.jsl.tokenizer.Token;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiFunction;

public class Separated<E> {
    final LinkedList<Pair<E, Token>> items;
    final E last;

    public Separated(LinkedList<Pair<E, Token>> items, E last) {
        this.items = items;
        this.last = Objects.requireNonNull(last);
    }

    public E fold(TriFunction<E, E, Token, E> func) {
        if (items.isEmpty()) return last;
        var iterator = items.iterator();
        Pair<E, Token> last = iterator.next();
        E acc = last.left();
        while (iterator.hasNext()) {
            var next = iterator.next();
            acc = func.apply(acc, next.left(), last.right());
            last = next;
        }
        return func.apply(acc, this.last, last.right());
    }
}
