package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.TokenType;

import java.math.BigDecimal;
import java.util.TreeMap;

public class TermExpression extends Expression {
    public enum Op {
        ADD, SUBTRACT,
    }

    final Expression left;
    final Expression right;
    final Op operator;

    public TermExpression(Expression left, Expression right, Op operator) {
        this.left = addChild(left);
        this.right = addChild(right);
        this.operator = operator;
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        left.compile(code);
        right.compile(code);
        switch (operator) {
            case ADD -> {
                code.addCallInstruction(BigDecimal.class, "add", BigDecimal.class, BigDecimal.class);
            }
            case SUBTRACT -> {
                code.addCallInstruction(BigDecimal.class, "subtract", BigDecimal.class, BigDecimal.class);
            }
        }
    }

    private static final TokenType[] OPERATORS = {TokenType.PLUS, TokenType.DASH,};
    private static final TreeMap<TokenType, Op> OPS = new TreeMap<>();

    static {
        OPS.put(TokenType.PLUS, Op.ADD);
        OPS.put(TokenType.DASH, Op.SUBTRACT);
    }

    public static Expression parse(Parser parser) {
        return parser
                .separated(TermExpression.class, FactorExpression::parse, OPERATORS)
                .fold((left, right, op) -> new TermExpression(left, right, OPS.get(op.type())));
    }
}
