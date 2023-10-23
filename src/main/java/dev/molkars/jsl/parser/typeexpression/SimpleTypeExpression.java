package dev.molkars.jsl.parser.typeexpression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.math.BigDecimal;

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

    TypeRef cached = null;

    @Override
    public TypeRef resolve(ByteCodeGenerator2 code) {
        if (cached != null) {
            return cached;
        }
        return (cached = switch (name.lex()) {
            case "Num" -> code.getType(BigDecimal.class);
            case "String" -> code.getType(String.class);
            default -> code.getType(name.lex());
        });
    }
}
