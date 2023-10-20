package dev.molkars.jsl.parser.declaration;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.CompileElement;
import dev.molkars.jsl.parser.ParseElement;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;

public abstract class Declaration extends ParseElement implements CompileElement {
    public Declaration() {
    }

    public Declaration(Token token) {
        super(token);
    }

    public Declaration(Token start, Token end) {
        super(start, end);
    }

    public abstract void compile(ByteCodeGenerator2 code);

    public static Declaration parse(Parser parser) {
        Declaration out;

        if ((out = ObjectDeclaration.parse(parser)) != null) {
            return out;
        }

        if ((out = StatementDeclaration.parse(parser)) != null) {
            return out;
        }

        return null;
    }
}
