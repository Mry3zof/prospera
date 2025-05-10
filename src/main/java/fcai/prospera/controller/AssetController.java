package fcai.prospera.controller;

import fcai.prospera.CurrencyComboBox;
import fcai.prospera.CurrencyConversion;
import fcai.prospera.CurrencyItem;
import fcai.prospera.SceneManager;
import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import fcai.prospera.service.AssetService;
import fcai.prospera.service.AuthService;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for managing the asset view.
 * This class handles interactions for displaying, adding, editing, and removing assets,
 * as well as calculating and displaying the user's net worth.
 */
public class AssetController {
    private AssetService assetService;
    private AuthService authService;
    private SceneManager sceneManager;

    @FXML private TableView<Asset> assetTable;
    @FXML private TableColumn<Asset, String> nameColumn;
    @FXML private TableColumn<Asset, AssetType> typeColumn;
    @FXML private TableColumn<Asset, BigDecimal> purchasePriceColumn;
    @FXML private TableColumn<Asset, BigDecimal> currentValueColumn;
    @FXML private TableColumn<Asset, Currency> currencyColumn;
    @FXML private TableColumn<Asset, Date> purchaseDateColumn;
    @FXML private TableColumn<Asset, Void> actionsColumn;

    @FXML private Label netWorthLabel;
    @FXML private CurrencyComboBox netWorthCurrencyComboBox;

    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Button returnToDashBoard;

    private ObservableList<Asset> assetsList = FXCollections.observableArrayList();
    private static final String DEFAULT_NET_WORTH_CURRENCY = "USD";

