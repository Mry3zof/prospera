package fcai.prospera.model;

public class PDFReport implements Report {
    private byte[] data;

    public PDFReport() {
    }

    @Override
    public void generateData(ReportData reportData) {
        System.out.println("Generating PDF report for user: " + reportData.getUser().getUsername());

        this.data = new byte[1024]; // TODO: implement this
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}