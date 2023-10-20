package dev.molkars.jsl.parser.typeexpression;

import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;

public abstract class TypeExpression extends ParseElement {

    public TypeExpression() {
        super();
    }

    public TypeExpression(Token token) {
        super(token);
    }

    public TypeExpression(Token start, Token end) {
        super(start, end);
    }

    public static TypeExpression parse(Parser parser) {
        TypeExpression out;

        if ((out = SimpleTypeExpression.parse(parser)) != null) {
            return out;
        }

        return null;
    }
}
