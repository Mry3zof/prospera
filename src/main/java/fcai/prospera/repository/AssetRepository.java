package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An interface to handle database communication for asset data
 */
public interface AssetRepository {
    /**
     * Adds asset to database
     * @param asset : asset to be added
     * @return true if asset was added, false otherwise
     */
    boolean addAsset(Asset asset);

    /**
     * Removes asset from database
     * @param assetId : asset id to be removed
     * @return true if asset was removed, false otherwise
     */
    boolean removeAsset(UUID assetId);

    /**
     * Updates asset in database
     * @param assetId : asset id to update
     * @param newAsset : new asset data
     * @return true if asset was updated, false otherwise
     */
    boolean updateAsset(UUID assetId, Asset newAsset);

    /**
     * Updates the current value of an asset in the database
     * @param assetId : asset id to be updated
     * @param newValue : new value
     * @return true if updated, false otherwise
     */
    boolean updateCurrentValue(UUID assetId, BigDecimal newValue);

    /**
     * Gets asset by id
     * @param assetId : asset id
     * @return asset
     */
    Asset getAssetById(UUID assetId);

    /**
     * Gets all assets for a user
     * @param userId : user id
     * @return list of assets
     */
    List<Asset> getUserAssets(UUID userId);

    /**
     * Calculates the net worth of a user
     * @param userId : user id
     * @return net worth
     */
    BigDecimal calculateNetWorth(UUID userId);

    /**
     * gets the asset distribution of a user
     * @param userId : user id
     * @return asset distribution
     */
    Map<AssetType, BigDecimal> getUserAssetDistribution(UUID userId);

    /**
     * Gets all non-sharia compliant assets for a user
     * @param userId : user id
     * @return list of assets
     */
    List<Asset> getNonShariaCompliantAssets(UUID userId);
}