package dev.molkars.jsl.parser.type.domain;

import java.math.BigDecimal;

public class IntersectionDomain extends Domain {
    private final Domain left;
    private final Domain right;

    public IntersectionDomain(Domain left, Domain right) {
        this.left = left;
        this.right = right;
    }

    public Domain getLeft() {
        return left;
    }

    public Domain getRight() {
        return right;
    }

    @Override
    public boolean test(BigDecimal value) {
        return left.test(value) && right.test(value);
    }
}
