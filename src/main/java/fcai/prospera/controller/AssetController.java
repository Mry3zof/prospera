package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import fcai.prospera.service.AssetService;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.AssetView; // Make sure this interface/class exists
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AssetController {
    // Services & SceneManager - will be injected by init
    private AssetService assetService;
    private AuthService authService;
    private AssetView assetView; // Injected by init (can be null if AssetView is optional)
    private SceneManager sceneManager; // Injected by init

    @FXML private TableView<Asset> assetTable;
    @FXML private TableColumn<Asset, String> nameColumn;
    @FXML private TableColumn<Asset, AssetType> typeColumn;
    @FXML private TableColumn<Asset, BigDecimal> purchasePriceColumn;
    @FXML private TableColumn<Asset, BigDecimal> currentValueColumn;
    @FXML private TableColumn<Asset, Currency> currencyColumn;
    @FXML private TableColumn<Asset, Date> purchaseDateColumn;
    @FXML private TableColumn<Asset, Boolean> zakatableColumn;
    @FXML private TableColumn<Asset, Void> actionsColumn;
    @FXML private Label netWorthLabel;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Button returnToDashBoard;

    private ObservableList<Asset> assetsList = FXCollections.observableArrayList();

    // No-arg constructor for FXML loader
    public AssetController() {
        // System.out.println("AssetController: No-arg constructor called");
    }

    @FXML
    public void initialize() {
        // System.out.println("AssetController: FXML initialize() called");
        // Setup FXML components that DON'T rely on injected services yet.
        // CellValueFactories are generally fine as they define how to get data later.
        if (nameColumn != null) nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        if (typeColumn != null) typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        if (purchasePriceColumn != null) purchasePriceColumn.setCellValueFactory(cellData -> cellData.getValue().purchasePriceProperty());
        if (currentValueColumn != null) currentValueColumn.setCellValueFactory(cellData -> cellData.getValue().currentValueProperty());
        if (currencyColumn != null) currencyColumn.setCellValueFactory(cellData -> cellData.getValue().currencyProperty());
        if (purchaseDateColumn != null) purchaseDateColumn.setCellValueFactory(cellData -> cellData.getValue().purchaseDateProperty());
        if (zakatableColumn != null) zakatableColumn.setCellValueFactory(cellData -> cellData.getValue().zakatableProperty());

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
        // DO NOT call refreshAssets() or assetTable.setItems() here yet.
    }

    // Custom init method called by SceneManager
    public void init(SceneManager sceneManager, AuthService authService, AssetService assetService) {
        // System.out.println("AssetController: Custom init() called");
        this.sceneManager = sceneManager;
        this.authService = authService;
        this.assetService = assetService;
        this.assetView = assetView; // assetView can be null if optional

        // Now that FXML components are ready (from initialize()) and services are injected,
        // set up the table with items and load initial data.
        if (assetTable != null) {
            assetTable.setItems(assetsList);
            refreshAssets();
        } else {
            System.err.println("AssetController.init(): assetTable is null.");
        }
    }

    private void handleEditAssetClicked(Asset assetToEdit) {
        if (assetService == null) {
            System.err.println("AssetService not initialized in AssetController for editing.");
            return;
        }
        System.out.println("Editing asset: " + assetToEdit.getName());
        Asset editedAsset = new Asset(
                assetToEdit.getUserId(),
                assetToEdit.getName() + " (edited)",
                assetToEdit.getType(),
                assetToEdit.getPurchasePrice().add(BigDecimal.TEN),
                assetToEdit.getPurchaseDate(),
                assetToEdit.getCurrentValue().add(BigDecimal.TEN),
                assetToEdit.getCurrency(),
                !assetToEdit.isZakatable()
        );

        if (assetService.updateAsset(assetToEdit.getId(), editedAsset)) {
            assetsChanged();
            System.out.println("Asset updated successfully.");
        } else {
            if (assetView != null) {
                // assetView.onEditAssetFail(); // Assuming AssetView has such a method
            } else {
                System.err.println("Failed to update asset. (AssetView is null)");
                new Alert(Alert.AlertType.ERROR, "Could not update the asset.").showAndWait();
            }
        }
    }

    private void handleRemoveAssetClicked(Asset assetToRemove) {
        if (assetService == null) {
            System.err.println("AssetService not initialized in AssetController for removal.");
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
                    System.out.println("Asset removed successfully.");
                } else {
                    if (assetView != null) {
                        // assetView.onRemoveAssetFail(); // Assuming AssetView has such a method
                    } else {
                        System.err.println("Failed to remove asset. (AssetView is null)");
                        new Alert(Alert.AlertType.ERROR, "Could not delete the asset.").showAndWait();
                    }
                }
            }
        });
    }

    private void refreshAssets() {
        // System.out.println("AssetController: refreshAssets() called");
        if (authService == null || authService.getCurrentUser() == null) {
            if(assetsList != null) assetsList.clear();
            if(netWorthLabel != null) netWorthLabel.setText("N/A");
            System.err.println("Cannot refresh assets: AuthService not initialized or no current user.");
            return;
        }
        if (assetService == null) {
            if(assetsList != null) assetsList.clear();
            if(netWorthLabel != null) netWorthLabel.setText("N/A");
            System.err.println("Cannot refresh assets: AssetService not initialized.");
            return;
        }

        UUID currentUserId = authService.getCurrentUser().getId();
        List<Asset> userAssets = assetService.getAssets(currentUserId);
        assetsList.setAll(userAssets);
        updateNetWorth(currentUserId);
    }

    private void updateNetWorth(UUID userId) {
        if (assetService == null) {
            if(netWorthLabel != null) netWorthLabel.setText("N/A");
            System.err.println("Cannot update net worth: AssetService not initialized.");
            return;
        }
        BigDecimal netWorth = assetService.calculateValuation(assetsList); // Use current assetsList
        if (netWorthLabel != null) {
            netWorthLabel.setText(String.format("%.2f", netWorth));
        }
    }

    public boolean addAssetViaForm(Asset asset) {
        if (assetService == null) {
            System.err.println("AssetService not initialized in AssetController for adding.");
            return false;
        }
        boolean success = assetService.addAsset(asset);
        if (success) {
            assetsChanged();
        } else {
            if (assetView != null) {
                // assetView.onAddAssetFail(); // Assuming AssetView has such a method
            } else {
                System.err.println("Failed to add asset. (AssetView is null)");
                new Alert(Alert.AlertType.ERROR, "Could not add the asset.").showAndWait();
            }
        }
        return success;
    }

    @FXML
    private void handleAddAsset() {
        System.out.println("Add Asset button clicked. Implement dialog here.");
        if (authService == null || authService.getCurrentUser() == null) {
            System.err.println("Cannot add asset: AuthService not initialized or no current user.");
            return;
        }
        UUID currentUserId = authService.getCurrentUser().getId();
        Asset newAsset = new Asset(
                currentUserId,
                "New Asset " + (assetsList.size() + 1),
                AssetType.STOCKS,
                new BigDecimal("1000.00"),
                new Date(),
                new BigDecimal("1100.00"),
                Currency.getInstance("USD"),
                true
        );
        addAssetViaForm(newAsset);
    }

    @FXML
    public void handleRefreshAssets() {
        // System.out.println("AssetController: Refresh button clicked.");
        refreshAssets();
    }

    @FXML
    private void handleReturnToDashboard() {
        // System.out.println("AssetController: Return to Dashboard button clicked.");
        if (sceneManager != null) {
            try {
                sceneManager.showDashboardView();
            } catch (IOException e) {
                System.err.println("Failed to navigate to Dashboard: " + e.getMessage());
                new Alert(Alert.AlertType.ERROR, "Could not load the dashboard. Please try again.").showAndWait();
                e.printStackTrace();
            }
        } else {
            System.err.println("SceneManager is null in AssetController. Cannot navigate.");
        }
    }

    public void assetsChanged() {
        refreshAssets();
    }
}