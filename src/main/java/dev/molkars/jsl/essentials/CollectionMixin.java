package dev.molkars.jsl.essentials;

import java.util.Collection;
import java.util.Iterator;

public interface CollectionMixin<T> extends Collection<T> {
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    default <T1> T1[] toArray(T1[] a) {
        if (a.length < size()) {
            a = (T1[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
        }
        Iterator<T> iterator = iterator();
        for (int i = 0; i < a.length; i++) {
            if (!iterator.hasNext()) {
                if (a != null && a.length > i) {
                    a[i] = null;
                }
                return a;
            }
            a[i] = (T1) iterator.next();
        }
        assert !iterator.hasNext();
        return a;
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T t : c) {
            if (add(t))
                modified = true;
        }
        return modified;
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            if (remove(o))
                modified = true;
        }
        return modified;
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            if (!c.contains(iterator.next())) {
                iterator.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    default void clear() {
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}
