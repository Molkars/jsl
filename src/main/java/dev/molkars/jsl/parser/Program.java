package dev.molkars.jsl.parser;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.declaration.Declaration;
import dev.molkars.jsl.parser.statement.Statement;
import dev.molkars.jsl.runtime.JSLProgram;
import dev.molkars.jsl.runtime.JSLRuntime;
import dev.molkars.jsl.tokenizer.TokenView;
import dev.molkars.jsl.tokenizer.Tokens;

import java.util.LinkedList;
import java.util.List;

import static dev.molkars.jsl.Essentials.nicely;
import static dev.molkars.jsl.tokenizer.Tokenizer.tokenize;

public class Program extends ParseElement implements CompileElement {
    public Program(List<ParseElement> children) {
        super(children);
    }

    public static Program parseProgram(String src) {
        return parseProgram(tokenize(src));
    }

    public static JSLProgram compileProgram(Program program) {
        ByteCodeGenerator2 code = new ByteCodeGenerator2();
        program.compile(code);
        code.close();

        return nicely(() ->
                code.compile((TypeRef.Generated) code.getType("dev/molkars/jsl", "$$JSLProgram")));
    }

    public static void runProgram(JSLProgram program) {
        JSLRuntime.initialize(program);
        nicely(program::execute);
    }

    public static Program parseProgram(Tokens tokens) {
        Parser parser = new Parser(new TokenView(tokens));

        LinkedList<ParseElement> children = new LinkedList<>();
        while (parser.more()) {
            children.add(parser.require(Program.class, Declaration::parse));
        }

        return new Program(children);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        code.addClass("dev/molkars/jsl", "$$JSLProgram", Object.class, JSLProgram.class);

        code.addConstructor();
        code.addLoadClassPointerInstruction();
        code.addConstructorCallInstruction(Object.class);

        code.addField("$$LOGS", StringBuffer.class);
        code.addLoadClassPointerInstruction();
        code.addNewInstruction(StringBuffer.class);
        code.addDuplicateInstruction();
        code.addConstructorCallInstruction(StringBuffer.class);
        code.addPutFieldInstruction("$$LOGS");

        code.addReturnInstruction();
        code.closeMethod();

        code.addMethod("execute", void.class);
        for (var child : getChildren()) {
            if (child instanceof CompileElement element) {
                element.compile(code);
            }
        }
        if (!code.hasReturned())
            code.addReturnInstruction();
        code.closeMethod();

        code.addMethod("print", void.class, Object.class);
        code.debugSlots();
        code.addLoadClassPointerInstruction();
        code.addGetFieldInstruction("$$LOGS");
        code.addLoadInstruction(1);
        code.addCallInstruction(String.class, "valueOf", String.class, Object.class);
        code.addCallInstruction(StringBuffer.class, "append", StringBuffer.class, String.class);
        code.addPopInstruction();
        code.addReturnInstruction();
        code.closeMethod();

        code.addMethod("getOutput", String.class);
        code.addLoadClassPointerInstruction();
        code.addGetFieldInstruction("$$LOGS");
        code.addCallInstruction(StringBuffer.class, "toString", String.class);
        code.addObjectReturnInstruction();
        code.closeMethod();

        code.closeClass();
    }
}
