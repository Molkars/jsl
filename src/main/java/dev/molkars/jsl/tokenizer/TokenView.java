package dev.molkars.jsl.tokenizer;

import java.util.Iterator;

public class TokenView implements Iterator<Token> {
    private final Tokens tokens;
    int index = 0;

    public TokenView(Tokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean hasNext() {
        return index < tokens.size();
    }

    @Override
    public Token next() {
        return tokens.get(index++);
    }

    public Token peek() {
        return tokens.get(index);
    }

    public Token peek(int n) {
        return tokens.get(index + n);
    }

    public boolean more() {
        return hasNext();
    }

    public boolean more(int i) {
        int idx = index + i;
        return idx >= 0 && idx < tokens.size();
    }
}
