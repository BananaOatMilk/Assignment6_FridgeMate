package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;

import java.util.Objects;

public class GroceryPlanner {
    private final Inventory inventory;
    private final GroceryList groceryList;

    public GroceryPlanner(Inventory inventory, GroceryList groceryList) {
        this.inventory = Objects.requireNonNull(inventory, "Inventory is required");
        this.groceryList = Objects.requireNonNull(groceryList, "Grocery list is required");
    }

    public int addLowStockItems(int threshold) {
        int before = groceryList.getItems().size();

        for (FoodItem item : inventory.lowStock(threshold)) {
            groceryList.addItem(item.getName());
        }

        return groceryList.getItems().size() - before;
    }
}
