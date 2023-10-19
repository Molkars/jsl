package dev.molkars.jsl.tokenizer;

public enum TokenType {
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),

    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),

    STRING(null),
    SYMBOL(null),
    NUMBER(null);

    private final String lex;

    TokenType(String lex) {
        this.lex = lex;
    }

    public String lex() {
        return lex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name());

        if (lex != null) {
            builder.append("(");
            builder.append(lex);
            builder.append(")");
        }

        return builder.toString();
    }
}
