package fcai.prospera.model;

import javafx.beans.property.*;

import java.io.*; // Import for Serializable and ObjectOutputStream/InputStream
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

/**
 * A class to model asset data
 */
public class Asset implements Serializable { // Ensure Asset implements Serializable
    @Serial
    private static final long serialVersionUID = 2L; // Changed version UID due to serialization changes

    private final UUID id; // This is final and Serializable (UUID is Serializable)

    // Mark JavaFX properties as transient
    private transient ObjectProperty<UUID> userId;
    private transient StringProperty name;
    private transient ObjectProperty<AssetType> type;
    private transient ObjectProperty<BigDecimal> purchasePrice;
    private transient ObjectProperty<Date> purchaseDate;
    private transient ObjectProperty<BigDecimal> currentValue;
    private transient ObjectProperty<Currency> currency;
    private transient BooleanProperty zakatable;

    public Asset() {
        this.id = UUID.randomUUID();
        initializeProperties(); // Helper method to initialize transient properties
    }

    public Asset(UUID userId, String name, AssetType type, BigDecimal purchasePrice,
                 Date purchaseDate, BigDecimal currentValue, Currency currency) {
        this(); // Call to the default constructor to initialize id and properties
        setUserId(userId);
        setName(name);
        setType(type);
        setPurchasePrice(purchasePrice);
        setPurchaseDate(purchaseDate);
        setCurrentValue(currentValue);
        setCurrency(currency);
    }

    /**
     * Method to initialize transient properties for JavaFX
     */
    private void initializeProperties() {
        this.userId = new SimpleObjectProperty<>();
        this.name = new SimpleStringProperty();
        this.type = new SimpleObjectProperty<>();
        this.purchasePrice = new SimpleObjectProperty<>();
        this.purchaseDate = new SimpleObjectProperty<>();
        this.currentValue = new SimpleObjectProperty<>();
        this.currency = new SimpleObjectProperty<>();
        this.zakatable = new SimpleBooleanProperty();
    }

    /**
     * Serializes object
     * @param out : output stream
     * @throws IOException : if an I/O error occurs
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Writes non-static and non-transient fields (like 'id')

        // Manually write the values held by the transient properties
        out.writeObject(getUserId());
        out.writeObject(getName());
        out.writeObject(getType());
        out.writeObject(getPurchasePrice());
        out.writeObject(getPurchaseDate());
        out.writeObject(getCurrentValue());
        out.writeObject(getCurrency());
    }

    /**
     * Deserializes object
     * @param in : input stream
     * @throws IOException : if an I/O error occurs
     * @throws ClassNotFoundException : if class not found
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Reads non-static and non-transient fields (like 'id')

        // Re-initialize transient properties
        initializeProperties();

        // Manually read the values and set them into the properties
        setUserId((UUID) in.readObject());
        setName((String) in.readObject());
        setType((AssetType) in.readObject());
        setPurchasePrice((BigDecimal) in.readObject());
        setPurchaseDate((Date) in.readObject());
        setCurrentValue((BigDecimal) in.readObject());
        setCurrency((Currency) in.readObject());
    }


    // Property getters (no change)
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<AssetType> typeProperty() { return type; }
    public ObjectProperty<BigDecimal> purchasePriceProperty() { return purchasePrice; }
    public ObjectProperty<Date> purchaseDateProperty() { return purchaseDate; }
    public ObjectProperty<BigDecimal> currentValueProperty() { return currentValue; }
    public ObjectProperty<Currency> currencyProperty() { return currency; }
    public BooleanProperty zakatableProperty() { return zakatable; }
    public ObjectProperty<UUID> userIdProperty() { return userId; }

    // Regular getters and setters (no change)
    public UUID getId() { return id; }
    public UUID getUserId() { return userId.get(); }
    public void setUserId(UUID userId) { this.userId.set(userId); }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public AssetType getType() { return type.get(); }
    public void setType(AssetType type) { this.type.set(type); }
    public BigDecimal getPurchasePrice() { return purchasePrice.get(); }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice.set(purchasePrice); }
    public Date getPurchaseDate() { return purchaseDate.get(); }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate.set(purchaseDate); }
    public BigDecimal getCurrentValue() { return currentValue.get(); }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue.set(currentValue); }
    public Currency getCurrency() { return currency.get(); }
    public void setCurrency(Currency currency) { this.currency.set(currency); }

    public BigDecimal calculateROI() {
        if (getPurchasePrice() == null || getPurchasePrice().compareTo(BigDecimal.ZERO) == 0 || getCurrentValue() == null) {
            return BigDecimal.ZERO;
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
                ", type=" + (getType() != null ? getType().toString() : "null") +
                ", currentValue=" + (getCurrentValue() != null ? getCurrentValue().toPlainString() : "null") +
                '}';
    }
}