package fcai.prospera.model;

public class ExcelReport implements Report {
    private byte[] data;

    public ExcelReport() {

    }

    @Override
    public void generateData(ReportData reportData) {
        System.out.println("Generating Excel report for user: " + reportData.getUser().getUsername());

        this.data = new byte[1024]; // Placeholder
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}