package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.parser.CompileElement;
import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;

public abstract class Expression extends ParseElement implements CompileElement {
    protected Expression() {
        super();
    }

    protected Expression(Token token) {
        super(token);
    }

    protected Expression(Token start, Token end) {
        super(start, end);
    }

    public static Expression parse(Parser parser) {
        return TermExpression.parse(parser);
    }

    public static Expression primary(Parser parser) {
        Expression out;

        if ((out = NumberExpression.parse(parser)) != null) {
            return out;
        }

        if ((out = StringExpression.parse(parser)) != null) {
            return out;
        }

        if ((out = ConstructorExpression.parse(parser)) != null) {
            return out;
        }

        return out;
    }
}
