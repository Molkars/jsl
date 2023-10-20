package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.TokenType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

public class FactorExpression extends Expression {
    public enum Op {
        TIMES,
        DIVIDE,
        MODULO,
        INT_DIVIDE,
        INT_MODULO,
    }

    final Expression left;
    final Expression right;
    final Op operator;

    public FactorExpression(Expression left, Expression right, Op operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        if (operator != Op.INT_MODULO) {
            left.compile(code);
            right.compile(code);
        }

        if (operator == Op.TIMES) {
            code.addCallInstruction(BigDecimal.class, "multiply", BigDecimal.class, BigDecimal.class);
        } else if (operator == Op.DIVIDE) {
            code.addCallInstruction(BigDecimal.class, "divide", BigDecimal.class, BigDecimal.class);
        } else if (operator == Op.MODULO) {
            code.addCallInstruction(BigDecimal.class, "remainder", BigDecimal.class, BigDecimal.class);
        } else if (operator == Op.INT_DIVIDE) {
            code.addCallInstruction(BigDecimal.class, "divideToIntegralValue", BigDecimal.class, BigDecimal.class);
        } else if (operator == Op.INT_MODULO) {
            code.addNewInstruction(BigDecimal.class);
            code.addDuplicateInstruction();
            left.compile(code);
            right.compile(code);
            code.addCallInstruction(BigDecimal.class, "remainder", BigDecimal.class, BigDecimal.class);
            code.addCallInstruction(BigDecimal.class, "toBigInteger", BigInteger.class);
            code.addConstructorCallInstruction(BigDecimal.class, BigInteger.class);
        } else {
            throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    private static final TokenType[] TOKENS = new TokenType[]{
            TokenType.STAR, TokenType.SLASH, TokenType.PERCENT, TokenType.TILDE_SLASH, TokenType.TILDE_PERCENT
    };
    private static final TreeMap<TokenType, Op> OPERATORS = new TreeMap<>();

    static {
        OPERATORS.put(TokenType.STAR, Op.TIMES);
        OPERATORS.put(TokenType.SLASH, Op.DIVIDE);
        OPERATORS.put(TokenType.PERCENT, Op.MODULO);
        OPERATORS.put(TokenType.TILDE_SLASH, Op.INT_DIVIDE);
        OPERATORS.put(TokenType.TILDE_PERCENT, Op.INT_MODULO);
    }

    public static Expression parse(Parser parser) {
        return parser
                .separated(FactorExpression.class, Expression::primary, TOKENS)
                .fold((left, right, op) -> new FactorExpression(left, right, OPERATORS.get(op.type())));
    }
}
