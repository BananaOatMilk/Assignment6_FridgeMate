package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.GroceryList;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.service.DefaultExpirationPolicy;
import com.snackoverflow.fridgemate.service.FridgeMateManager;
import com.snackoverflow.fridgemate.service.Inventory;
import com.snackoverflow.fridgemate.storage.CsvShare;
import com.snackoverflow.fridgemate.storage.LocalJsonStorage;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Function;

public class FridgeMateApp extends Application {
    private static final String CARD_STYLE =
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: #d8dde6;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 12;";

    private static final String SECTION_TITLE_STYLE =
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;";

    private final FridgeMateManager manager = new FridgeMateManager(
            new Inventory(new DefaultExpirationPolicy(7)),
            new GroceryList(),
            new LocalJsonStorage(Path.of("fridgemate-data.json")),
            new CsvShare()
    );

    private final ObservableList<FoodItem> inventoryRows = FXCollections.observableArrayList();
    private final ObservableList<String> groceryRows = FXCollections.observableArrayList();
    private final ObservableList<FoodItem> expiringRows = FXCollections.observableArrayList();

    private TableView<FoodItem> inventoryTable;
    private ComboBox<FoodCategory> filterCategoryBox;
    private ComboBox<String> viewModeBox;
    private ComboBox<StorageLocation> viewLocationBox;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("FridgeMate");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Label subtitle = new Label("Track inventory, grocery needs, and expiring items");
        subtitle.setStyle("-fx-text-fill: #5c6678;");
        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(0, 0, 8, 2));
        root.setTop(header);

        inventoryTable = createInventoryTable();
        VBox inventoryPanel = createInventoryPanel(stage);
        VBox groceryPanel = createGroceryPanel();
        VBox expiringPanel = createExpiringPanel();

        HBox center = new HBox(14, inventoryPanel, groceryPanel, expiringPanel);
        root.setCenter(center);

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        BorderPane.setMargin(statusLabel, new Insets(8, 2, 0, 2));
        root.setBottom(statusLabel);

        try {
            manager.load();
            setStatus("Loaded saved data");
        } catch (IOException ex) {
            setStatus("No saved data loaded");
        }

        refreshViews();

        Scene scene = new Scene(root, 1220, 620);
        stage.setTitle("FridgeMate");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<FoodItem> createInventoryTable() {
        TableView<FoodItem> table = new TableView<>();
        table.setItems(inventoryRows);
        table.getColumns().add(column("Name", FoodItem::getName));
        table.getColumns().add(column("Category", item -> item.getCategory().name()));
        table.getColumns().add(column("Location", item -> item.getLocation().name()));
        table.getColumns().add(column("Qty", item -> String.valueOf(item.getQuantity())));
        table.getColumns().add(column("Expires", item -> item.getExpirationDate().toString()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(420);
        return table;
    }

    private VBox createInventoryPanel(Stage stage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Item name");

        ComboBox<FoodCategory> categoryBox = new ComboBox<>(FXCollections.observableArrayList(FoodCategory.values()));
        categoryBox.setValue(FoodCategory.OTHER);

        ComboBox<StorageLocation> locationBox = new ComboBox<>(FXCollections.observableArrayList(StorageLocation.values()));
        locationBox.setValue(StorageLocation.PANTRY);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 999, 1);
        DatePicker expirationPicker = new DatePicker(LocalDate.now().plusDays(7));

        filterCategoryBox = new ComboBox<>(FXCollections.observableArrayList(FoodCategory.values()));
        filterCategoryBox.setPromptText("Filter category");

        viewModeBox = new ComboBox<>(FXCollections.observableArrayList("View all", "View by location"));
        viewModeBox.setValue("View all");
        viewLocationBox = new ComboBox<>(FXCollections.observableArrayList(StorageLocation.values()));
        viewLocationBox.setPromptText("Select location");
        viewLocationBox.setDisable(true);

        viewModeBox.setOnAction(event -> {
            boolean byLocation = "View by location".equals(viewModeBox.getValue());
            viewLocationBox.setDisable(!byLocation);
            if (!byLocation) {
                viewLocationBox.setValue(null);
            }
            applyCurrentFilter();
        });

        viewLocationBox.setOnAction(event -> applyCurrentFilter());

        Button applyFilterButton = new Button("Apply Filter");
        applyFilterButton.setOnAction(event -> applyCurrentFilter());

        Button clearFilterButton = new Button("Clear Filter");
        clearFilterButton.setOnAction(event -> {
            filterCategoryBox.setValue(null);
            viewModeBox.setValue("View all");
            viewLocationBox.setValue(null);
            viewLocationBox.setDisable(true);
            applyCurrentFilter();
            setStatus("Cleared filters");
        });

        Button addButton = new Button("Add Item");
        addButton.setMinWidth(88);
        addButton.setOnAction(event -> {
            try {
                FoodItem item = new FoodItem(
                        nameField.getText(), categoryBox.getValue(),
                        locationBox.getValue(),
                        quantitySpinner.getValue(),
                        LocalDate.now(),
                        expirationPicker.getValue()
                );
                manager.addFoodItem(item);
                nameField.clear();
                quantitySpinner.getValueFactory().setValue(1);
                expirationPicker.setValue(LocalDate.now().plusDays(7));
                refreshViews();
                setStatus("Added " + item.getName());
            } catch (RuntimeException ex) {
                setStatus("Error: " + ex.getMessage());
            }
        });

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setMinWidth(110);
        deleteButton.setOnAction(event -> {
            FoodItem selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                setStatus("Select an item to delete");
                return;
            }
            manager.removeFoodItemById(selected.getId());
            refreshViews();
            setStatus("Deleted " + selected.getName());
        });

        Button saveButton = new Button("Save");
        saveButton.setMinWidth(70);
        saveButton.setOnAction(event -> {
            try {
                manager.save();
                setStatus("Saved data");
            } catch (IOException ex) {
                setStatus("Save failed: " + ex.getMessage());
            }
        });

        Button loadButton = new Button("Load");
        loadButton.setMinWidth(70);
        loadButton.setOnAction(event -> {
            try {
                manager.load();
                refreshViews();
                setStatus("Loaded data");
            } catch (IOException ex) {
                setStatus("Load failed: " + ex.getMessage());
            }
        });

        Button exportButton = new Button("Export Grocery CSV");
        exportButton.setMinWidth(95);
        exportButton.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export grocery list CSV");
            chooser.setInitialFileName("fridgemate-grocery.csv");
            File file = chooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }
            try {
                manager.exportGroceryList(file.toPath());
                setStatus("Exported grocery CSV");
            } catch (IOException ex) {
                setStatus("Export failed: " + ex.getMessage());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.addRow(0, new Label("Name"), nameField);
        form.addRow(1, new Label("Category"), categoryBox);
        form.addRow(2, new Label("Location"), locationBox);
        form.addRow(3, new Label("Quantity"), quantitySpinner);
        form.addRow(4, new Label("Expiration"), expirationPicker);

        HBox filterRow = new HBox(8, viewModeBox, viewLocationBox, filterCategoryBox, applyFilterButton, clearFilterButton);
        HBox actions = new HBox(8, addButton, deleteButton, saveButton, loadButton, exportButton);

        Label sectionTitle = new Label("Inventory");
        sectionTitle.setStyle(SECTION_TITLE_STYLE);

        VBox inventoryViewContent = new VBox(10, filterRow, inventoryTable);
        TitledPane viewInventoryPane = new TitledPane("View Inventory", inventoryViewContent);
        viewInventoryPane.setExpanded(false);
        viewInventoryPane.setAnimated(false);

        VBox panel = new VBox(10, sectionTitle, form, actions, viewInventoryPane);
        panel.setPrefWidth(650);
        panel.setStyle(CARD_STYLE);
        return panel;
    }

    private VBox createGroceryPanel() {
        ListView<String> groceryListView = new ListView<>(groceryRows);
        groceryListView.setPrefHeight(480);

        Button addLowStock = new Button("Add Low-Stock Items");
        addLowStock.setMaxWidth(Double.MAX_VALUE);
        addLowStock.setOnAction(event -> {
            int added = manager.addLowStockItemsToGrocery(1);
            refreshViews();
            setStatus("Added " + added + " low-stock item(s)");
        });

        Label sectionTitle = new Label("Grocery List");
        sectionTitle.setStyle(SECTION_TITLE_STYLE);

        VBox panel = new VBox(10, sectionTitle, addLowStock, groceryListView);
        panel.setPrefWidth(260);
        panel.setStyle(CARD_STYLE);
        return panel;
    }

    private VBox createExpiringPanel() {
        ListView<FoodItem> expiringList = new ListView<>(expiringRows);
        expiringList.setPrefHeight(480);
        expiringList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(FoodItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    long days = item.daysUntilExpiration(LocalDate.now());
                    setText(item.getName() + " (" + item.getExpirationDate() + ", " + days + " day(s) left)");
                }
            }
        });

        Button refreshButton = new Button("Refresh Expiring");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        refreshButton.setOnAction(event -> refreshViews());

        Label sectionTitle = new Label("Expiring Soon");
        sectionTitle.setStyle(SECTION_TITLE_STYLE);

        VBox panel = new VBox(10, sectionTitle, refreshButton, expiringList);
        panel.setPrefWidth(280);
        panel.setStyle(CARD_STYLE);
        return panel;
    }

    private TableColumn<FoodItem, String> column(String title, Function<FoodItem, String> mapper) {
        TableColumn<FoodItem, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return col;
    }

    private void refreshViews() {
        applyCurrentFilter();
        groceryRows.setAll(manager.getGroceryItems());
        expiringRows.setAll(manager.getExpiringItems(LocalDate.now()));
    }

    private void applyCurrentFilter() {
        FoodCategory category = filterCategoryBox == null ? null : filterCategoryBox.getValue();
        StorageLocation location = null;

        if (viewModeBox != null && "View by location".equals(viewModeBox.getValue()) && viewLocationBox != null) {
            location = viewLocationBox.getValue();
        }

        inventoryRows.setAll(manager.filterInventory(category, location));
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

}
