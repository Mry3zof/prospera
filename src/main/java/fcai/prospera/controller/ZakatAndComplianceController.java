package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.*;
import fcai.prospera.service.ZakatAndComplianceService;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.ZakatAndComplianceView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ZakatAndComplianceController {
    public static class SelectableAsset {
        private final Asset asset;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);
        private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();

        public SelectableAsset(Asset asset, Image icon) {
            this.asset = asset;
            this.icon.set(icon);
        }

        public SelectableAsset(Asset asset) {
            this.asset = asset;
        }

        public SelectableAsset(Asset asset, Boolean selected) {
            this.asset = asset;
            this.selected.set(selected);
        }

        public SelectableAsset(Asset asset, Image icon, Boolean selected) {
            this.asset = asset;
            this.selected.set(selected);
            this.icon.set(icon);
        }

        public Asset getAsset() {
            return asset;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public ObjectProperty<Image> iconProperty() {
            return icon;
        }

        public Image getIcon() {
            return icon.get();
        }

        public void setIcon(Image icon) {
            this.icon.set(icon);
        }

        // Convenience getters
        public String getName() {
            return asset.getName();
        }

        public BigDecimal getCurrentValue() {
            return asset.getCurrentValue();
        }

        // Add more accessors if needed
    }

    private ZakatAndComplianceService zakatService;
    private AuthService authService;
    private SceneManager sceneManager;

    private List<Asset> selectedAssets;

    @FXML private TableView<SelectableAsset> assets_table;
    @FXML private TableColumn<SelectableAsset, Boolean> select_col;
    @FXML private TableColumn<SelectableAsset, Image> icon_col;
    @FXML private TableColumn<SelectableAsset, String> name_col;
    @FXML private TableColumn<SelectableAsset, BigDecimal> value_col;
    @FXML private TableColumn<SelectableAsset, Boolean> zakatable_col;

    private final ObservableList<SelectableAsset> assets = FXCollections.observableArrayList();

    public void init(SceneManager sceneManager, AuthService authService, ZakatAndComplianceService zakatService, Boolean isSelectionView) {
        this.sceneManager = sceneManager;
        this.authService = authService;
        this.zakatService = zakatService;

        if (isSelectionView)
            initSelectionView();
    }

    private void initSelectionView() {
        select_col.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        select_col.setCellFactory(CheckBoxTableCell.forTableColumn(select_col));

        // TODO: setup icon cell

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        value_col.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
//        zakatable_col.setCellValueFactory(new PropertyValueFactory<>("zakatable"));

        assets.addAll(
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset1", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(120), Currency.getInstance("USD"), true)),
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset2", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(1200), Currency.getInstance("USD"), true)),
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset3", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(124), Currency.getInstance("USD"), true))
        );

        // TODO: make selection persistent

        assets_table.setItems(assets);
    }

    public void onSaveSelection() {
        selectedAssets = assets_table.getItems().stream().filter(SelectableAsset::isSelected).map(SelectableAsset::getAsset).collect(Collectors.toList());
    }

    public void onSelectAll() {
        assets_table.getItems().forEach(selected -> selected.setSelected(true));
    }

    public void onClearSelection() {
        assets_table.getItems().forEach(selected -> selected.setSelected(false));
    }

    public void showChooseAssetsView() {
        try {
            sceneManager.showZakatChooseAssetsView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    public void showZakatView() {
        try {
            sceneManager.showZakatView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    public void showDashboardView() {
        try {
            sceneManager.showDashboardView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    /*
    TODO: implement gold and silver exchange rate input
    TODO: make currency switching convert current value to new currency value
    TODO: implement zakat calculation view
    TODO: either implement asset input form or remove it
     */
}