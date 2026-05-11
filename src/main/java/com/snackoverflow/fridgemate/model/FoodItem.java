package com.snackoverflow.fridgemate.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class FoodItem {
    private final String id;
    private String name;
    private FoodCategory category;
    private StorageLocation location;
    private int quantity;
    private LocalDate dateAdded;
    private LocalDate expirationDate;

    public FoodItem(String name,
                    FoodCategory category,
                    StorageLocation location,
                    int quantity,
                    LocalDate dateAdded,
                    LocalDate expirationDate) {
        this(UUID.randomUUID().toString(), name, category, location, quantity, dateAdded, expirationDate);
    }

    public FoodItem(String id,
                    String name,
                    FoodCategory category,
                    StorageLocation location,
                    int quantity,
                    LocalDate dateAdded,
                    LocalDate expirationDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        this.id = id;
        this.name = requireName(name);
        this.category = requireCategory(category);
        this.location = requireLocation(location);
        this.quantity = requireQuantity(quantity);
        this.dateAdded = requireDateAdded(dateAdded);
        this.expirationDate = requireExpirationDate(expirationDate);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public StorageLocation getLocation() {
        return location;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setName(String name) {
        this.name = requireName(name);
    }

    public void setCategory(FoodCategory category) {
        this.category = requireCategory(category);
    }

    public void setLocation(StorageLocation location) {
        this.location = requireLocation(location);
    }

    public void setQuantity(int quantity) {
        this.quantity = requireQuantity(quantity);
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = requireDateAdded(dateAdded);
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = requireExpirationDate(expirationDate);
    }

    public long daysUntilExpiration(LocalDate today) {
        return ChronoUnit.DAYS.between(today, expirationDate);
    }

    public boolean isExpired(LocalDate today) {
        return daysUntilExpiration(today) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FoodItem foodItem)) {
            return false;
        }
        return id.equals(foodItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        return name.trim();
    }

    private static FoodCategory requireCategory(FoodCategory category) {
        return Objects.requireNonNull(category, "Category is required");
    }

    private static StorageLocation requireLocation(StorageLocation location) {
        return Objects.requireNonNull(location, "Location is required");
    }

    private static int requireQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        return quantity;
    }

    private static LocalDate requireDateAdded(LocalDate dateAdded) {
        return Objects.requireNonNull(dateAdded, "Date added is required");
    }

    private static LocalDate requireExpirationDate(LocalDate expirationDate) {
        return Objects.requireNonNull(expirationDate, "Expiration date is required");
    }
}
