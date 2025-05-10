package fcai.prospera.service;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import fcai.prospera.repository.AssetRepository;
import fcai.prospera.CurrencyConversion; // Import the conversion utility

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the business logic for assets
 */
public class AssetService {
    private final AssetRepository assetRepo;

    /**
     *
     * @param assetRepo : the asset repository
     */
    public AssetService(AssetRepository assetRepo) {
        if (assetRepo == null) {
            throw new IllegalArgumentException("AssetRepository cannot be null.");
        }
        this.assetRepo = assetRepo;
    }

    /**
     * Gets an asset by its ID
     * @param assetId : the asset ID
     * @return the asset
     */
    public Asset getAssetById(UUID assetId) {
        if (assetId == null) return null;
        return assetRepo.getAssetById(assetId);
    }

    /**
     * Gets all assets for a user
     * @param userId : the user ID
     * @return a list of assets
     */
    public List<Asset> getAssets(UUID userId) {
        if (userId == null) return Collections.emptyList();
        return assetRepo.getUserAssets(userId);
    }

    /**
     * Adds an asset
     * @param asset : the asset to add
     * @return true if the asset was added, false otherwise
     */
    public boolean addAsset(Asset asset) {
        if (asset == null) return false;
        return assetRepo.addAsset(asset);
    }

    /**
     * Removes an asset
     * @param assetId : the asset ID
     * @return true if the asset was removed, false otherwise
     */
    public boolean removeAsset(UUID assetId) {
        if (assetId == null) return false;
        return assetRepo.removeAsset(assetId);
    }

    /**
     * Updates an asset
     * @param assetIdToUpdate : the asset ID to update
     * @param newAssetData : the new asset data
     * @return true if the asset was updated, false otherwise
     */
    public boolean updateAsset(UUID assetIdToUpdate, Asset newAssetData) {
        if (assetIdToUpdate == null || newAssetData == null) {
            return false;
        }
        Asset assetToUpdate = assetRepo.getAssetById(assetIdToUpdate);
        if (assetToUpdate != null) {
            assetToUpdate.setUserId(newAssetData.getUserId());
            assetToUpdate.setName(newAssetData.getName());
            assetToUpdate.setType(newAssetData.getType());
            assetToUpdate.setPurchasePrice(newAssetData.getPurchasePrice());
            assetToUpdate.setPurchaseDate(newAssetData.getPurchaseDate());
            assetToUpdate.setCurrentValue(newAssetData.getCurrentValue());
            assetToUpdate.setCurrency(newAssetData.getCurrency());
            return assetRepo.updateAsset(assetIdToUpdate, assetToUpdate);
        }
        return false;
    }

    /**
     * Updates the current value of an asset
     * @param assetId : the asset ID
     * @param newValue : the new value
     * @return true if the asset was updated, false otherwise
     */
    public boolean updateAssetCurrentValue(UUID assetId, BigDecimal newValue) {
        if (assetId == null || newValue == null) return false;
        return assetRepo.updateCurrentValue(assetId, newValue);
    }

    /**
     * Calculates the total value of a list of assets.
     * WARNING: This method directly sums values and does NOT perform currency conversion.
     * Use calculateValuationInBase for a currency-converted sum.
     *
     * @param assets The list of assets.
     * @return The raw sum of current values of the assets.
     */
    public BigDecimal calculateValuation(List<Asset> assets) {
        if (assets == null || assets.isEmpty()) return BigDecimal.ZERO;
        return assets.stream()
                .filter(asset -> asset != null && asset.getCurrentValue() != null)
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the net worth for a specific user by summing asset values.
     * WARNING: This method directly sums values and does NOT perform currency conversion.
     * Use calculateUserNetWorthInBase for a currency-converted sum.
     *
     * @param userId The ID of the user.
     * @return The raw sum of current values of the user's assets.
     */
    public BigDecimal calculateUserNetWorth(UUID userId) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        // This delegates to the repository, which also likely sums directly.
        return assetRepo.calculateNetWorth(userId);
    }

    /**
     * Calculates the total net worth of a user's assets, converted to a specified base currency.
     *
     * @param userId The ID of the user.
     * @param baseCurrencyCode The currency code (e.g., "USD", "EUR") to convert all asset values to.
     * @return The total net worth in the specified base currency. Returns BigDecimal.ZERO if user has no assets
     *         or if conversion fails for all assets.
     */
    public BigDecimal calculateUserNetWorthInBase(UUID userId, String baseCurrencyCode) {
        if (userId == null || baseCurrencyCode == null || baseCurrencyCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<Asset> userAssets = assetRepo.getUserAssets(userId); // Get assets for the user

        if (userAssets == null || userAssets.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalNetWorthInBase = BigDecimal.ZERO;
        for (Asset asset : userAssets) {
            if (asset != null && asset.getCurrentValue() != null && asset.getCurrency() != null) {
                BigDecimal valueInBase;
                String assetCurrencyCode = asset.getCurrency().getCurrencyCode();

                if (assetCurrencyCode.equalsIgnoreCase(baseCurrencyCode)) {
                    valueInBase = asset.getCurrentValue();
                } else {
                    try {
                        valueInBase = CurrencyConversion.convert(
                                assetCurrencyCode,
                                baseCurrencyCode,
                                asset.getCurrentValue()
                        );
                    } catch (IllegalArgumentException e) {
                        System.err.println("Could not convert asset '" + asset.getName() +
                                "' from " + assetCurrencyCode + " to " + baseCurrencyCode +
                                ": " + e.getMessage());
                        valueInBase = BigDecimal.ZERO;
                    }
                }
                totalNetWorthInBase = totalNetWorthInBase.add(valueInBase);
            }
        }
        return totalNetWorthInBase;
    }

    /**
     * Calculates the performance of each asset in a list.
     * WARNING: This method directly sums values and does NOT perform currency conversion.
     *
     * @param assets The list of assets.
     */
    public Map<UUID, BigDecimal> calculatePerformance(List<Asset> assets) {
        if (assets == null || assets.isEmpty()) return Collections.emptyMap();
        return assets.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Asset::getId,
                        Asset::calculateROI,
                        (roi1, roi2) -> roi1,
                        HashMap::new
                ));
    }

    /**
     * Calculates the percentage of each asset type in a user's assets.
     * WARNING: This method directly sums values and does NOT perform currency conversion.
     *
     * @param userId The ID of the user.
     * @return A map of asset Types to their percentages.
     */
    public Map<AssetType, BigDecimal> getAssetDistributionForUser(UUID userId) {
        if (userId == null) return Collections.emptyMap();
        return assetRepo.getUserAssetDistribution(userId);
    }
}