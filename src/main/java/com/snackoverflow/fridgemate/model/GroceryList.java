package com.snackoverflow.fridgemate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroceryList {
    private final List<String> items = new ArrayList<>();

    public void addItem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Grocery item is required");
        }

        String cleaned = name.trim();
        if (items.stream().noneMatch(item -> item.equalsIgnoreCase(cleaned))) {
            items.add(cleaned);
        }
    }

    public void removeItem(String name) {
        if (name == null) {
            return;
        }
        items.removeIf(item -> item.equalsIgnoreCase(name));
    }

    public void clear() {
        items.clear();
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }
}
