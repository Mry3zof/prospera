package fcai.prospera;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.List;

/**
 * This is a JavaFX custom component that extends the ComboBox class and adds a custom cell factory to display the currency code and name in the dropdown menu.
 */
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

    /**
     * A list of default currencies (USD, EUR, GBP, EGP) to populate the dropdown menu with.
     * @return A list of CurrencyItem objects
     */
    private static List<CurrencyItem> getDefaultCurrencies() {
        return List.of(
                new CurrencyItem("USD", "United States Dollar", "$"),
                new CurrencyItem("EUR", "Euro", "€"),
                new CurrencyItem("GBP", "British Pound", "£"),
                new CurrencyItem("EGP", "Egyptian Pound", "£")
        );
    }
}

