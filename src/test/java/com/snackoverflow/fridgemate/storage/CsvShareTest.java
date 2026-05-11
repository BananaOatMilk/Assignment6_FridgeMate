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

class CsvShareTest {
    @Test
    void exportGroceryListIncludesLocationAndPreviousQty(@TempDir Path tempDir) throws IOException {
        CsvShare csv = new CsvShare();
        Path file = tempDir.resolve("grocery.csv");

        List<FoodItem> inventory = List.of(
                new FoodItem("Milk", "", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 8)),
                new FoodItem("Eggs", "", FoodCategory.PROTEIN, StorageLocation.FRIDGE, 2,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 20))
        );

        csv.exportGroceryList(List.of("Milk", "Eggs", "Bananas"), inventory, file);
        List<String> lines = Files.readAllLines(file);

        assertEquals(
                List.of(
                        "No.,Item,Location,Previous Qty",
                        "1,Milk,FRIDGE,1",
                        "2,Eggs,FRIDGE,2",
                        "3,Bananas,,"
                ),
                lines
        );
    }

    @Test
    void exportGroceryListMatchesInventoryCaseInsensitiveAndEscapesCommas(@TempDir Path tempDir) throws IOException {
        CsvShare csv = new CsvShare();
        Path file = tempDir.resolve("grocery-escaped.csv");

        List<FoodItem> inventory = List.of(
                new FoodItem("greek yogurt", "", FoodCategory.DAIRY, StorageLocation.FRIDGE, 2,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 20))
        );

        csv.exportGroceryList(List.of("Greek Yogurt", "bread, whole wheat"), inventory, file);
        List<String> lines = Files.readAllLines(file);

        assertEquals("1,Greek Yogurt,FRIDGE,2", lines.get(1));
        assertEquals("2,\"bread, whole wheat\",,", lines.get(2));
    }
}
