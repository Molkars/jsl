package dev.molkars.jsl.parser.type;

import dev.molkars.jsl.parser.type.domain.Domain;

public abstract class Type {

    public static final class Int extends Type {
        final Domain domain;

        public Int(Domain domain) {
            this.domain = domain;
        }
    }
}
