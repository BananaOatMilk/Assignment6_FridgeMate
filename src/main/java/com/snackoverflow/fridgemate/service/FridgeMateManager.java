package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;
import com.snackoverflow.fridgemate.model.StorageLocation;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class FridgeMateManager {
    private final Inventory inventory;
    private final GroceryList groceryList;
    private final GroceryPlanner groceryPlanner;

    public FridgeMateManager(Inventory inventory, GroceryList groceryList) {
        this.inventory = Objects.requireNonNull(inventory, "Inventory is required");
        this.groceryList = Objects.requireNonNull(groceryList, "Grocery list is required");
        this.groceryPlanner = new GroceryPlanner(inventory, groceryList);
    }

    public void addFoodItem(FoodItem item) {
        inventory.addItem(item);
    }

    public boolean removeFoodItemById(String id) {
        return inventory.removeItemById(id);
    }

    public List<FoodItem> getInventoryItems() {
        return inventory.getAllItems();
    }

    public List<FoodItem> filterInventory(FoodCategory category, StorageLocation location) {
        return inventory.filter(category, location);
    }

    public List<FoodItem> getExpiringItems(LocalDate today) {
        return inventory.expiringSoon(today);
    }

    public List<String> getGroceryItems() {
        return groceryList.getItems();
    }

    public int addLowStockItemsToGrocery(int threshold) {
        return groceryPlanner.addLowStockItems(threshold);
    }

    public void addGroceryItem(String name) {
        groceryList.addItem(name);
    }

    public void removeGroceryItem(String name) {
        groceryList.removeItem(name);
    }
}
