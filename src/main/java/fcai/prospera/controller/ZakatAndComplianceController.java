package fcai.prospera.controller;

import fcai.prospera.CurrencyComboBox;
import fcai.prospera.CurrencyConversion;
import fcai.prospera.CurrencyItem;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.swing.*;
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

        public BooleanProperty zakatableProperty() {
            return asset.zakatableProperty();
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
    }

    private ZakatAndComplianceService zakatService;
    private AuthService authService;
    private SceneManager sceneManager;

    static private List<Asset> selectedAssets; // TODO: consider switching to asset id

    @FXML private AnchorPane root;

    @FXML private TableView<SelectableAsset> assets_table;
    @FXML private TableColumn<SelectableAsset, Boolean> select_col;
    @FXML private TableColumn<SelectableAsset, Image> icon_col;
    @FXML private TableColumn<SelectableAsset, String> name_col;
    @FXML private TableColumn<SelectableAsset, BigDecimal> value_col;
    @FXML private TableColumn<SelectableAsset, Boolean> zakatable_col;

    @FXML private TextField gold_rate_field;
    @FXML private TextField silver_rate_field;
    @FXML private CurrencyComboBox currency_picker;
    @FXML private Label gold_currency_label;
    @FXML private Label silver_currency_label;
    @FXML private Label error_label;

    @FXML private ListView<String> results_list;

    private final ObservableList<SelectableAsset> assets = FXCollections.observableArrayList();

    private static double goldExchangeRate = 4780;
    private static double silverExchangeRate = 52.22;
    private static CurrencyItem exchangeCurrency = new CurrencyItem("EGP", "Egyptian Pound", "£");

    public void init(SceneManager sceneManager, AuthService authService, ZakatAndComplianceService zakatService, String view) {
        this.sceneManager = sceneManager;
        this.authService = authService;
        this.zakatService = zakatService;

        switch (view) {
            case "MAIN":
                initMainView();
                break;
            case "SELECTION":
                initSelectionView();
                break;
            case "RESULTS":
                initResultsView();
                break;
            default:
                break;
        }
    }

    private void initMainView() {
        gold_rate_field.setText(String.valueOf(goldExchangeRate));
        silver_rate_field.setText(String.valueOf(silverExchangeRate));
        currency_picker.setValue(
                currency_picker.getItems().stream()
                        .filter(item -> item.getCode().equals(exchangeCurrency.getCode()))
                        .findFirst()
                        .orElse(null)
        );
        gold_currency_label.setText(exchangeCurrency.getCode() + "/gram");
        silver_currency_label.setText(exchangeCurrency.getCode() + "/gram");

        gold_rate_field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // focus lost
                try {
                    double value = Double.parseDouble(gold_rate_field.getText());
                    if (value >= 0) {
                        goldExchangeRate = value;
                    } else {
                        gold_rate_field.setText(String.valueOf(goldExchangeRate)); // revert
                    }
                } catch (Exception e) {
                    gold_rate_field.setText(String.valueOf(goldExchangeRate)); // revert
                }
            }
        });
        silver_rate_field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // focus lost
                try {
                    double value = Double.parseDouble(silver_rate_field.getText());
                    if (value >= 0) {
                        silverExchangeRate = value;
                    } else {
                        silver_rate_field.setText(String.valueOf(silverExchangeRate)); // revert
                    }
                } catch (Exception e) {
                    silver_rate_field.setText(String.valueOf(silverExchangeRate)); // revert
                }
            }
        });

        currency_picker.valueProperty().addListener((obs, oldVal, newVal) -> {
            exchangeCurrency = newVal;
            gold_currency_label.setText(exchangeCurrency.getCode() + "/gram");
            silver_currency_label.setText(exchangeCurrency.getCode() + "/gram");
            goldExchangeRate = CurrencyConversion.convert(oldVal.getCode(), newVal.getCode(), BigDecimal.valueOf(goldExchangeRate)).doubleValue();
            silverExchangeRate = CurrencyConversion.convert(oldVal.getCode(), newVal.getCode(), BigDecimal.valueOf(silverExchangeRate)).doubleValue();
            gold_rate_field.setText(String.valueOf(goldExchangeRate));
            silver_rate_field.setText(String.valueOf(silverExchangeRate));
        });
    }

    private void initSelectionView() {
        select_col.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        select_col.setCellFactory(CheckBoxTableCell.forTableColumn(select_col));

        // TODO: setup icon cell

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        value_col.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
        zakatable_col.setCellValueFactory(new PropertyValueFactory<>("zakatable"));

        assets.addAll(
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset1", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(120), Currency.getInstance("USD"), true)),
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset2", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(1200), Currency.getInstance("USD"), true)),
                new SelectableAsset(new Asset(UUID.randomUUID(), "asset3", AssetType.GOLD, BigDecimal.valueOf(100), new Date(), BigDecimal.valueOf(124), Currency.getInstance("USD"), true))
        ); // TODO: fetch from DB

        // TODO: make selection persistent

        assets_table.setItems(assets);
    }

    private void initResultsView() {
        results_list.getItems().clear();

        double goldNisab = ZakatAndComplianceService.getGoldNisab(goldExchangeRate);
        double silverNisab = ZakatAndComplianceService.getSilverNisab(silverExchangeRate);

        System.out.println(goldNisab + " " + silverNisab);
        double applicableNisab = Math.min(goldNisab, silverNisab);
        BigDecimal zakatAmount = zakatService.calculateZakat(applicableNisab, authService.getCurrentUser().getId());

        results_list.getItems().addAll(
                "Gold Nisab: " + goldNisab,
                "Silver Nisab: " + silverNisab,
                "Applicable Nisab: " + applicableNisab,
                "-------------------------------------",
                "Total Assets Value: " + selectedAssets.stream().map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add),
                "-------------------------------------",
                "Zakat amount: " + zakatAmount
        );

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

    public void showZakatResultView() {
        if (!canCalculateZakat()) {
            error_label.setText("You must select at least one asset to calculate zakat");
            return;
        }

        try {
            sceneManager.showZakatResultView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    private Boolean canCalculateZakat() {
        return selectedAssets != null && !selectedAssets.isEmpty();
    }


    /*
    TODO: implement zakat calculation view
    TODO: either implement asset input form or remove it
    TODO: purchase date and hawl due in the asset selection view
     */
}