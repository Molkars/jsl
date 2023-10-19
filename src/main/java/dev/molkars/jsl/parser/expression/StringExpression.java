package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
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

    public static StringExpression parse(Parser parser) {
        if (!parser.peek(TokenType.STRING)) {
            return null;
        }
        return new StringExpression(parser.take());
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addPushConstantInstruction(getValue());
    }

    public String getValue() {
        if (value == null) {
            value = token.lex();
            StringBuilder builder = new StringBuilder(value.length() - 2);
            for (int i = 1; i < value.length() - 1; i++) {
                int c = value.charAt(i);
                builder.append((char) c);
            }
            value = builder.toString();
        }
        return value;
    }
}
