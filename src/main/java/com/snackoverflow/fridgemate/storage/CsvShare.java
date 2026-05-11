package com.snackoverflow.fridgemate.storage;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvShare {
    public void exportItems(List<FoodItem> items, Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,description,category,location,quantity,dateAdded,expirationDate");

        for (FoodItem item : items) {
            lines.add(String.join(",",
                    escape(item.getId()),
                    escape(item.getName()),
                    escape(item.getDescription()),
                    item.getCategory().name(),
                    item.getLocation().name(),
                    String.valueOf(item.getQuantity()),
                    item.getDateAdded().toString(),
                    item.getExpirationDate().toString()));
        }

        Files.write(path, lines);
    }

    public List<FoodItem> importItems(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<FoodItem> items = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",", -1);
            if (parts.length < 8) {
                continue;
            }

            FoodItem item = new FoodItem(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    FoodCategory.valueOf(parts[3]),
                    StorageLocation.valueOf(parts[4]),
                    Integer.parseInt(parts[5]),
                    LocalDate.parse(parts[6]),
                    LocalDate.parse(parts[7]));
            items.add(item);
        }

        return items;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ");
    }

    private String unescape(String value) {
        return value;
    }
}
