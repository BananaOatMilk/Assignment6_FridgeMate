package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Inventory {
    private final List<FoodItem> items = new ArrayList<>();
    private final ExpirationPolicy expirationPolicy;

    public Inventory(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy = Objects.requireNonNull(expirationPolicy, "expirationPolicy is required");
    }

    public void addItem(FoodItem item) {
        items.add(Objects.requireNonNull(item, "item is required"));
    }

    public boolean removeItemById(String id) {
        return items.removeIf(item -> item.getId().equals(id));
    }

    public Optional<FoodItem> findById(String id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    public List<FoodItem> getAllItems() {
        return Collections.unmodifiableList(items);
    }

    public void replaceAll(Collection<FoodItem> newItems) {
        items.clear();
        items.addAll(newItems);
    }

    public List<FoodItem> filter(FoodCategory category, StorageLocation location) {
        return items.stream()
                .filter(item -> category == null || item.getCategory() == category)
                .filter(item -> location == null || item.getLocation() == location)
                .sorted(Comparator.comparing(FoodItem::getExpirationDate))
                .collect(Collectors.toList());
    }

    public List<FoodItem> expiringSoon(LocalDate today) {
        return items.stream()
                .filter(item -> expirationPolicy.isExpiringSoon(item, today))
                .sorted(Comparator.comparing(FoodItem::getExpirationDate))
                .collect(Collectors.toList());
    }

    public List<FoodItem> lowStock(int threshold) {
        return items.stream()
                .filter(item -> item.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    public Map<FoodCategory, List<FoodItem>> byCategory() {
        return items.stream().collect(Collectors.groupingBy(FoodItem::getCategory));
    }
}
