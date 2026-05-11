package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.service.DefaultExpirationPolicy;
import com.snackoverflow.fridgemate.service.FridgeMateManager;
import com.snackoverflow.fridgemate.service.Inventory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FridgeMateManagerTest {
    private FridgeMateManager manager() {
        return new FridgeMateManager(
                new Inventory(new DefaultExpirationPolicy(7)),
                new GroceryList()
        );
    }

    private FoodItem item(String name, FoodCategory category, StorageLocation location, int qty, LocalDate exp) {
        return new FoodItem(name, "", category, location, qty, LocalDate.of(2026, 5, 1), exp);
    }

    @Test
    void addRemoveAndFilterFlow() {
        FridgeMateManager manager = manager();
        FoodItem milk = item("Milk", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1, LocalDate.of(2026, 5, 8));
        FoodItem rice = item("Rice", FoodCategory.GRAINS, StorageLocation.PANTRY, 3, LocalDate.of(2026, 12, 1));

        manager.addFoodItem(milk);
        manager.addFoodItem(rice);

        assertEquals(2, manager.getInventoryItems().size());
        assertEquals(List.of(milk), manager.filterInventory(FoodCategory.DAIRY, StorageLocation.FRIDGE));

        assertTrue(manager.removeFoodItemById(milk.getId()));
        assertEquals(1, manager.getInventoryItems().size());
    }

    @Test
    void expiringAndLowStockFlow() {
        FridgeMateManager manager = manager();
        FoodItem yogurt = item("Yogurt", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1, LocalDate.of(2026, 5, 6));
        FoodItem pasta = item("Pasta", FoodCategory.GRAINS, StorageLocation.PANTRY, 3, LocalDate.of(2026, 8, 1));

        manager.addFoodItem(yogurt);
        manager.addFoodItem(pasta);

        assertEquals(List.of(yogurt), manager.getExpiringItems(LocalDate.of(2026, 5, 1)));

        int firstAdded = manager.addLowStockItemsToGrocery(1);
        int secondAdded = manager.addLowStockItemsToGrocery(1);

        assertEquals(1, firstAdded);
        assertEquals(0, secondAdded);
        assertEquals(List.of("Yogurt"), manager.getGroceryItems());
    }
}
