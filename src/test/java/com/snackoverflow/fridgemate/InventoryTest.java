package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.service.DefaultExpirationPolicy;
import com.snackoverflow.fridgemate.service.Inventory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {
    private FoodItem item(String name, FoodCategory category, StorageLocation location, int quantity, LocalDate expirationDate) {
        return new FoodItem(name, category, location, quantity, LocalDate.of(2026, 5, 1), expirationDate);
    }

    @Test
    void addFilterAndRemoveItem() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
        FoodItem milk = item("Milk", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1, LocalDate.of(2026, 5, 8));
        FoodItem rice = item("Rice", FoodCategory.GRAINS, StorageLocation.PANTRY, 3, LocalDate.of(2027, 1, 1));

        inventory.addItem(milk);
        inventory.addItem(rice);

        assertEquals(2, inventory.getAllItems().size());
        assertEquals(List.of(milk), inventory.filter(FoodCategory.DAIRY, StorageLocation.FRIDGE));
        assertTrue(inventory.removeItemById(milk.getId()));
        assertEquals(1, inventory.getAllItems().size());
    }

    @Test
    void expiringSoonUsesPolicy() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
        FoodItem yogurt = item("Yogurt", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1, LocalDate.of(2026, 5, 6));
        FoodItem pasta = item("Pasta", FoodCategory.GRAINS, StorageLocation.PANTRY, 1, LocalDate.of(2026, 8, 1));

        inventory.addItem(yogurt);
        inventory.addItem(pasta);

        assertEquals(List.of(yogurt), inventory.expiringSoon(LocalDate.of(2026, 5, 1)));
    }

    @Test
    void findByIdLowStockAndByCategoryWork() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
        FoodItem eggs = item("Eggs", FoodCategory.PROTEIN, StorageLocation.FRIDGE, 1, LocalDate.of(2026, 5, 10));
        FoodItem oats = item("Oats", FoodCategory.GRAINS, StorageLocation.PANTRY, 4, LocalDate.of(2026, 12, 1));

        inventory.addItem(eggs);
        inventory.addItem(oats);

        assertTrue(inventory.findById(eggs.getId()).isPresent());
        assertEquals(List.of(eggs), inventory.lowStock(1));

        Map<FoodCategory, List<FoodItem>> grouped = inventory.byCategory();
        assertEquals(List.of(eggs), grouped.get(FoodCategory.PROTEIN));
        assertEquals(List.of(oats), grouped.get(FoodCategory.GRAINS));
    }

    @Test
    void invalidItemInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new FoodItem("", FoodCategory.OTHER, StorageLocation.PANTRY, 1, LocalDate.now(), LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () ->
                new FoodItem("Beans", FoodCategory.OTHER, StorageLocation.PANTRY, 0, LocalDate.now(), LocalDate.now()));
    }
    @Test
    void removingUnknownIdReturnsFalse() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
    
        assertTrue(inventory.getAllItems().isEmpty());
        assertEquals(false, inventory.removeItemById("fake-id"));
    }
    @Test
    void removingUnknownIdReturnsFalse() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
    
        assertFalse(inventory.removeItemById("fake-id"));
    }
    @Test
    void filterWithNoMatchesReturnsEmptyList() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
        FoodItem milk = item("Milk", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1,
                LocalDate.of(2026, 5, 8));
    
        inventory.addItem(milk);
    
        assertEquals(List.of(), inventory.filter(FoodCategory.PROTEIN, StorageLocation.PANTRY));
    }
}
