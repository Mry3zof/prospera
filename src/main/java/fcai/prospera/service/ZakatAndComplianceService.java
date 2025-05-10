package fcai.prospera.service;

import fcai.prospera.model.*;
import fcai.prospera.repository.AssetRepository;
import fcai.prospera.repository.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;

/**
 * This class handles the logic for calculating zakat and compliance
 */
public class ZakatAndComplianceService {
    private final AssetRepository assetRepo;

    static private final double GOLD_NISAB_WEIGHT = 87.48;
    static private final double SILVER_NISAB_WEIGHT = 612.36;
    static private final double ZAKAT_PERCENTAGE = 2.5 / 100.0;

    /**
     * The constructor for ZakatAndComplianceService
     * @param assetRepo : the asset repository from which asset data will be fetched
     */
    public ZakatAndComplianceService(AssetRepository assetRepo) {
        this.assetRepo = assetRepo;
    }

    /**
     * This method handles zakat calculation
     * @param nisab : the nisab value, which is the minimum amount of assets required to be eligible for zakat
     * @param selectedAssets : the list of assets selected by the user to apply zakat on
     * @return the zakat amount
     */
    public static BigDecimal calculateZakat(double nisab, List<Asset> selectedAssets) {
        BigDecimal total = selectedAssets.stream().map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.compareTo(BigDecimal.valueOf(nisab)) >= 0 ? total.multiply(BigDecimal.valueOf(ZAKAT_PERCENTAGE)) : BigDecimal.ZERO;
    }


    /**
     * This function calculates the gold nisab based on the gold exchange rate
     * @param goldExchangeRate : the gold exchange rate
     * @return the gold nisab
     */
    static public double getGoldNisab(double goldExchangeRate) {
        return goldExchangeRate * GOLD_NISAB_WEIGHT;
    }

    /**
     * This function calculates the silver nisab based on the silver exchange rate
     * @param silverExchangeRate : the silver exchange rate
     * @return the silver nisab
     */
    static public double getSilverNisab(double silverExchangeRate) {
        return silverExchangeRate * SILVER_NISAB_WEIGHT;
    }

    /**
     * This function returns the date of the Hawl for an asset
     * @param assetId : the asset in question
     * @return the date of the Hawl
     */
    public Date getHawlDate(UUID assetId) {
        Date purchaseDate = assetRepo.getAssetById(assetId).getPurchaseDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(purchaseDate);
        cal.add(Calendar.DAY_OF_YEAR, 354); // one lunar year after purchase date
        return cal.getTime();
    }

    /**
     * Checks whether the Hawl for an asset has passed
     * @param assetId : the asset in question
     * @return true if the Hawl has passed, false otherwise
     */
    public Boolean hasHawlPassed(UUID assetId) {
        Date hawlDate = getHawlDate(assetId);
        Date now = new Date();
        return hawlDate.before(now) || hawlDate.equals(now);
    }
}