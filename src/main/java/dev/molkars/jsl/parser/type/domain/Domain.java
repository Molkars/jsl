package dev.molkars.jsl.parser.type.domain;

import java.math.BigDecimal;

public abstract class Domain {

    public abstract boolean test(BigDecimal value);
}
