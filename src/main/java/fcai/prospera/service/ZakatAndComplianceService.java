package fcai.prospera.service;

import fcai.prospera.model.*;
import fcai.prospera.repository.AssetRepository;
import fcai.prospera.repository.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;

public class ZakatAndComplianceService {
    private final AssetRepository assetRepo;

    static private final double GOLD_NISAB_WEIGHT = 87.48;
    static private final double SILVER_NISAB_WEIGHT = 612.36;

    public ZakatAndComplianceService(AssetRepository assetRepo) {
        this.assetRepo = assetRepo;
    }

    public BigDecimal calculateZakat(double nisab, UUID userId) {
        List<Asset> assets = assetRepo.getUserAssets(userId);
        return BigDecimal.ZERO;
    }

    public List<Asset> getZakatableAssets(UUID userId) {
        return Collections.emptyList();
    }

    static public double getGoldNisab(double goldExchangeRate) {
        return goldExchangeRate * GOLD_NISAB_WEIGHT;
    }

    static public double getSilverNisab(double silverExchangeRate) {
        return silverExchangeRate * SILVER_NISAB_WEIGHT;
    }

    public Date getHawlDate(UUID assetId) {
        Date purchaseDate = assetRepo.getAssetById(assetId).getPurchaseDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(purchaseDate);
        cal.add(Calendar.DAY_OF_YEAR, 354); // one lunar year after purchase date
        return cal.getTime();
    }

    public Boolean hasHawlPassed(UUID assetId) {
        Date hawlDate = getHawlDate(assetId);
        Date now = new Date();
        return hawlDate.before(now) || hawlDate.equals(now);
    }
}