package fcai.prospera.model;

import javafx.beans.property.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode; // Added import
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

// Assuming AssetType enum exists, e.g.:
// package fcai.prospera.model;
// public enum AssetType { STOCK, BOND, REAL_ESTATE, CRYPTOCURRENCY, OTHER }

public class Asset implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final ObjectProperty<UUID> userId = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<AssetType> type = new SimpleObjectProperty<>(); // Assumes AssetType is defined
    private final ObjectProperty<BigDecimal> purchasePrice = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> purchaseDate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> currentValue = new SimpleObjectProperty<>();
    private final ObjectProperty<Currency> currency = new SimpleObjectProperty<>();
    private final BooleanProperty zakatable = new SimpleBooleanProperty();

    // TODO: Can an asset exist without a userId? + is randomUUID enough?
    public Asset() {
        this.id = UUID.randomUUID();
    }

    public Asset(UUID userId, String name, AssetType type, BigDecimal purchasePrice,
                 Date purchaseDate, BigDecimal currentValue, Currency currency,
                 boolean isZakatable) {
        this(); // Call to the default constructor to initialize id
        setUserId(userId);
        setName(name);
        setType(type);
        setPurchasePrice(purchasePrice);
        setPurchaseDate(purchaseDate);
        setCurrentValue(currentValue);
        setCurrency(currency);
        setZakatable(isZakatable);
    }

    // Property getters
    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<AssetType> typeProperty() {
        return type;
    }

    public ObjectProperty<BigDecimal> purchasePriceProperty() {
        return purchasePrice;
    }

    public ObjectProperty<Date> purchaseDateProperty() {
        return purchaseDate;
    }

    public ObjectProperty<BigDecimal> currentValueProperty() {
        return currentValue;
    }

    public ObjectProperty<Currency> currencyProperty() {
        return currency;
    }

    public BooleanProperty zakatableProperty() {
        return zakatable;
    }

    public ObjectProperty<UUID> userIdProperty() {
        return userId;
    }

    // Regular getters and setters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId.get();
    }

    public void setUserId(UUID userId) {
        this.userId.set(userId);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public AssetType getType() {
        return type.get();
    }

    public void setType(AssetType type) {
        this.type.set(type);
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice.get();
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice.set(purchasePrice);
    }

    public Date getPurchaseDate() {
        return purchaseDate.get();
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate.set(purchaseDate);
    }

    public BigDecimal getCurrentValue() {
        return currentValue.get();
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue.set(currentValue);
    }

    public Currency getCurrency() {
        return currency.get();
    }

    public void setCurrency(Currency currency) {
        this.currency.set(currency);
    }

    public boolean isZakatable() {
        return zakatable.get();
    }

    public void setZakatable(boolean zakatable) {
        this.zakatable.set(zakatable);
    }

    public BigDecimal calculateROI() {
        if (getPurchasePrice() == null || getPurchasePrice().compareTo(BigDecimal.ZERO) == 0 || getCurrentValue() == null) {
            return BigDecimal.ZERO; // Or handle as an error/NaN appropriately
        }
        BigDecimal profit = getCurrentValue().subtract(getPurchasePrice());
        return profit.divide(getPurchasePrice(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", name=" + getName() +
                ", type=" + (getType() != null ? getType().toString() : "null") + // Added null check for type
                ", currentValue=" + (getCurrentValue() != null ? getCurrentValue().toPlainString() : "null") + // Added null check
                '}';
    }
}