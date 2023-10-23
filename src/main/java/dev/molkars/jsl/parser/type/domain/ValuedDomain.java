package dev.molkars.jsl.parser.type.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;

public class ValuedDomain extends Domain {
    public TreeSet<BigDecimal> values;

    public ValuedDomain(Collection<BigDecimal> values) {
        Objects.requireNonNull(values);
        this.values = new TreeSet<>();
        this.values.addAll(values);
    }

    @Override
    public boolean test(BigDecimal value) {
        return values.contains(value);
    }
}
