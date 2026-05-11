package com.snackoverflow.fridgemate.storage;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalJsonStorageTest {
    @Test
    void loadMissingFileReturnsEmptyData(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("missing.json");
        LocalJsonStorage storage = new LocalJsonStorage(file);

        AppData loaded = storage.load();

        assertEquals(List.of(), loaded.getItems());
        assertEquals(List.of(), loaded.getGroceryItems());
    }

    @Test
    void saveAndLoadRoundTrip(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("fridgemate-data.json");
        LocalJsonStorage storage = new LocalJsonStorage(file);

        AppData toSave = new AppData();
        toSave.setItems(List.of(new FoodItem(
                "Milk",
                "",
                FoodCategory.DAIRY,
                StorageLocation.FRIDGE,
                1,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 8)
        )));
        toSave.setGroceryItems(List.of("Eggs", "Rice"));

        storage.save(toSave);
        AppData loaded = storage.load();

        assertEquals(1, loaded.getItems().size());
        assertEquals("Milk", loaded.getItems().get(0).getName());
        assertEquals(List.of("Eggs", "Rice"), loaded.getGroceryItems());
    }

    @Test
    void loadCorruptedFileThrowsIOException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bad.json");
        Files.writeString(file, "{ definitely-not-valid-json");

        LocalJsonStorage storage = new LocalJsonStorage(file);
        assertThrows(IOException.class, storage::load);
    }
}
