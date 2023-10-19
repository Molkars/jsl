package dev.molkars.jsl.tokenizer;

import dev.molkars.jsl.Lumber;

public class Tokenizer implements Lumber {
    public static Tokens tokenize(String source) {
        Tokenizer tokenizer = new Tokenizer(source);
        Tokens tokens = new Tokens();
        tokenizer.consumeWhitespace();
        while (tokenizer.more()) {
            tokens.add(tokenizer.nextToken());
            tokenizer.consumeWhitespace();
        }
        return tokens;
    }

    private final String source;
    int line, column, index;

    public Tokenizer(String source) {
        this.source = source;
        this.line = 1;
        this.column = 1;
        this.index = 0;
    }

    public boolean consumeWhitespace() {
        int start = this.index;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else if (c == '\n') {
                index++;
                line++;
                column = 1;
            } else {
                break;
            }
        }
        return start != this.index;
    }

    public Token nextToken() {
        if (!more()) {
            throw new IllegalStateException("No more tokens");
        }

        char c = source.charAt(index);
        return switch (c) {
            case '(' -> makeToken(TokenType.LEFT_PAREN);
            case ')' -> makeToken(TokenType.RIGHT_PAREN);
            case '{' -> makeToken(TokenType.LEFT_BRACE);
            case '}' -> makeToken(TokenType.RIGHT_BRACE);
            case '"' -> makeString();
            default -> {
                if (Character.isLetter(c) || c == '_') yield makeIdentifier();
                if (Character.isDigit(c)) yield makeNumber();
                throw new IllegalStateException("Unexpected character " + c);
            }
        };
    }


    public Token makeToken(TokenType type) {
        String lex = type.lex();
        if (lex == null) {
            throw new IllegalArgumentException("Token type " + type + " has no lexeme");
        }
        Token token = new Token(type, source, index, lex.length(), line, column);
        index += lex.length();
        column += lex.length();
        return token;
    }

    public Token makeString() {
        int start = index;
        advance();
        while (more() && !peek('"')) {
            advance();
        }
        if (more()) {
            advance();
            return new Token(TokenType.STRING, source, start, index - start, line, column);
        }
        throw new IllegalStateException("Unterminated string literal!");
    }

    public Token makeNumber() {
        int start = index;
        while (more()) {
            char c = source.charAt(index);
            if (!Character.isDigit(c) && c != '_') {
                break;
            }
            index++;
            column++;
        }

        if (doTake('.')) {
            while (more()) {
                char c = source.charAt(index);
                if (!Character.isDigit(c) && c != '_') {
                    break;
                }
                index++;
                column++;
            }
        }

        return new Token(TokenType.NUMBER, source, start, index - start, line, column);
    }

    public Token makeIdentifier() {
        int start = index;
        while (more()) {
            char c = source.charAt(index);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                break;
            }
            advance();
        }
        return new Token(TokenType.SYMBOL, source, start, index - start, line, column);
    }

    // ---- snip ----


    public boolean more() {
        return index < source.length();
    }

    public void advance() {
        index++;
        column++;
    }

    public boolean doTake(char c) {
        if (more() && source.charAt(index) == c) {
            advance();
            return true;
        }
        return false;
    }

    public boolean peek(char c) {
        return more() && source.charAt(index) == c;
    }

    public char take() {
        if (!more()) {
            throw new IllegalStateException("No more characters");
        }
        char c = source.charAt(index);
        advance();
        return c;
    }
}
