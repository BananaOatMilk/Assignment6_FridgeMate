package com.snackoverflow.fridgemate.storage;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvShareTest {
    @Test
    void exportAndImportRoundTrip(@TempDir Path tempDir) throws IOException {
        CsvShare csv = new CsvShare();
        Path file = tempDir.resolve("share.csv");

        List<FoodItem> items = List.of(
                new FoodItem("Rice", "", FoodCategory.GRAINS, StorageLocation.PANTRY, 2,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 12, 1)),
                new FoodItem("Yogurt", "", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 9))
        );

        csv.exportItems(items, file);
        List<FoodItem> imported = csv.importItems(file);

        assertEquals(2, imported.size());
        assertEquals("Rice", imported.get(0).getName());
        assertEquals(FoodCategory.GRAINS, imported.get(0).getCategory());
        assertEquals("Yogurt", imported.get(1).getName());
        assertEquals(StorageLocation.FRIDGE, imported.get(1).getLocation());
    }
}