    /**
     * Default constructor for the AssetController.
     */
    public AssetController() { }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up cell value factories for table columns,
     * configures the actions column with edit and delete buttons, and initializes the
     * net worth currency combo box.
     */
    @FXML
    public void initialize() {
        if (nameColumn != null) nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        if (typeColumn != null) typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        if (purchasePriceColumn != null) purchasePriceColumn.setCellValueFactory(cellData -> cellData.getValue().purchasePriceProperty());
        if (currentValueColumn != null) currentValueColumn.setCellValueFactory(cellData -> cellData.getValue().currentValueProperty());
        if (currencyColumn != null) {
            currencyColumn.setCellValueFactory(cellData -> cellData.getValue().currencyProperty());
            currencyColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Currency item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getCurrencyCode());
                }
            });
        }
        if (purchaseDateColumn != null) purchaseDateColumn.setCellValueFactory(cellData -> cellData.getValue().purchaseDateProperty());

        if (actionsColumn != null) {
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox pane = new HBox(5, editBtn, deleteBtn);
                {
                    editBtn.setOnAction(event -> {
                        Asset asset = getTableView().getItems().get(getIndex());
                        handleEditAssetClicked(asset);
                    });
                    deleteBtn.setOnAction(event -> {
                        Asset asset = getTableView().getItems().get(getIndex());
                        handleRemoveAssetClicked(asset);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            });
        }

        if (netWorthCurrencyComboBox != null) {
            CurrencyItem defaultDisplayCurrency = findCurrencyItemByCode(DEFAULT_NET_WORTH_CURRENCY, netWorthCurrencyComboBox);
            if (defaultDisplayCurrency == null && !netWorthCurrencyComboBox.getItems().isEmpty()) {
                defaultDisplayCurrency = netWorthCurrencyComboBox.getItems().get(0);
            }
            netWorthCurrencyComboBox.setValue(defaultDisplayCurrency);
            netWorthCurrencyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && authService != null && authService.getCurrentUser() != null) {
                    updateNetWorth(authService.getCurrentUser().getId());
                } else if (newVal != null) {
                    updateNetWorth(null);
                }
            });
        }
    }

    /**
     * Initializes the controller with necessary services and managers.
     * This method should be called after the FXML view is loaded and controller instance is created.
     * It sets up the asset table with data and refreshes the asset list.
     *
     * @param sceneManager The {@link SceneManager} for view navigation.
     * @param authService The {@link AuthService} for user authentication.
     * @param assetService The {@link AssetService} for asset data operations.
     */
    public void init(SceneManager sceneManager, AuthService authService, AssetService assetService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
        this.assetService = assetService;
        if (assetTable != null) {
            assetTable.setItems(assetsList);
            refreshAssets();
        } else {
            System.err.println("AssetController.init(): assetTable is null.");
        }
    }

    /**
     * Finds a {@link CurrencyItem} in a {@link CurrencyComboBox} by its currency code.
     *
     * @param code The currency code to search for (e.g., "USD").
     * @param comboBox The {@link CurrencyComboBox} to search within.
     * @return The {@link CurrencyItem} if found, or {@code null} otherwise.
     */
    private CurrencyItem findCurrencyItemByCode(String code, CurrencyComboBox comboBox) {
        if (code == null || comboBox == null || comboBox.getItems() == null) return null;
        return comboBox.getItems().stream()
                .filter(item -> item != null && item.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
    }

    /**
     * Displays an error alert dialog to the user.
     *
     * @param title The title of the error dialog.
     * @param content The content message of the error.
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Updates the net worth label with the calculated net worth for the given user ID
     * in the currency selected in {@code netWorthCurrencyComboBox}.
     *
     * @param userId The ID of the user whose net worth is to be calculated.
     *               If {@code null}, or if services are unavailable, "N/A" is displayed.
     */
    private void updateNetWorth(UUID userId) {
        if (netWorthLabel == null || netWorthCurrencyComboBox == null) {
            System.err.println("Net worth UI components not initialized.");
            return;
        }
        if (userId == null || authService == null || authService.getCurrentUser() == null) {
            netWorthLabel.setText("N/A");
            return;
        }
        if (assetService == null) {
            netWorthLabel.setText("N/A (Service unavailable)");
            return;
        }
        CurrencyItem selectedDisplayCurrencyItem = netWorthCurrencyComboBox.getValue();
        String displayCurrencyCode = (selectedDisplayCurrencyItem != null) ? selectedDisplayCurrencyItem.getCode() : DEFAULT_NET_WORTH_CURRENCY;
        if (selectedDisplayCurrencyItem == null) {
            CurrencyItem fallbackItem = findCurrencyItemByCode(DEFAULT_NET_WORTH_CURRENCY, netWorthCurrencyComboBox);
            if (fallbackItem == null && !netWorthCurrencyComboBox.getItems().isEmpty()) {
                fallbackItem = netWorthCurrencyComboBox.getItems().get(0);
            }
            netWorthCurrencyComboBox.setValue(fallbackItem);
            if(fallbackItem != null) displayCurrencyCode = fallbackItem.getCode();
        }
        BigDecimal netWorthInSelectedCurrency = assetService.calculateUserNetWorthInBase(userId, displayCurrencyCode);
        netWorthLabel.setText(String.format("%.2f %s", netWorthInSelectedCurrency, displayCurrencyCode));
    }

    /**
     * Refreshes the list of assets displayed in the table for the current user.
     * It fetches assets from the {@link AssetService} and updates the net worth.
     * If no user is logged in or the service is unavailable, the table is cleared
     * and net worth is updated accordingly.
     */
    private void refreshAssets() {
        UUID currentUserId = (authService != null && authService.getCurrentUser() != null) ? authService.getCurrentUser().getId() : null;
        if (currentUserId == null) {
            if (assetsList != null) assetsList.clear();
            updateNetWorth(null);
            return;
        }
        if (assetService == null) {
            if (assetsList != null) assetsList.clear();
            updateNetWorth(currentUserId);
            return;
        }
        List<Asset> userAssets = assetService.getAssets(currentUserId);
        assetsList.setAll(userAssets);
        updateNetWorth(currentUserId);
    }

    /**
     * Handles the action of clicking the "Delete" button for an asset in the table.
     * It prompts the user for confirmation before attempting to remove the asset
     * via the {@link AssetService}. If successful, it calls {@link #assetsChanged()}
     * to refresh the view.
     *
     * @param assetToRemove The {@link Asset} object to be removed.
     */
    private void handleRemoveAssetClicked(Asset assetToRemove) {
        if (assetService == null) {
            showErrorAlert("Service Error", "Asset service is not initialized.");
            return;
        }
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText("Delete Asset: " + assetToRemove.getName());
        confirmationDialog.setContentText("Are you sure you want to delete this asset?");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (assetService.removeAsset(assetToRemove.getId())) {
                    assetsChanged();
                    System.out.println("Asset removed successfully: " + assetToRemove.getName());
                } else {
                    showErrorAlert("Deletion Failed", "Could not delete the asset: " + assetToRemove.getName());
                }
            }
        });
    }

    /**
     * Handles the action of clicking the "Edit" button for an asset in the table.
     * It opens a dialog pre-filled with the asset's current details, allowing the user
     * to modify them. If changes are saved, the asset is updated via the
     * {@link AssetService}, and {@link #assetsChanged()} is called to refresh the view.
     * The dialog includes currency conversion if the currency is changed.
     *
     * @param assetToEdit The {@link Asset} object to be edited.
     */
    private void handleEditAssetClicked(Asset assetToEdit) {
        if (assetService == null) {
            showErrorAlert("Service Error", "Asset service is not initialized.");
            return;
        }
        Dialog<Asset> dialog = new Dialog<>();
        dialog.setTitle("Edit Asset");
        dialog.setHeaderText("Edit details for: " + assetToEdit.getName());
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nameField = new TextField(assetToEdit.getName());
        ComboBox<AssetType> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(AssetType.values()));
        typeComboBox.setValue(assetToEdit.getType());
        TextField purchasePriceField = new TextField(assetToEdit.getPurchasePrice() != null ? assetToEdit.getPurchasePrice().toPlainString() : "0");
        DatePicker purchaseDatePicker = new DatePicker();
        if (assetToEdit.getPurchaseDate() != null) {
            purchaseDatePicker.setValue(assetToEdit.getPurchaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        TextField currentValueField = new TextField(assetToEdit.getCurrentValue() != null ? assetToEdit.getCurrentValue().toPlainString() : "0");
        CurrencyComboBox dialogCurrencyPicker = new CurrencyComboBox();
        CurrencyItem initialDialogCurrencyItem = (assetToEdit.getCurrency() != null) ?
                findCurrencyItemByCode(assetToEdit.getCurrency().getCurrencyCode(), dialogCurrencyPicker) : null;
        if (initialDialogCurrencyItem == null) {
            initialDialogCurrencyItem = findCurrencyItemByCode("USD", dialogCurrencyPicker);
            if (initialDialogCurrencyItem == null && !dialogCurrencyPicker.getItems().isEmpty()) {
                initialDialogCurrencyItem = dialogCurrencyPicker.getItems().get(0);
            }
        }
        dialogCurrencyPicker.setValue(initialDialogCurrencyItem);
        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1); grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Purchase Price:"), 0, 2); grid.add(purchasePriceField, 1, 2);
        grid.add(new Label("Purchase Date:"), 0, 3); grid.add(purchaseDatePicker, 1, 3);
        grid.add(new Label("Current Value:"), 0, 4); grid.add(currentValueField, 1, 4);
        grid.add(new Label("Currency:"), 0, 5); grid.add(dialogCurrencyPicker, 1, 5);
        ObjectProperty<CurrencyItem> currentDialogFieldsCurrency = new SimpleObjectProperty<>(dialogCurrencyPicker.getValue());
        dialogCurrencyPicker.valueProperty().addListener((obs, oldCI, newCI) -> {
            if (oldCI != null && newCI != null && !oldCI.getCode().equals(newCI.getCode())) {
                try {
                    BigDecimal oldPP = new BigDecimal(purchasePriceField.getText());
                    BigDecimal oldCV = new BigDecimal(currentValueField.getText());
                    purchasePriceField.setText(CurrencyConversion.convert(oldCI.getCode(), newCI.getCode(), oldPP).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    currentValueField.setText(CurrencyConversion.convert(oldCI.getCode(), newCI.getCode(), oldCV).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    currentDialogFieldsCurrency.set(newCI);
                } catch (Exception e) { System.err.println("Currency conversion error in edit dialog: " + e.getMessage());}
            } else if (newCI != null) { currentDialogFieldsCurrency.set(newCI); }
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);
        dialog.setResultConverter(db -> {
            if (db == saveButtonType) {
                try {
                    CurrencyItem selCurItem = dialogCurrencyPicker.getValue();
                    if (nameField.getText().trim().isEmpty() || typeComboBox.getValue() == null || selCurItem == null) {
                        showErrorAlert("Validation Error", "Name, Type, and Currency are required."); return null;
                    }
                    return new Asset(assetToEdit.getUserId(), nameField.getText().trim(), typeComboBox.getValue(),
                            new BigDecimal(purchasePriceField.getText().trim()),
                            (purchaseDatePicker.getValue() != null) ? Date.from(purchaseDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                            new BigDecimal(currentValueField.getText().trim()), Currency.getInstance(selCurItem.getCode()));
                } catch (Exception e) { showErrorAlert("Invalid Input", "Error processing input: " + e.getMessage()); return null;}
            }
            return null;
        });
        Optional<Asset> result = dialog.showAndWait();
        result.ifPresent(editedAssetData -> {
            if (assetService.updateAsset(assetToEdit.getId(), editedAssetData)) {
                assetsChanged();
            } else {
                showErrorAlert("Update Failed", "Could not update the asset.");
            }
        });
    }

    /**
     * Adds a new asset using the provided {@link Asset} object.
     * This method is typically called after an asset is created through a form/dialog.
     * It uses the {@link AssetService} to add the asset and then calls
     * {@link #assetsChanged()} to refresh the view.
     *
     * @param asset The {@link Asset} object to be added.
     */
    public void addAssetViaForm(Asset asset) {
        if (assetService == null) {
            showErrorAlert("Service Error", "Asset service not initialized.");
            return;
        }
        if (assetService.addAsset(asset)) {
            assetsChanged();
        } else {
            showErrorAlert("Add Failed", "Could not add the asset.");
        }
    }

    /**
     * Handles the action of clicking the "Add Asset" button.
     * It opens a dialog for the user to enter details for a new asset.
     * If the user submits valid data, a new asset is created for the current user
     * and passed to {@link #addAssetViaForm(Asset)}.
     * The dialog includes currency conversion if the currency is changed during input.
     */
    @FXML
    private void handleAddAsset() {
        if (authService == null || authService.getCurrentUser() == null) {
            showErrorAlert("Authentication Error", "No current user."); return;
        }
        if (assetService == null) {
            showErrorAlert("Service Error", "Asset service not initialized."); return;
        }
        UUID currentUserId = authService.getCurrentUser().getId();
        Dialog<Asset> dialog = new Dialog<>();
        dialog.setTitle("Add New Asset");
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nameField = new TextField(); nameField.setPromptText("e.g., Gold Bar");
        ComboBox<AssetType> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(AssetType.values()));
        typeComboBox.setPromptText("Select Type");
        TextField purchasePriceField = new TextField("0"); purchasePriceField.setPromptText("e.g., 1000.00");
        DatePicker purchaseDatePicker = new DatePicker(LocalDate.now());
        TextField currentValueField = new TextField("0"); currentValueField.setPromptText("e.g., 1100.00");
        CurrencyComboBox dialogCurrencyPicker = new CurrencyComboBox();
        CurrencyItem defaultDialogCurrency = findCurrencyItemByCode("USD", dialogCurrencyPicker);
        if (defaultDialogCurrency == null && !dialogCurrencyPicker.getItems().isEmpty()) {
            defaultDialogCurrency = dialogCurrencyPicker.getItems().get(0);
        }
        dialogCurrencyPicker.setValue(defaultDialogCurrency);
        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1); grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Purchase Price:"), 0, 2); grid.add(purchasePriceField, 1, 2);
        grid.add(new Label("Purchase Date:"), 0, 3); grid.add(purchaseDatePicker, 1, 3);
        grid.add(new Label("Current Value:"), 0, 4); grid.add(currentValueField, 1, 4);
        grid.add(new Label("Currency:"), 0, 5); grid.add(dialogCurrencyPicker, 1, 5);
        ObjectProperty<CurrencyItem> currentDialogFieldsCurrency = new SimpleObjectProperty<>(dialogCurrencyPicker.getValue());
        dialogCurrencyPicker.valueProperty().addListener((obs, oldCI, newCI) -> {
            if (oldCI != null && newCI != null && !oldCI.getCode().equals(newCI.getCode())) {
                try {
                    BigDecimal oldPP = new BigDecimal(purchasePriceField.getText());
                    BigDecimal oldCV = new BigDecimal(currentValueField.getText());
                    purchasePriceField.setText(CurrencyConversion.convert(oldCI.getCode(), newCI.getCode(), oldPP).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    currentValueField.setText(CurrencyConversion.convert(oldCI.getCode(), newCI.getCode(), oldCV).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    currentDialogFieldsCurrency.set(newCI);
                } catch (Exception e) { System.err.println("Currency conversion error in add dialog: " + e.getMessage());}
            } else if (newCI != null) { currentDialogFieldsCurrency.set(newCI); }
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);
        dialog.setResultConverter(db -> {
            if (db == addButtonType) {
                try {
                    CurrencyItem selCurItem = dialogCurrencyPicker.getValue();
                    if (nameField.getText().trim().isEmpty() || typeComboBox.getValue() == null ||
                            purchasePriceField.getText().trim().isEmpty() || currentValueField.getText().trim().isEmpty() || selCurItem == null) {
                        showErrorAlert("Validation Error", "All fields (Name, Type, Prices, Currency) are required."); return null;
                    }
                    return new Asset(currentUserId, nameField.getText().trim(), typeComboBox.getValue(),
                            new BigDecimal(purchasePriceField.getText().trim()),
                            (purchaseDatePicker.getValue() != null) ? Date.from(purchaseDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : new Date(),
                            new BigDecimal(currentValueField.getText().trim()), Currency.getInstance(selCurItem.getCode()));
                } catch (Exception e) { showErrorAlert("Invalid Input", "Error processing input: " + e.getMessage()); return null;}
            }
            return null;
        });
        Optional<Asset> result = dialog.showAndWait();
        result.ifPresent(this::addAssetViaForm);
    }

    /**
     * Handles the action of clicking the "Refresh" button.
     * Calls {@link #refreshAssets()} to update the displayed asset list and net worth.
     */
    @FXML
    public void handleRefreshAssets() {
        refreshAssets();
    }

    /**
     * Handles the action of clicking the "Return to Dashboard" button.
     * Uses the {@link SceneManager} to navigate back to the dashboard view.
     * Displays an error if navigation fails or if the {@link SceneManager} is not available.
     */
    @FXML
    private void handleReturnToDashboard() {
        if (sceneManager != null) {
            try {
                sceneManager.showDashboardView();
            } catch (IOException e) {
                System.err.println("Failed to navigate to Dashboard: " + e.getMessage());
                showErrorAlert("Navigation Error", "Could not load the dashboard.");
                e.printStackTrace();
            }
        } else {
            System.err.println("SceneManager is null in AssetController.");
        }
    }

    /**
     * Signals that the underlying asset data has changed (e.g., an asset was added,
     * edited, or removed). This method calls {@link #refreshAssets()} to update the UI.
     */
    public void assetsChanged() {
        refreshAssets();
    }
}