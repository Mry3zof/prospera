package fcai.prospera.repository;

import fcai.prospera.model.Asset;
import fcai.prospera.model.AssetType;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class AssetFileRepository implements AssetRepository {

    private final String ASSETS_FILE_PATH = "data/assets.ser";
    private final File assetsStorage;
    private HashMap<UUID, Asset> assets;

    public AssetFileRepository() {
        assetsStorage = new File(ASSETS_FILE_PATH);
        assets = new HashMap<>();
        loadAssets();
    }

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

    @Override
    public boolean updateAsset(UUID assetId, Asset newAsset) {
        if (assetId == null || newAsset == null || !assets.containsKey(assetId)) {
            return false;
        }

        assets.put(assetId, newAsset);
        saveAssets();
        return true;
    }

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

    // TODO: implement
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
