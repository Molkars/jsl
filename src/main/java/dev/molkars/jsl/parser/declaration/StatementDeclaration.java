package dev.molkars.jsl.parser.declaration;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.statement.Statement;

public class StatementDeclaration extends Declaration {
    private final Statement statement;

    public StatementDeclaration(Statement statement) {
        this.statement = addChild(statement);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        statement.compile(code);
    }

    public static StatementDeclaration parse(Parser parser) {
        Statement stmt = Statement.parse(parser);
        if (stmt == null) {
            return null;
        }
        return new StatementDeclaration(stmt);
    }
}
