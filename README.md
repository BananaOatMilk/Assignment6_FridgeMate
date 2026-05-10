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

## Run tests

```bash
mvn test
```

## Next steps

- Add grocery list model + tests
- Add first JavaFX screen
- Add save/load
