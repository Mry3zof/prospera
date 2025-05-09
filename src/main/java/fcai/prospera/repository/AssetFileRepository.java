package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssetFileRepository implements AssetRepository {
    @Override
    public boolean addAsset(Asset asset) {
        return false;
    }

    @Override
    public boolean removeAsset(UUID assetId) {
        return false;
    }

    @Override
    public boolean updateAsset(UUID assetId, Asset newAsset) {
        return false;
    }

    @Override
    public boolean updateCurrentValue(UUID assetId, BigDecimal newValue) {
        return false;
    }

    @Override
    public Asset getAssetById(UUID assetId) {
        return null;
    }

    @Override
    public List<Asset> getUserAssets(UUID userId) {
        return List.of();
    }

    @Override
    public BigDecimal calculateNetWorth(UUID userId) {
        return null;
    }

    @Override
    public Map<AssetType, BigDecimal> getUserAssetDistribution(UUID userId) {
        return Map.of();
    }

    @Override
    public List<Asset> getNonShariaCompliantAssets(UUID userId) {
        return List.of();
    }
}
