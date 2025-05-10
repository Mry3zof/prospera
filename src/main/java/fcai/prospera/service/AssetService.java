package fcai.prospera.service;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;
import fcai.prospera.repository.AssetRepository;
import fcai.prospera.CurrencyConversion; // Import the conversion utility

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class AssetService {
    private final AssetRepository assetRepo;

    // ... (existing constructor and other methods) ...
    public AssetService(AssetRepository assetRepo) {
        if (assetRepo == null) {
            throw new IllegalArgumentException("AssetRepository cannot be null.");
        }
        this.assetRepo = assetRepo;
    }

    public Asset getAssetById(UUID assetId) {
        if (assetId == null) return null;
        return assetRepo.getAssetById(assetId);
    }

    public List<Asset> getAssets(UUID userId) {
        if (userId == null) return Collections.emptyList();
        return assetRepo.getUserAssets(userId);
    }

    public boolean addAsset(Asset asset) {
        if (asset == null) return false;
        return assetRepo.addAsset(asset);
    }

    public boolean removeAsset(UUID assetId) {
        if (assetId == null) return false;
        return assetRepo.removeAsset(assetId);
    }

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

    public boolean updateAssetCurrentValue(UUID assetId, BigDecimal newValue) {
        if (assetId == null || newValue == null) return false;
        return assetRepo.updateCurrentValue(assetId, newValue);
    }

    public BigDecimal calculateValuation(List<Asset> assets) {
        if (assets == null || assets.isEmpty()) return BigDecimal.ZERO;
        // This method still sums directly. It might be less used if
        // calculateUserNetWorthInBase is the primary way to get total value.
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
                        // Optionally, skip this asset or add 0, or rethrow if critical
                        valueInBase = BigDecimal.ZERO; // Or handle as an error
                    }
                }
                totalNetWorthInBase = totalNetWorthInBase.add(valueInBase);
            }
        }
        return totalNetWorthInBase;
    }


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

    public Map<AssetType, BigDecimal> getAssetDistributionForUser(UUID userId) {
        if (userId == null) return Collections.emptyMap();
        // This would also need currency conversion for a meaningful distribution
        // if assets are in mixed currencies.
        return assetRepo.getUserAssetDistribution(userId);
    }

    public List<Asset> getNonShariaCompliantAssetsForUser(UUID userId) {
        if (userId == null) return Collections.emptyList();
        return assetRepo.getNonShariaCompliantAssets(userId);
    }
}