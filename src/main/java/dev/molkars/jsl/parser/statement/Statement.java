package dev.molkars.jsl.parser.statement;

import dev.molkars.jsl.parser.CompileElement;
import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;

public abstract class Statement extends ParseElement implements CompileElement {
    protected Statement() {
        super();
    }

    protected Statement(Token token) {
        super(token);
    }

    protected Statement(Token start, Token end) {
        super(start, end);
    }

    public static Statement parse(Parser parser) {
        if (!parser.more()) {
            return null;
        }

        Statement out;
        if ((out = PrintStatement.parse(parser)) != null) {
            return out;
        }

        return null;
    }
}
