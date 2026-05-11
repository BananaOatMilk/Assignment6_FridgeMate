package com.snackoverflow.fridgemate.storage;

import com.snackoverflow.fridgemate.model.FoodItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CsvShare {
    public void exportGroceryList(List<String> groceryItems, Collection<FoodItem> inventoryItems, Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("No.,Item,Location,Previous Qty");

        for (int i = 0; i < groceryItems.size(); i++) {
            String groceryItem = groceryItems.get(i);
            Optional<FoodItem> match = findByName(inventoryItems, groceryItem);
            String location = match.map(item -> item.getLocation().name()).orElse("");
            String qty = match.map(item -> String.valueOf(item.getQuantity())).orElse("");

            lines.add((i + 1)
                    + "," + escapeCsv(groceryItem)
                    + "," + escapeCsv(location)
                    + "," + escapeCsv(qty));
        }

        Files.write(path, lines);
    }

    private Optional<FoodItem> findByName(Collection<FoodItem> inventoryItems, String itemName) {
        if (itemName == null) {
            return Optional.empty();
        }
        String name = itemName.trim().toLowerCase(Locale.ROOT);
        return inventoryItems.stream()
                .filter(item -> item.getName().trim().toLowerCase(Locale.ROOT).equals(name))
                .findFirst();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String cleaned = value.replace("\r", " ").replace("\n", " ");
        boolean needsQuotes = cleaned.contains(",") || cleaned.contains("\"");
        String escaped = cleaned.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
