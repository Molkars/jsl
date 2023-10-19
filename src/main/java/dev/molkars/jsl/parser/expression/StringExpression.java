package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

public class StringExpression extends Expression {
    final Token token;
    String value;

    public StringExpression(Token token) {
        super(token);
        this.token = token;
    }

    public String getValue() {
        if (value == null) {
            value = token.lex();
        }
        return value;
    }

    public static StringExpression parse(Parser parser) {
        if (!parser.peek(TokenType.STRING)) {
            return null;
        }
        return new StringExpression(parser.take());
    }
}
