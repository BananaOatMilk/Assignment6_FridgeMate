package com.snackoverflow.fridgemate.storage;

import com.snackoverflow.fridgemate.model.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private List<FoodItem> items = new ArrayList<>();
    private List<String> groceryItems = new ArrayList<>();

    public List<FoodItem> getItems() {
        return items;
    }

    public void setItems(List<FoodItem> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    public List<String> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<String> groceryItems) {
        this.groceryItems = groceryItems == null ? new ArrayList<>() : new ArrayList<>(groceryItems);
    }
}
