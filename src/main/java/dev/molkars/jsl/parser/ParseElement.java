package dev.molkars.jsl.parser;

import dev.molkars.jsl.tokenizer.Token;

import java.util.*;

public abstract class ParseElement {
    private final LinkedList<ParseElement> children = new LinkedList<>();
    private Token start;
    private Token end;

    protected ParseElement(List<ParseElement> children) {
        this(null, null, children);
    }

    protected <T extends ParseElement> T addChild(T child) {
        children.add(child);
        return child;
    }

    protected ParseElement(Token start, Token end, Collection<ParseElement> children) {
        this.start = start;
        this.end = end;
        this.children.addAll(children);
    }

    protected ParseElement() {
    }

    protected ParseElement(Token token) {
        start = end = Objects.requireNonNull(token);
    }

    protected ParseElement(Token start, Token end) {
        this.start = start;
        this.end = end;
    }

    public Collection<ParseElement> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public Token getStart() {
        if (start == null) {
            start = children.getFirst().getStart();
        }
        return Objects.requireNonNull(start, "setToken must be called on leaf elements!");
    }

    public Token getEnd() {
        if (end == null) {
            end = children.getLast().getEnd();
        }
        return Objects.requireNonNull(end, "setToken must be called on leaf elements!");
    }
}
