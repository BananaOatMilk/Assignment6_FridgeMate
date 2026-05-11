package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.storage.AppData;
import com.snackoverflow.fridgemate.storage.CsvShare;
import com.snackoverflow.fridgemate.storage.Storage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class FridgeMateManager {
    private final Inventory inventory;
    private final GroceryList groceryList;
    private final GroceryPlanner groceryPlanner;
    private final Storage storage;
    private final CsvShare csvShare;

    public FridgeMateManager(Inventory inventory, GroceryList groceryList) {
        this(inventory, groceryList, null, null);
    }

    public FridgeMateManager(Inventory inventory,
                             GroceryList groceryList,
                             Storage storage,
                             CsvShare csvShare) {
        this.inventory = Objects.requireNonNull(inventory, "Inventory is required");
        this.groceryList = Objects.requireNonNull(groceryList, "Grocery list is required");
        this.groceryPlanner = new GroceryPlanner(inventory, groceryList);
        this.storage = storage;
        this.csvShare = csvShare;
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

    public void load() throws IOException {
        requireStorage();
        AppData data = storage.load();
        inventory.replaceAll(data.getItems());
        groceryList.clear();
        data.getGroceryItems().forEach(groceryList::addItem);
    }

    public void save() throws IOException {
        requireStorage();
        AppData data = new AppData();
        data.setItems(inventory.getAllItems());
        data.setGroceryItems(groceryList.getItems());
        storage.save(data);
    }

    public void exportGroceryList(Path path) throws IOException {
        requireCsvShare();
        csvShare.exportGroceryList(groceryList.getItems(), inventory.getAllItems(), path);
    }

    private void requireStorage() {
        if (storage == null) {
            throw new IllegalStateException("Storage service is not configured");
        }
    }

    private void requireCsvShare() {
        if (csvShare == null) {
            throw new IllegalStateException("CSV share service is not configured");
        }
    }
}
