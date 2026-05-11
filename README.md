# Assignment6_FridgeMate

This is our fresh start version for Assignment 6.

## Where we are now

- We finished the inventory backbone first.
- Main code is in `src/main/java/com/snackoverflow/fridgemate`.
- Tests are in `src/test/java/com/snackoverflow/fridgemate`.

## What works so far

- `FoodItem` with basic validation:
  - name is required
  - quantity must be at least 1
  - category/location/dates are required
- Enums for `FoodCategory` and `StorageLocation`
- `Inventory` methods:
  - add/remove/find
  - filter by category and location
  - check expiring soon
  - check low stock
- `GroceryList` methods:
  - add/remove items
  - prevent duplicate items (case-insensitive)
- `GroceryPlanner`:
  - add low-stock inventory items into grocery list
- JavaFX app shell:
  - add/delete inventory items
  - add low-stock items to grocery list
  - filter inventory by category/location
  - view expiring-soon items
- Data features:
  - save/load app data to `fridgemate-data.json`
  - export/import inventory as CSV

## Run tests

```bash
mvn test
```

## Run app

```bash
mvn javafx:run
```

## Next steps

- Add recipe suggestions
