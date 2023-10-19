package dev.molkars.jsl.parser;

import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;
import dev.molkars.jsl.tokenizer.TokenView;

import java.util.function.Function;

public class Parser {
    final TokenView tokens;

    public Parser(TokenView tokens) {
        this.tokens = tokens;
    }

    public boolean peek(String token) {
        if (!tokens.more()) {
            return false;
        }

        return tokens.peek().equals(token);
    }

    public Token take() {
        return tokens.next();
    }

    public <T extends ParseElement> T require(Class<? extends ParseElement> parent, Function<Parser, T> parser) {
        T element = parser.apply(this);
        if (element == null) {
            throw new ParseException("Expected " + parent.getSimpleName() + " but found " + tokens.peek());
        }
        return element;
    }

    public boolean peek(TokenType tokenType) {
        if (!tokens.more()) {
            return false;
        }

        return tokens.peek().type() == tokenType;
    }

    public boolean more() {
        return tokens.more();
    }

    public Token expect(TokenType tokenType) {
        if (!tokens.more()) {
            throw new ParseException("Expected " + tokenType + " but found end of file");
        }

        Token token = tokens.next();
        if (token.type() != tokenType) {
            throw new ParseException("Expected " + tokenType + " but found " + token);
        }

        return token;
    }
}
