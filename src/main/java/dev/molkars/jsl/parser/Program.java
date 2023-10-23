package dev.molkars.jsl.parser;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;
import dev.molkars.jsl.bytecode.TypeRef;
import dev.molkars.jsl.parser.declaration.Declaration;
import dev.molkars.jsl.parser.declaration.ObjectDeclaration;
import dev.molkars.jsl.parser.declaration.StatementDeclaration;
import dev.molkars.jsl.runtime.JSLProgram;
import dev.molkars.jsl.runtime.JSLRuntime;
import dev.molkars.jsl.tokenizer.TokenView;
import dev.molkars.jsl.tokenizer.Tokens;

import java.util.LinkedList;
import java.util.List;

import static dev.molkars.jsl.Essentials.nicely;
import static dev.molkars.jsl.tokenizer.Tokenizer.tokenize;

public class Program extends ParseElement implements CompileElement {
    final List<ObjectDeclaration> objectDeclarations;
    final List<StatementDeclaration> statementDeclarations;

    public Program(
            List<ParseElement> children,
            List<StatementDeclaration> statementDeclarations,
            List<ObjectDeclaration> objectDeclarations
    ) {
        super(children);
        this.statementDeclarations = statementDeclarations;
        this.objectDeclarations = objectDeclarations;
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
        List<ObjectDeclaration> objects = new LinkedList<>();
        List<StatementDeclaration> statements = new LinkedList<>();
        while (parser.more()) {
            var declaration = parser.require(Program.class, Declaration::parse);
            if (declaration instanceof ObjectDeclaration object) {
                objects.add(object);
            } else if (declaration instanceof StatementDeclaration statement) {
                statements.add(statement);
            } else {
                throw new RuntimeException("Unknown declaration type: " + declaration.getClass());
            }
            children.add(declaration);
        }

        return new Program(children, statements, objects);
    }

    @Override
    public void compile(ByteCodeGenerator2 code) {
        for (var object : objectDeclarations) {
            object.precompile(code);
        }
        for (var object : objectDeclarations) {
            object.compile(code);
        }

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
        for (var element : statementDeclarations) {
            element.compile(code);
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
