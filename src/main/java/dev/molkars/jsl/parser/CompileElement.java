package dev.molkars.jsl.parser;

import dev.molkars.jsl.bytecode.ByteCodeGenerator2;

public interface CompileElement {
    void compile(ByteCodeGenerator2 code);
}
