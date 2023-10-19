package dev.molkars.jsl.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlotProvider {
    final List<TypeRef> slots = new ArrayList<>();
    final HashMap<String, Integer> namedSlots = new HashMap<>();

    public int createUnnamedSlot(TypeRef ref) {
        slots.add(ref);
        return slots.size() - 1;
    }

    public int createNamedSlot(String name, TypeRef ref) {
        if (namedSlots.containsKey(name)) {
            throw new IllegalStateException("named slot already exists: " + name);
        }
        namedSlots.put(name, slots.size());
        slots.add(ref);
        return slots.size() - 1;
    }

    public TypeRef getSlotType(int slot) {
        return slots.get(slot);
    }

    public int getSlot(String name) {
        if (!namedSlots.containsKey(name)) {
            throw new IllegalArgumentException("no slot named " + name);
        }
        return namedSlots.get(name);
    }

    public TypeRef getSlotType(String name) {
        Integer slot = namedSlots.get(name);
        if (slot == null) return null;
        return slots.get(slot);
    }
}
