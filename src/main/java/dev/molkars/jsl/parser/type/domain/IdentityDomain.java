package dev.molkars.jsl.parser.type.domain;

import java.math.BigDecimal;

public class IdentityDomain extends Domain {
    BigDecimal value;

    public IdentityDomain(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean test(BigDecimal value) {
        return this.value.equals(value);
    }
}
