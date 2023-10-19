package dev.molkars.jsl;

import static dev.molkars.jsl.Essentials.readToString;
import static dev.molkars.jsl.parser.Program.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: jil <file>");
            System.exit(1);
        }

        var content = readToString(args[0]);
        var ast = parseProgram(content);
        var program = compileProgram(ast);
        runProgram(program);

        var output = program.getOutput();
        System.out.println(output);
    }
}
