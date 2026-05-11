package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.service.DefaultExpirationPolicy;
import com.snackoverflow.fridgemate.service.GroceryPlanner;
import com.snackoverflow.fridgemate.service.Inventory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroceryPlannerTest {
    private FoodItem item(String name, int quantity) {
        return new FoodItem(
                name, FoodCategory.OTHER,
                StorageLocation.PANTRY,
                quantity,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 6, 1)
        );
    }

    @Test
    void addLowStockItemsMovesOnlyMatchingItemsAndAvoidsDuplicates() {
        Inventory inventory = new Inventory(new DefaultExpirationPolicy(7));
        GroceryList groceryList = new GroceryList();
        GroceryPlanner planner = new GroceryPlanner(inventory, groceryList);

        inventory.addItem(item("Rice", 1));
        inventory.addItem(item("Pasta", 3));
        inventory.addItem(item("Beans", 1));

        int firstAdded = planner.addLowStockItems(1);
        int secondAdded = planner.addLowStockItems(1);

        assertEquals(2, firstAdded);
        assertEquals(0, secondAdded);
        assertEquals(List.of("Rice", "Beans"), groceryList.getItems());
    }
}
