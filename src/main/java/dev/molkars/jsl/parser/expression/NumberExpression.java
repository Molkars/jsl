package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

public class NumberExpression extends Expression {
    final Token number;
    Double value;

    public NumberExpression(Token token) {
        super(token);
        this.number = token;
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addPushConstantInstruction(getValue());
        code.addCallInstruction(Double.class, "valueOf", Double.class, double.class);
    }

    public double getValue() {
        if (value == null) value = Double.parseDouble(number.lex());
        return value;
    }

    public static NumberExpression parse(Parser parser) {
        if (!parser.peek(TokenType.NUMBER)) {
            return null;
        }
        Token token = parser.take();
        return new NumberExpression(token);
    }
}
