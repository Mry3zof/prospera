package fcai.prospera;

public class CurrencyItem {
    private final String code;
    private final String name;
    private final String symbol;

    public CurrencyItem(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }

    public String getDisplayName() {
        return code + " - " + name + " (" + symbol + ")";
    }
}
