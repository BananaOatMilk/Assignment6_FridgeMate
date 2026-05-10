package com.snackoverflow.fridgemate.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class FoodItem {
    private final String id;
    private String name;
    private String description;
    private FoodCategory category;
    private StorageLocation location;
    private int quantity;
    private LocalDate dateAdded;
    private LocalDate expirationDate;

    public FoodItem(String name,
                    String description,
                    FoodCategory category,
                    StorageLocation location,
                    int quantity,
                    LocalDate dateAdded,
                    LocalDate expirationDate) {
        this(UUID.randomUUID().toString(), name, description, category, location, quantity, dateAdded, expirationDate);
    }

    public FoodItem(String id,
                    String name,
                    String description,
                    FoodCategory category,
                    StorageLocation location,
                    int quantity,
                    LocalDate dateAdded,
                    LocalDate expirationDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        this.id = id;
        setName(name);
        setDescription(description);
        setCategory(category);
        setLocation(location);
        setQuantity(quantity);
        setDateAdded(dateAdded);
        setExpirationDate(expirationDate);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public void setCategory(FoodCategory category) {
        this.category = Objects.requireNonNull(category, "Category is required");
    }

    public void setLocation(StorageLocation location) {
        this.location = Objects.requireNonNull(location, "Location is required");
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = Objects.requireNonNull(dateAdded, "Date added is required");
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = Objects.requireNonNull(expirationDate, "Expiration date is required");
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
}
