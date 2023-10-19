package dev.molkars.jsl.parser.statement;

import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.expression.Expression;
import dev.molkars.jsl.tokenizer.Token;

public class PrintStatement extends Statement {
    private final Expression value;

    public PrintStatement(Token start, Expression value) {
        super(start, null);
        this.value = addChild(value);
    }

    public static PrintStatement parse(Parser parser) {
        if (!parser.peek("print")) {
            return null;
        }
        Token start = parser.take();
        Expression value = parser.require(PrintStatement.class, Expression::parse);

        return new PrintStatement(start, value);
    }
}
