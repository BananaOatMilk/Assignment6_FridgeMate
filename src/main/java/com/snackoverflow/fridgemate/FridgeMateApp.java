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
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private final FridgeMateManager manager = new FridgeMateManager(
            new Inventory(new DefaultExpirationPolicy(7)),
            new GroceryList(),
            new LocalJsonStorage(Path.of("fridgemate-data.json")),
            new CsvShare()
    );

    private final ObservableList<FoodItem> inventoryRows = FXCollections.observableArrayList();
    private final ObservableList<String> groceryRows = FXCollections.observableArrayList();

    private TableView<FoodItem> inventoryTable;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("FridgeMate");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        root.setTop(title);

        inventoryTable = createInventoryTable();
        VBox inventoryPanel = createInventoryPanel(stage);
        VBox groceryPanel = createGroceryPanel();

        HBox center = new HBox(12, inventoryPanel, groceryPanel);
        root.setCenter(center);

        statusLabel = new Label("Ready");
        root.setBottom(statusLabel);

        try {
            manager.load();
            setStatus("Loaded saved data");
        } catch (IOException ex) {
            setStatus("No saved data loaded");
        }

        refreshViews();

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("FridgeMate");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<FoodItem> createInventoryTable() {
        TableView<FoodItem> table = new TableView<>();
        table.setItems(inventoryRows);
        table.getColumns().addAll(
                column("Name", FoodItem::getName),
                column("Category", item -> item.getCategory().name()),
                column("Location", item -> item.getLocation().name()),
                column("Qty", item -> String.valueOf(item.getQuantity())),
                column("Expires", item -> item.getExpirationDate().toString())
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
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

        Button addButton = new Button("Add Item");
        addButton.setOnAction(event -> {
            try {
                FoodItem item = new FoodItem(
                        nameField.getText(),
                        "",
                        categoryBox.getValue(),
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
        saveButton.setOnAction(event -> {
            try {
                manager.save();
                setStatus("Saved data");
            } catch (IOException ex) {
                setStatus("Save failed: " + ex.getMessage());
            }
        });

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> {
            try {
                manager.load();
                refreshViews();
                setStatus("Loaded data");
            } catch (IOException ex) {
                setStatus("Load failed: " + ex.getMessage());
            }
        });

        Button exportButton = new Button("Export CSV");
        exportButton.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export inventory CSV");
            chooser.setInitialFileName("fridgemate-share.csv");
            File file = chooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }
            try {
                manager.exportInventory(file.toPath());
                setStatus("Exported CSV");
            } catch (IOException ex) {
                setStatus("Export failed: " + ex.getMessage());
            }
        });

        Button importButton = new Button("Import CSV");
        importButton.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import inventory CSV");
            File file = chooser.showOpenDialog(stage);
            if (file == null) {
                return;
            }
            try {
                manager.importInventory(file.toPath());
                refreshViews();
                setStatus("Imported CSV");
            } catch (IOException ex) {
                setStatus("Import failed: " + ex.getMessage());
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

        HBox actions = new HBox(8, addButton, deleteButton, saveButton, loadButton, exportButton, importButton);
        VBox panel = new VBox(10, new Label("Inventory"), form, actions, inventoryTable);
        panel.setPrefWidth(660);
        return panel;
    }

    private VBox createGroceryPanel() {
        ListView<String> groceryListView = new ListView<>(groceryRows);

        Button addLowStock = new Button("Add Low-Stock Items");
        addLowStock.setOnAction(event -> {
            int added = manager.addLowStockItemsToGrocery(1);
            refreshViews();
            setStatus("Added " + added + " low-stock item(s)");
        });

        VBox panel = new VBox(10, new Label("Grocery List"), addLowStock, groceryListView);
        panel.setPrefWidth(300);
        return panel;
    }

    private TableColumn<FoodItem, String> column(String title, Function<FoodItem, String> mapper) {
        TableColumn<FoodItem, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(mapper.apply(data.getValue())));
        return col;
    }

    private void refreshViews() {
        inventoryRows.setAll(manager.getInventoryItems());
        groceryRows.setAll(manager.getGroceryItems());
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
