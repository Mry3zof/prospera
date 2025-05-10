package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Implements the AssetRepository interface using a file to store assets
 */
public class AssetFileRepository implements AssetRepository {

    private final String ASSETS_FILE_PATH = "data/assets.ser";
    private final File assetsStorage;
    private HashMap<UUID, Asset> assets;

    public AssetFileRepository() {
        assetsStorage = new File(ASSETS_FILE_PATH);
        assets = new HashMap<>();
        loadAssets();
    }

    /**
     * Loads assets from file
     */
    private void loadAssets() {
        if (!assetsStorage.exists() || assetsStorage.length() == 0) {
            return;
        }

        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(assetsStorage))) {
            assets = (HashMap<UUID, Asset>) objectIn.readObject();
        }
        catch (IOException | ClassNotFoundException exception) {
            System.err.println("Error loading assets from " + ASSETS_FILE_PATH + ": " + exception.getMessage());
        }
    }

    /**
     * Saves assets to file
     */
    private void saveAssets() {
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(assetsStorage))) {
            objectOut.writeObject(assets);
        }
        catch (IOException exception) {
            System.err.println("Error saving assets to " + ASSETS_FILE_PATH + ": " + exception.getMessage());
        }
    }

    @Override
    public boolean addAsset(Asset asset) {
        if (asset == null || assets.containsKey(asset.getId())) {
            return false;
        }

        assets.put(asset.getId(), asset);
        saveAssets();
        return true;
    }

    @Override
    public boolean removeAsset(UUID assetId) {
        Asset removedAsset = assets.remove(assetId);
        if (removedAsset == null) {
            return false;
        }

        saveAssets();
        return true;
    }

    /**
     * Updates the asset with the given ID with the given new asset data.
     *
     * @param assetId the ID of the asset to update
     * @param newAsset the new asset data
     * @return true if the update is successful, false otherwise
     */
    @Override
    public boolean updateAsset(UUID assetId, Asset newAsset) {
        if (assetId == null || newAsset == null || !assets.containsKey(assetId)) {
            return false;
        }

        assets.put(assetId, newAsset);
        saveAssets();
        return true;
    }

    /**
     * Updates the current value of the specified asset.
     *
     * @param assetId the UUID of the asset to update
     * @param newValue the new value to set for the asset
     * @return true if the asset's value was successfully updated, false if the asset does not exist or input is invalid
     */
    @Override
    public boolean updateCurrentValue(UUID assetId, BigDecimal newValue) {
        if (assetId == null || newValue == null || !assets.containsKey(assetId)) {
            return false;
        }

        Asset asset = assets.get(assetId);
        asset.setCurrentValue(newValue);
        saveAssets();
        return true;
    }

    @Override
    public Asset getAssetById(UUID assetId) {
        return (assetId != null ? assets.get(assetId) : null);
    }

    @Override
    public List<Asset> getUserAssets(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        List<Asset> userAssets = new ArrayList<>();
        for (Asset asset : assets.values()) {
            if (userId.equals(asset.getUserId())) {
                userAssets.add(asset);
            }
        }

        return userAssets;
    }

    /**
     * Calculates the total net worth of a user's assets
     * @param userId the id of the user
     * @return the total net worth of the user's assets
     */
    @Override
    public BigDecimal calculateNetWorth(UUID userId) {
        List<Asset> userAssets = getUserAssets(userId);
        return userAssets.stream().map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the percentage of each asset type in a user's assets.
     * <p>
     * This method directly sums values and does NOT perform currency conversion.
     *
     * @param userId The ID of the user.
     * @return A map of asset Types to their percentages.
     */
    @Override
    public Map<AssetType, BigDecimal> getUserAssetDistribution(UUID userId) {
        List<Asset> userAssets = getUserAssets(userId);
        BigDecimal totalNetWorth = calculateNetWorth(userId);
        Map<AssetType, BigDecimal> distribution = new HashMap<>();

        for (Asset asset : userAssets) {
            AssetType type = asset.getType();
            BigDecimal assetValue = asset.getCurrentValue();
            BigDecimal percentage = assetValue.divide(totalNetWorth, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            distribution.put(type, distribution.getOrDefault(type, BigDecimal.ZERO).add(percentage));
        }
        return distribution;
    }
}
