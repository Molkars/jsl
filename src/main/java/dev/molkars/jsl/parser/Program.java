package dev.molkars.jsl.parser;

import dev.molkars.jsl.parser.statement.Statement;
import dev.molkars.jsl.tokenizer.TokenView;
import dev.molkars.jsl.tokenizer.Tokens;

import java.util.LinkedList;
import java.util.List;

import static dev.molkars.jsl.tokenizer.Tokenizer.tokenize;

public class Program extends ParseElement {
    public Program(List<ParseElement> children) {
        super(children);
    }

    public static Program parseProgram(String src) {
        return parseProgram(tokenize(src));
    }

    public static Program parseProgram(Tokens tokens) {
        Parser parser = new Parser(new TokenView(tokens));

        LinkedList<ParseElement> children = new LinkedList<>();
        while (parser.more()) {
            children.add(parser.require(Program.class, Statement::parse));
        }

        return new Program(children);
    }
}
