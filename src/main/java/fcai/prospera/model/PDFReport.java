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
    private static final float LEFT_MARGIN = 50;
    private static final float LINE_HEIGHT = 15;
    private static final float HEADER_FONT_SIZE = 12;
    private static final float TABLE_FONT_SIZE = 10;

    @Override
    public void generateData(ReportData reportData) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 700; // Start from top of page

                // Create report sections
                yPosition = createHeader(contentStream, reportData, yPosition);
                yPosition = createTableHeader(contentStream, yPosition);
                yPosition = createAssetRows(contentStream, reportData, yPosition);
                createAssetDistribution(contentStream, reportData, yPosition);

            } // End contentStream

            document.save(baos);
            this.data = baos.toByteArray();
        }
        catch (IOException e) {
            handlePdfError(e);
        }
    }

    private float createHeader(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);

        contentStream.showText("Portfolio Report for: " + reportData.getUser().getUsername());
        yPosition = addNewLine(contentStream, yPosition, LINE_HEIGHT * 1.5f);

        contentStream.showText("Net Worth: " + reportData.getNetWorth().toString());
        yPosition = addNewLine(contentStream, yPosition, LINE_HEIGHT * 2);

        contentStream.endText();
        return yPosition;
    }

    private float createTableHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, TABLE_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);

        // Column headers with fixed positions
        contentStream.showText("Asset Name");
        contentStream.newLineAtOffset(120, 0);
        contentStream.showText("Type");
        contentStream.newLineAtOffset(120, 0);
        contentStream.showText("Purchase Price");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Current Value");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("ROI (%)");

        contentStream.endText();
        return yPosition - LINE_HEIGHT;
    }

    private float createAssetRows(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, TABLE_FONT_SIZE);

        for (Asset asset : reportData.getAssets()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);

            addAssetName(contentStream, asset);
            addAssetType(contentStream, asset);
            addPurchasePrice(contentStream, asset);
            addCurrentValue(contentStream, asset);
            addRoiPercentage(contentStream, asset);

            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }
        return yPosition - LINE_HEIGHT; // Extra spacing after table
    }

    private void createAssetDistribution(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, HEADER_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);
        contentStream.showText("Asset Distribution:");
        contentStream.endText();

        // Reduced from LINE_HEIGHT * 2 to just LINE_HEIGHT
        yPosition -= LINE_HEIGHT;
        contentStream.setFont(PDType1Font.HELVETICA, TABLE_FONT_SIZE);

        for (Map.Entry<AssetType, BigDecimal> entry : reportData.getAssetDistribution().entrySet()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);
            contentStream.showText(entry.getKey() + ": " + entry.getValue() + "%");
            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }
    }

    // Helper methods for table columns
    private void addAssetName(PDPageContentStream contentStream, Asset asset) throws IOException {
        contentStream.showText(truncate(asset.getName(), 20));
        contentStream.newLineAtOffset(120, 0);
    }

    private void addAssetType(PDPageContentStream contentStream, Asset asset) throws IOException {
        contentStream.showText(asset.getType().toString());
        contentStream.newLineAtOffset(120, 0);
    }

    private void addPurchasePrice(PDPageContentStream contentStream, Asset asset) throws IOException {
        String price = asset.getPurchasePrice() != null ?
                asset.getPurchasePrice().toPlainString() : "N/A";
        contentStream.showText(price);
        contentStream.newLineAtOffset(100, 0);
    }

    private void addCurrentValue(PDPageContentStream contentStream, Asset asset) throws IOException {
        String value = asset.getCurrentValue() != null ?
                asset.getCurrentValue().toPlainString() : "N/A";
        contentStream.showText(value);
        contentStream.newLineAtOffset(100, 0);
    }

    private void addRoiPercentage(PDPageContentStream contentStream, Asset asset) throws IOException {
        BigDecimal roi = asset.calculateROI();
        if (roi != null) {
            setRoiColor(contentStream, roi);
            contentStream.showText(roi.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            resetTextColor(contentStream);
        } else {
            contentStream.showText("N/A");
        }
    }

    // Style helpers
    private void setRoiColor(PDPageContentStream contentStream, BigDecimal roi) throws IOException {
        if (roi.compareTo(BigDecimal.ZERO) >= 0) {
            contentStream.setNonStrokingColor(0, 128/255f, 0); // Green
        } else {
            contentStream.setNonStrokingColor(128/255f, 0, 0); // Red
        }
    }

    private void resetTextColor(PDPageContentStream contentStream) throws IOException {
        contentStream.setNonStrokingColor(0, 0, 0); // Black
    }

    // General helpers
    private float addNewLine(PDPageContentStream contentStream, float currentY, float spacing) throws IOException {
        contentStream.newLineAtOffset(0, -spacing);
        return currentY - spacing;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        return text.length() > maxLength ? text.substring(0, maxLength-1) + "…" : text;
    }

    private void handlePdfError(Exception e) {
        System.err.println("PDF Generation Failed:");
        e.printStackTrace();
        this.data = new byte[0];
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}