package fcai.prospera;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.List;

public class CurrencyComboBox extends ComboBox<CurrencyItem> {

    public CurrencyComboBox() {
        super(FXCollections.observableArrayList(getDefaultCurrencies()));
        setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CurrencyItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
            }
        });
        setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CurrencyItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCode());
            }
        });
    }

    private static List<CurrencyItem> getDefaultCurrencies() {
        return List.of(
                new CurrencyItem("USD", "United States Dollar", "$"),
                new CurrencyItem("EUR", "Euro", "€"),
                new CurrencyItem("GBP", "British Pound", "£"),
                new CurrencyItem("EGP", "Egyptian Pound", "£")
        );
    }
}

