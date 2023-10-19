package dev.molkars.jsl.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypedStore {
    public static class Key<T> {
        final String name;

        public Key(String name) {
            this.name = name;
        }
    }

    private final Map<Key<?>, Object> map = new ConcurrentHashMap<>();

    public <T> void put(Key<T> key, T value) {
        map.put(key, value);
    }

    public <T> T get(Key<T> key) {
        return (T) map.get(key);
    }
}
