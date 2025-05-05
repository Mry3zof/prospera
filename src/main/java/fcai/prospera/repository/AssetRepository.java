package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetRepository {
    boolean addAsset(Asset asset);
    boolean removeAsset(UUID assetsId);
    boolean updateAsset(UUID assetsId, Asset newAsset);
    boolean updateCurrentValue(UUID assetsId, BigDecimal newValue);
    Asset getAssetById(UUID assetsId);
    List<Asset> getUserAssets(UUID userId);
    BigDecimal calculateNetWorth(UUID userId);
    Map<AssetType, BigDecimal> getUserAssetDistribution(UUID userId);
    List<Asset> getNonShariaCompliantAssets(UUID userId);
}