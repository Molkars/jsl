package dev.molkars.jsl.parser.declaration;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.typeexpression.TypeExpression;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.util.LinkedHashSet;
import java.util.LinkedList;

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

    TypeRef.Generated generated = null;

    public void precompile(ByteCodeGenerator2 code) {
        if (generated != null) {
            throw new IllegalStateException("precompile already called");
        }
        generated = code.addClass("dev/molkars/jsl", name.lex(), Object.class).getType();
        code.closeClass();
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        if (generated == null) {
            throw new IllegalStateException("precompile not called");
        }
        code.focusClass(generated);

        LinkedHashSet<String> fieldNames = new LinkedHashSet<>();
        LinkedList<TypeRef> fieldTypes = new LinkedList<>();
        for (Field field : this.fields) {
            if (fieldNames.contains(field.name.lex())) {
                throw new RuntimeException("duplicate field: " + field.name.lex());
            }
            fieldNames.add(field.name.lex());
            fieldTypes.add(field.type.resolve(code));
        }

        for (Field field : fields) {
            code.addField(field.name.lex(), field.type.resolve(code));
        }

        code.addConstructor(fieldTypes.toArray(TypeRef[]::new));
        code.addLoadClassPointerInstruction();
        code.addConstructorCallInstruction(Object.class);
        int i = 1;
        for (Field field : fields) {
            code.addLoadClassPointerInstruction();
            code.addLoadInstruction(i);
            code.addPutFieldInstruction(field.name.lex());
            i++;
        }

        code.addReturnInstruction();
        code.closeMethod();

        code.addMethod("toString", String.class);
        // Name { x: val(x), y: val(y) }
        code.addNewInstruction(StringBuilder.class);
        code.addDuplicateInstruction();
        code.addConstructorCallInstruction(StringBuilder.class);

        code.addPushConstantInstruction(name.lex() + " { ");
        code.addCallInstruction(StringBuilder.class, "append", StringBuilder.class, String.class);

        boolean first = true;
        for (Field field : fields) {
            String s = "";
            if (!first) {
                s = ", ";
            }
            first = false;
            s += field.name.lex() + ": ";

            code.addPushConstantInstruction(s);
            code.addCallInstruction(StringBuilder.class, "append", StringBuilder.class, String.class);

            code.addLoadClassPointerInstruction();
            code.addGetFieldInstruction(field.name.lex());
            code.addCallInstruction(StringBuilder.class, "append", StringBuilder.class, Object.class);
        }
        if (fields.size() == 0) {
            code.addPushConstantInstruction("}");
        } else {
            code.addPushConstantInstruction(" }");
        }
        code.addCallInstruction(StringBuilder.class, "append", StringBuilder.class, String.class);
        code.addCallInstruction(StringBuilder.class, "toString", String.class);
        code.addObjectReturnInstruction();
        code.closeMethod();

        code.closeClass();
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
