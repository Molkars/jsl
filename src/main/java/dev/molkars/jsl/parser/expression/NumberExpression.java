package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.math.BigDecimal;

public class NumberExpression extends Expression {
    final Token number;
    BigDecimal value;

    public NumberExpression(Token token) {
        super(token);
        this.number = token;
    }

    public static NumberExpression parse(Parser parser) {
        if (!parser.peek(TokenType.NUMBER)) {
            return null;
        }
        Token token = parser.take();
        return new NumberExpression(token);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addNewInstruction(BigDecimal.class);
        code.addDuplicateInstruction();
        code.addPushConstantInstruction(number.lex());
        code.addConstructorCallInstruction(BigDecimal.class, String.class);
    }

    public BigDecimal getValue() {
        if (value == null) value = new BigDecimal(number.lex());
        return value;
    }
}
