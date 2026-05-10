package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.GroceryList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroceryListTest {
    @Test
    void addItemPreventsCaseInsensitiveDuplicates() {
        GroceryList groceryList = new GroceryList();

        groceryList.addItem("Milk");
        groceryList.addItem("milk");
        groceryList.addItem("  MILK  ");

        assertEquals(List.of("Milk"), groceryList.getItems());
    }

    @Test
    void removeItemIsCaseInsensitive() {
        GroceryList groceryList = new GroceryList();
        groceryList.addItem("Eggs");

        groceryList.removeItem("eggs");

        assertEquals(List.of(), groceryList.getItems());
    }

    @Test
    void addItemRejectsBlankValues() {
        GroceryList groceryList = new GroceryList();

        assertThrows(IllegalArgumentException.class, () -> groceryList.addItem(" "));
    }
}
