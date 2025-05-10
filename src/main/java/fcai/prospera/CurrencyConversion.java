package fcai.prospera;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to handle currency conversion
 */
public class CurrencyConversion {
    private static final Map<String, BigDecimal> conversionRates = new HashMap<>();

    /**
     * Adds a conversion rate to the conversion rates map (base rate is USD)
     * @param fromCode : the currency code to convert from
     * @param rate : the conversion rate
     */
    public static void addConversionRate(String fromCode, BigDecimal rate) {
        conversionRates.put(fromCode, rate);
    }

    static {
        addConversionRate("USD", BigDecimal.ONE);
        addConversionRate("EUR", new BigDecimal("1.13"));
        addConversionRate("GBP", new BigDecimal("1.33"));
        addConversionRate("EGP", new BigDecimal("0.020"));
    }

    /**
     * Converts an amount from one currency to another
     * @param fromCode : the currency code to convert from
     * @param toCode : the currency code to convert to
     * @param amount : the amount to convert
     * @return the converted amount
     */
    public static BigDecimal convert(String fromCode, String toCode, BigDecimal amount) {
        BigDecimal fromRate = conversionRates.get(fromCode);
        BigDecimal toRate = conversionRates.get(toCode);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Invalid currency codes: " + fromCode + " to " + toCode);
        }

        return amount.multiply(fromRate).divide(toRate, 2, RoundingMode.HALF_UP);
    }

}
