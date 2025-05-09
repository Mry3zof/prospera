package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetRepository {
    boolean addAsset(Asset asset);
    boolean removeAsset(UUID assetId);
    boolean updateAsset(UUID assetId, Asset newAsset);
    boolean updateCurrentValue(UUID assetId, BigDecimal newValue);
    Asset getAssetById(UUID assetId);
    List<Asset> getUserAssets(UUID userId);
    BigDecimal calculateNetWorth(UUID userId);
    Map<AssetType, BigDecimal> getUserAssetDistribution(UUID userId);
    List<Asset> getNonShariaCompliantAssets(UUID userId);
}