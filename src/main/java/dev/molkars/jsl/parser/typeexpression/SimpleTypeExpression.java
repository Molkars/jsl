package dev.molkars.jsl.parser.typeexpression;

import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

public class SimpleTypeExpression extends TypeExpression {
    final Token name;

    public SimpleTypeExpression(Token name) {
        super(name);
        this.name = name;
    }

    public static SimpleTypeExpression parse(Parser parser) {
        if (!parser.peek(TokenType.SYMBOL)) {
            return null;
        }

        Token name = parser.take();
        return new SimpleTypeExpression(name);
    }
}
