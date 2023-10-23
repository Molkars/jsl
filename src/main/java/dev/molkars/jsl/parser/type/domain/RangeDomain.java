package dev.molkars.jsl.parser.type.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class RangeDomain extends Domain {
    boolean startInclusive, endInclusive;
    BigDecimal start, end;

    public RangeDomain(boolean startInclusive, boolean endInclusive, BigDecimal start, BigDecimal end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        this.startInclusive = startInclusive;
        this.endInclusive = endInclusive;
        this.start = start;
        this.end = end;
    }

    public boolean isStartInclusive() {
        return startInclusive;
    }

    public boolean isEndInclusive() {
        return endInclusive;
    }

    public BigDecimal getStart() {
        return start;
    }

    public BigDecimal getEnd() {
        return end;
    }

    @Override
    public boolean test(BigDecimal value) {
        boolean out;

        int compare = value.compareTo(start);
        if (startInclusive) {
            out = compare >= 0;
        } else {
            out = compare > 0;
        }

        compare = value.compareTo(end);
        if (endInclusive) {
            out = out && compare <= 0;
        } else {
            out = out && compare < 0;
        }

        return out;
    }
}
