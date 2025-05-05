package fcai.prospera.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Asset {
    private UUID id;
    private UUID userId;
    private String name;
    private AssetType type;
    private BigDecimal purchasePrice;
    private Date purchaseDate;
    private BigDecimal currentValue;
    private Currency currency;
    private boolean isShariaCompliant;
    private boolean isZakatable;

    public Asset() {
        this.id = UUID.randomUUID();
    }

    public Asset(UUID userId, String name, AssetType type, BigDecimal purchasePrice,
                 Date purchaseDate, BigDecimal currentValue, Currency currency,
                 boolean isShariaCompliant, boolean isZakatable) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
        this.currentValue = currentValue;
        this.currency = currency;
        this.isShariaCompliant = isShariaCompliant;
        this.isZakatable = isZakatable;
    }

    public BigDecimal calculateROI() {
        if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // Avoid division by zero
        }

        BigDecimal profit = currentValue.subtract(purchasePrice);
        return profit.divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isShariaCompliant() {
        return isShariaCompliant;
    }

    public void setShariaCompliant(boolean shariaCompliant) {
        isShariaCompliant = shariaCompliant;
    }

    public boolean isZakatable() {
        return isZakatable;
    }

    public void setZakatable(boolean zakatable) {
        isZakatable = zakatable;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", purchasePrice=" + purchasePrice +
                ", currentValue=" + currentValue +
                ", currency=" + currency +
                '}';
    }
}