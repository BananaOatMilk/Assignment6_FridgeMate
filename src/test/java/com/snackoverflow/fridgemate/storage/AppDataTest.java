package com.snackoverflow.fridgemate.storage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppDataTest {
    @Test
    void nullSettersFallBackToEmptyLists() {
        AppData data = new AppData();
        data.setItems(null);
        data.setGroceryItems(null);

        assertEquals(List.of(), data.getItems());
        assertEquals(List.of(), data.getGroceryItems());
    }
}
