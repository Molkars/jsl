package dev.molkars.jsl.tokenizer;

import static dev.molkars.jsl.Essentials.debug;

public final class Token {
    final TokenType type;
    final String source;
    final int start, length;
    final int line, column;

    public Token(TokenType type, String source, int start, int length, int line, int column) {
        this.type = type;
        this.source = source;
        this.start = start;
        this.length = length;
        this.line = line;
        this.column = column;
    }

    public String lex() {
        return source.substring(start, start + length);
    }

    @Override
    public String toString() {
        return "Token(" + type + ", " + debug(source.substring(start, start + length)) + ", index(" + start + "..+" +
                length + ") location(" + line + ":" + column + "))";
    }

    public TokenType type() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token t) {
            return type().equals(t.type()) && lex().equals(t.lex());
        } else if (obj instanceof String s) {
            return type() == TokenType.SYMBOL && lex().equals(s);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return lex().hashCode();
    }
}
