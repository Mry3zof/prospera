package fcai.prospera.controller;

import fcai.prospera.model.Asset;
import fcai.prospera.service.AssetService;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.AssetView;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AssetController {
    private final AssetService assetService;
    private final AuthService authService;
    private final AssetView assetView;

    public AssetController(AssetService assetService, AuthService authService, AssetView assetView) {
        this.assetService = assetService;
        this.authService = authService;
        this.assetView = assetView;
    }

    public boolean addAsset(Asset asset) {
        boolean success = assetService.addAsset(asset);
        if (!success) assetView.onAddAssetFail();
        return success;
    }

    public boolean editAsset(UUID assetId, Asset newAsset) {
        return assetService.updateAsset(assetId, newAsset);
    }

    public boolean removeAsset(UUID assetId) {
        return assetService.removeAsset(assetId);
    }

    public List<Asset> getAssets(UUID userId) {
        return assetService.getAssets(userId);
    }

    public void assetsChanged() {
        assetView.refreshAssetsList(getAssets(authService.getCurrentUser().getId()));
    }

    public BigDecimal getNetWorth(UUID userId) {
        return assetService.calculateValuation(userId);
    }
}