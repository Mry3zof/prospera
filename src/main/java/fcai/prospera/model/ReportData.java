package fcai.prospera.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class represents the data for a report.
 */
public class ReportData {
    private User user;
    private ReportType type;
    private List<Asset> assets;
    private BigDecimal netWorth;
    private Map<AssetType, BigDecimal> assetDistribution;

    public ReportData() {}

    public ReportData(User user, ReportType type, List<Asset> assets, BigDecimal netWorth,
                      Map<AssetType, BigDecimal> assetDistribution) {
        this.user = user;
        this.type = type;
        this.assets = assets;
        this.netWorth = netWorth;
        this.assetDistribution = assetDistribution;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public BigDecimal getNetWorth() {
        return netWorth;
    }
    public void setNetWorth(BigDecimal netWorth) {
        this.netWorth = netWorth;
    }

    public Map<AssetType, BigDecimal> getAssetDistribution() {
        return assetDistribution;
    }
    public void setAssetDistribution(Map<AssetType, BigDecimal> assetDistribution) {
        this.assetDistribution = assetDistribution;
    }
}