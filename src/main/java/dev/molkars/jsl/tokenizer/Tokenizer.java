package dev.molkars.jsl.tokenizer;

import dev.molkars.jsl.Lumber;

import static dev.molkars.jsl.Essentials.debug;

public class Tokenizer implements Lumber {
    private final String source;
    int line, column, index;
    public Tokenizer(String source) {
        this.source = source;
        this.line = 1;
        this.column = 1;
        this.index = 0;
    }

    public static Tokens tokenize(String source) {
        Tokenizer tokenizer = new Tokenizer(source);
        Tokens tokens = new Tokens();
        tokenizer.consumeGarbage();
        while (tokenizer.more()) {
            tokens.add(tokenizer.nextToken());
            tokenizer.consumeGarbage();
        }
        return tokens;
    }

    public boolean consumeGarbage() {
        int start = this.index;
        consumeWhitespace();
        while (take("//")) {
            while (more() && !take('\n')) {
                advance();
            }
            consumeWhitespace();
        }
        return start != this.index;
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

            case '*' -> makeToken(TokenType.STAR);
            case '/' -> makeToken(TokenType.SLASH);
            case '%' -> makeToken(TokenType.PERCENT);
            case '~' -> {
                if (peek('/', 1)) {
                    yield makeToken(TokenType.TILDE_SLASH);
                }
                if (peek('%', 1)) {
                    yield makeToken(TokenType.TILDE_PERCENT);
                }
                throw new IllegalStateException("Unexpected character " + c);
            }
            case '+' -> makeToken(TokenType.PLUS);
            case '-' -> makeToken(TokenType.DASH);

            case ',' -> makeToken(TokenType.COMMA);

            case '"' -> makeString();
            default -> {
                if (Character.isLetter(c) || c == '_') yield makeIdentifier();
                if (Character.isDigit(c)) yield makeNumber();
                throw new IllegalStateException("Unexpected character " + debug(c));
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

        if (take('.')) {
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

    public boolean take(char c) {
        if (more() && source.charAt(index) == c) {
            advance();
            return true;
        }
        return false;
    }

    public boolean take(String s) {
        if (more() && source.startsWith(s, index)) {
            index += s.length();
            column += s.length();
            return true;
        }
        return false;
    }

    public boolean take(String s, int off) {
        int index = this.index + off;
        if (index + s.length() < source.length() && source.startsWith(s, index)) {
            this.index = index + s.length();
            this.column += off + s.length();
            return true;
        }
        return false;
    }

    public boolean peek(char c) {
        return more() && source.charAt(index) == c;
    }

    public boolean peek(String s) {
        return index + s.length() < source.length() && source.startsWith(s, index);
    }

    public boolean peek(String s, int off) {
        int index = this.index + off;
        return index + s.length() < source.length() && source.startsWith(s, index);
    }

    public boolean peek(char c, int off) {
        int index = this.index + off;
        return index < source.length() && index > 0 && source.charAt(index) == c;
    }

    public char peek() {
        if (!more()) {
            throw new IllegalStateException("No more characters");
        }
        return source.charAt(index);
    }

    public char take() {
        if (!more()) {
            throw new IllegalStateException("No more characters");
        }
        char c = source.charAt(index);
        advance();
        return c;
    }

    public boolean take(char c, int off) {
        int index = this.index + off;
        if (index < source.length() && index > 0 && source.charAt(index) == c) {
            this.index = index;
            this.column += off;
            return true;
        }
        return false;
    }
}
