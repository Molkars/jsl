package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.typeexpression.TypeExpression;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;

import static dev.molkars.jsl.essentials.Predicates.any;
import static dev.molkars.jsl.essentials.Predicates.notNull;


public class ConstructorExpression extends Expression {
    final TypeExpression name;
    final List<Token> fieldNames;
    final List<Expression> fieldValues;

    public ConstructorExpression(TypeExpression name, LinkedList<Token> fieldNames, LinkedList<Expression> fieldValues, Token end) {
        super(null, end);
        this.name = addChild(name);
        this.fieldNames = fieldNames;
        this.fieldValues = addChildren(fieldValues);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        if (any(fieldNames, notNull())) {
            throw new UnsupportedOperationException("Named fields are not supported yet");
        }

        var type = name.resolve(code);
        code.addNewInstruction(type);
        code.addDuplicateInstruction();
        for (var value : fieldValues) {
            value.compile(code);
        }
        TypeRef[] fieldTypes = new TypeRef[fieldValues.size()];
        for (int i = 0; i < fieldTypes.length; i++) {
            fieldTypes[i] = code.getStackElement(fieldTypes.length - i - 1);
        }
        code.addConstructorCallInstruction(type, fieldTypes);
    }

    public static ConstructorExpression parse(Parser parser) {
        if (!parser.peek(TokenType.SYMBOL) || !parser.peek(1, TokenType.LEFT_BRACE)) {
            return null;
        }
        TypeExpression name = parser.require(TypeExpression.class, TypeExpression::parse);
        parser.expect(TokenType.LEFT_BRACE);

        LinkedList<Token> fieldNames = new LinkedList<>();
        LinkedList<Expression> fieldValues = new LinkedList<>();
        while (parser.more() && !parser.peek(TokenType.RIGHT_BRACE)) {
            Token fieldName = null;
            if (parser.peek(TokenType.SYMBOL) && parser.peek(1, TokenType.EQUAL)) {
                fieldName = parser.expect(TokenType.SYMBOL);
                parser.expect(TokenType.EQUAL);
            }
            Expression fieldValue = parser.require(ConstructorExpression.class, Expression::parse);
            fieldNames.add(fieldName);
            fieldValues.add(fieldValue);

            if (!parser.take(TokenType.COMMA)) {
                break;
            }
        }

        var end = parser.expect(TokenType.RIGHT_BRACE);

        return new ConstructorExpression(name, fieldNames, fieldValues, end);
    }
}
