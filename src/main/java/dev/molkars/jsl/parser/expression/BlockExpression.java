package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.parser.statement.Statement;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;

public class BlockExpression extends Expression {
    final List<Statement> statements;

    public BlockExpression(Token start, Token end, List<Statement> statements) {
        super(start, end);
        this.statements = statements;
    }

    public static BlockExpression parse(Parser parser) {
        if (!parser.peek(TokenType.LEFT_BRACE)) {
            return null;
        }
        Token start = parser.take();

        List<Statement> statements = new LinkedList<>();
        while (parser.more() && !parser.peek(TokenType.RIGHT_BRACE)) {
            Statement statement = parser.require(BlockExpression.class, Statement::parse);
            statements.add(statement);
        }

        Token end = parser.expect(TokenType.RIGHT_BRACE);
        return new BlockExpression(start, end, statements);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        for (Statement statement : statements) {
            statement.compile(code);
        }
    }
}
