package dev.molkars.jsl.tokenizer;

import dev.molkars.jsl.essentials.CollectionMixin;

import java.util.ArrayList;
import java.util.Iterator;

public class Tokens implements CollectionMixin<Token> {
    private final ArrayList<Token> tokens = new ArrayList<>();

    public Tokens() {
    }

    @Override
    public int size() {
        return tokens.size();
    }

    @Override
    public boolean contains(Object o) {
        return tokens.contains(o);
    }

    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }

    @Override
    public boolean add(Token token) {
        return tokens.add(token);
    }

    @Override
    public boolean remove(Object o) {
        return tokens.remove(o);
    }

    public Token get(int i) {
        return tokens.get(i);
    }
}
