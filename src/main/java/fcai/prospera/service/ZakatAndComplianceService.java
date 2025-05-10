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
    static private final double ZAKAT_PERCENTAGE = 2.5 / 100.0;

    public ZakatAndComplianceService(AssetRepository assetRepo) {
        this.assetRepo = assetRepo;
    }

    public static BigDecimal calculateZakat(double nisab, List<Asset> selectedAssets) {
        BigDecimal total = selectedAssets.stream().map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.compareTo(BigDecimal.valueOf(nisab)) >= 0 ? total.multiply(BigDecimal.valueOf(ZAKAT_PERCENTAGE)) : BigDecimal.ZERO;
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