package dev.molkars.jsl.parser.declaration;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.typeexpression.TypeExpression;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;

public class ObjectDeclaration extends Declaration {
    public static class Field extends ParseElement {
        final TypeExpression type;
        final Token name;

        public Field(TypeExpression type, Token name) {
            super(null, name);
            this.type = addChild(type);
            this.name = name;
        }
    }

    final Token name;
    final LinkedList<Field> fields;

    public ObjectDeclaration(Token object, Token name, LinkedList<Field> fields) {
        super(object, null);
        this.name = name;
        this.fields = addChildren(fields);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addClass("dev/molkars/jsl", name.lex(), Object.class);

        TypeRef fieldTypes[] = fields.stream().map(field -> field.type.resolve(code)).toArray(TypeRef[]::new);

        code.addConstructor(fieldTypes);
        code.addConstructorCallInstruction(Object.class);

        for (Field field : fields) {
            code.addField(field.name.lex(), field.type.resolve(code));
        }


    }

    public static ObjectDeclaration parse(Parser parser) {
        if (!parser.peek("object")) {
            return null;
        }

        Token object = parser.take();
        Token name = parser.expect(TokenType.SYMBOL);
        parser.expect(TokenType.LEFT_BRACE);

        LinkedList<Field> fields = new LinkedList<>();
        while (parser.more() && !parser.peek(TokenType.RIGHT_BRACE)) {
            TypeExpression type = parser.require(ObjectDeclaration.class, TypeExpression::parse);
            Token fieldName = parser.expect(TokenType.SYMBOL);
            Field field = new Field(type, fieldName);
            fields.add(field);

            if (!parser.take(TokenType.COMMA)) {
                break;
            }
        }

        parser.expect(TokenType.RIGHT_BRACE);

        return new ObjectDeclaration(object, name, fields);
    }
}
