package fcai.prospera.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class PDFReport implements Report {
    private byte[] data;

    @Override
    public void generateData(ReportData reportData) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font and starting position
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                // Add header
                contentStream.showText("Portfolio Report for: " + reportData.getUser().getUsername());
                contentStream.newLineAtOffset(0, -20);

                // Add dates
                String period = "Period: " + reportData.getStartDate() + " to " + reportData.getEndDate();
                contentStream.showText(period);
                contentStream.newLineAtOffset(0, -20);

                // Add net worth
                contentStream.showText("Net Worth: " + reportData.getNetWorth().toString());
                contentStream.newLineAtOffset(0, -30);

                // Add assets table header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.showText("Asset Name      Type      Purchase Price      Current Value      ROI (%)");
                contentStream.newLineAtOffset(0, -15);

                // Add assets
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                for (Asset asset : reportData.getAssets()) {
                    String row = String.format("%-15s %-10s %-18s %-18s %s%%",
                            asset.getName(),
                            asset.getType(),
                            asset.getPurchasePrice(),
                            asset.getCurrentValue(),
                            asset.calculateROI().setScale(2, BigDecimal.ROUND_HALF_UP));
                    contentStream.showText(row);
                    contentStream.newLineAtOffset(0, -15);
                }

                // Add asset distribution
                contentStream.newLineAtOffset(0, -30);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Asset Distribution:");
                contentStream.newLineAtOffset(0, -20);
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                for (Map.Entry<AssetType, BigDecimal> entry : reportData.getAssetDistribution().entrySet()) {
                    contentStream.showText(entry.getKey() + ": " + entry.getValue() + "%");
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.endText();
            }

            document.save(baos);
            this.data = baos.toByteArray();
        } catch (IOException e) {
            System.err.println("PDF Generation Failed:");
            e.printStackTrace();
            this.data = new byte[0];
        }
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}