package fcai.prospera.view;

import fcai.prospera.controller.AssetController;
import fcai.prospera.model.Asset;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AssetView {
    private AssetController controller;

    private TableView<Asset> assetsTable;
    private Button refreshButton;
    private Label statusLabel;

    public AssetView(AssetController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {

        assetsTable = new TableView<>();
        refreshButton = new Button("Refresh Assets");
        statusLabel = new Label();


        VBox root = new VBox(10, assetsTable, refreshButton, statusLabel);


        refreshButton.setOnAction(e -> controller.handleRefreshAssets());
    }


    public void onAddAssetFail() {
        statusLabel.setText("Failed to add asset.");
    }

    public void refreshAssetsList() {
        statusLabel.setText("Assets refreshed.");
    }
}
