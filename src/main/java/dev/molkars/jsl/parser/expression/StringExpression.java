package dev.molkars.jsl.parser.expression;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.parser.Parser;
import dev.molkars.jsl.tokenizer.Token;
import dev.molkars.jsl.tokenizer.TokenType;

import java.util.LinkedList;

public class StringExpression extends Expression {
    final Token token;
    String value;

    public StringExpression(Token token) {
        super(token);
        this.token = token;
    }

    public static StringExpression parse(Parser parser) {
        if (!parser.peek(TokenType.STRING)) {
            return null;
        }
        return new StringExpression(parser.take());
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addPushConstantInstruction(getValue());
    }

    public String getValue() {
        if (value != null) {
            return value;
        }

        value = token.lex();
        LinkedList<String> lines = new LinkedList<>();
        int lastLine = 0;
        StringBuilder builder = new StringBuilder(value.length() - 2);
        for (int i = 1; i < value.length() - 1; i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                i++;
                c = value.charAt(i);
                switch (c) {
                    case 'n' -> builder.append('\n');
                    case 't' -> builder.append('\t');
                    case 'r' -> builder.append('\r');
                    case 'b' -> builder.append('\b');
                    case 'f' -> builder.append('\f');
                    case '\'' -> builder.append('\'');
                    case '"' -> builder.append('"');
                    case '\\' -> builder.append('\\');
                    case 'u' -> {
                        i += 1;
                        if (i + 4 >= value.length()) {
                            throw new RuntimeException("Invalid unicode escape sequence: " + value);
                        }
                        int codePoint = Integer.parseInt(value.substring(i, i + 4), 16);
                        builder.append((char) codePoint);
                        i += 4;
                    }
                    case 'U' -> {
                        i += 1;
                        if (i + 8 >= value.length()) {
                            throw new RuntimeException("Invalid unicode escape sequence: " + value);
                        }
                        int codePoint = Integer.parseInt(value.substring(i, i + 8), 16);
                        builder.append((char) codePoint);
                        i += 8;
                    }
                    case 'o' -> {
                        i += 1;
                        if (i + 3 >= value.length()) {
                            throw new RuntimeException("Invalid octal escape sequence: " + value);
                        }
                        int codePoint = Integer.parseInt(value.substring(i, i + 3), 8);
                        builder.append((char) codePoint);
                        i += 3;
                    }
                    default -> throw new RuntimeException("Unknown escape sequence: \\" + c);
                }
            } else {
                if (c == '\n') {
                    lines.add(value.substring(lastLine, i));
                    lastLine = i + 1;
                }
                builder.append(c);
            }
        }
        lines.add(value.substring(lastLine));

        value = builder.toString();
        if (lines.size() == 1) {
            return value;
        }

        int indent = lines.get(1).length() - lines.get(1).stripLeading().length();
        if (indent == 0) {
            return value;
        }

        builder = new StringBuilder(value.length() - 2);
        builder.append(lines.get(0).substring(1)).append('\n');
        for (String line : lines.subList(1, lines.size())) {
            var lineIndent = line.length() - line.stripLeading().length();
            if (lineIndent < indent) {
                return value;
            }
            builder.append(line.substring(indent));
            builder.append('\n');
        }
        builder.deleteCharAt(builder.length() - 1); // Remove last quote
        builder.deleteCharAt(builder.length() - 1); // Remove last newline
        value = builder.toString();
        return value;
    }
}
