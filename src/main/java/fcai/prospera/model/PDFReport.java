package fcai.prospera.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;


/**
 * This class is responsible for generating a PDF report based on the provided report data.
 * It uses the Apache PDFBox library to create and manipulate PDF documents.
 */
public class PDFReport implements Report {
    private byte[] data;
    private static final float LEFT_MARGIN = 50;
    private static final float LINE_HEIGHT = 15;
    private static final float HEADER_FONT_SIZE = 12;
    private static final float TABLE_FONT_SIZE = 10;

    /**
     * Generates a PDF report from the given report data.
     * The report includes the asset name, asset value, zakat percentage and zakat amount.
     * The report also includes a pie chart of the asset distribution.
     * @param reportData the data to generate the report from.
     */
    @Override
    public void generateData(ReportData reportData) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 700;

                yPosition = createHeader(contentStream, reportData, yPosition);
                yPosition = createTableHeader(contentStream, yPosition);
                yPosition = createAssetRows(contentStream, reportData, yPosition);
                createAssetDistribution(contentStream, reportData, yPosition);

            }

            document.save(baos);
            this.data = baos.toByteArray();
        }
        catch (IOException e) {
            handlePdfError(e);
        }
    }

    /**
     * Generates the header of the report, which includes the user name and their net worth.
     * The text is written in a bold font and the net worth is formatted as a currency.
     * @param contentStream the stream for writing to the PDF.
     * @param reportData the report data to use.
     * @param yPosition the current y position of the text.
     * @return the updated y position after writing the header.
     */
    private float createHeader(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), HEADER_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);

        contentStream.showText("Portfolio Report for: " + reportData.getUser().getUsername());
        yPosition = addNewLine(contentStream, yPosition, LINE_HEIGHT * 1.5f);

        contentStream.showText("Net Worth: " + reportData.getNetWorth().toString());
        yPosition = addNewLine(contentStream, yPosition, LINE_HEIGHT * 2);

        contentStream.endText();
        return yPosition;
    }

    /**
     * Creates the table header with fixed positions for the columns.
     * The columns are Asset Name, Type, Purchase Price, Current Value, and ROI (%).
     * The text is written in a bold font and the current y position is decremented by the line height.
     * @param contentStream the stream for writing to the PDF.
     * @param yPosition the current y position of the text.
     * @return the updated y position after writing the table header.
     */
    private float createTableHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), TABLE_FONT_SIZE);
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

    /**
     * Generates the rows of the asset table from the given report data.
     * Each row includes the asset name, type, purchase price, current value, and ROI (%).
     * The text is written in a normal font and the current y position is decremented by the line height.
     * @param contentStream the stream for writing to the PDF.
     * @param reportData the report data to use.
     * @param yPosition the current y position of the text.
     * @return the updated y position after writing the table rows.
     */
    private float createAssetRows(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), TABLE_FONT_SIZE);

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
        return yPosition - LINE_HEIGHT;
    }

    /**
     * Generates the asset distribution table from the given report data.
     * The table includes the asset type and its percentage of the net worth.
     * The text is written in a bold font for the header and a normal font for the rows.
     * The current y position is decremented by the line height for each row.
     * @param contentStream the stream for writing to the PDF.
     * @param reportData the report data to use.
     * @param yPosition the current y position of the text.
     */
    private void createAssetDistribution(PDPageContentStream contentStream, ReportData reportData, float yPosition)
            throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), HEADER_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);
        contentStream.showText("Asset Distribution:");
        contentStream.endText();

        yPosition -= LINE_HEIGHT;
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), TABLE_FONT_SIZE);

        for (Map.Entry<AssetType, BigDecimal> entry : reportData.getAssetDistribution().entrySet()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(LEFT_MARGIN, yPosition);
            contentStream.showText(entry.getKey() + ": " + entry.getValue() + "%");
            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }
    }


    /**
     * Writes the name of an asset to the PDF and moves the stream to the next line.
     * The name is truncated to 20 characters.
     * @param contentStream the stream for writing to the PDF.
     * @param asset the asset for which to write the name.
     * @throws IOException if an IO error occurs.
     */
    private void addAssetName(PDPageContentStream contentStream, Asset asset) throws IOException {
        contentStream.showText(truncate(asset.getName(), 20));
        contentStream.newLineAtOffset(120, 0);
    }

    /**
     * Writes the type of an asset to the PDF and moves the stream to the next line.
     * @param contentStream the stream for writing to the PDF.
     * @param asset the asset for which to write the type.
     * @throws IOException if an IO error occurs.
     */
    private void addAssetType(PDPageContentStream contentStream, Asset asset) throws IOException {
        contentStream.showText(asset.getType().toString());
        contentStream.newLineAtOffset(120, 0);
    }

    /**
     * Writes the purchase price of an asset to the PDF and moves the stream to the next line.
     * If the purchase price is null, the string "N/A" is written instead.
     * @param contentStream the stream for writing to the PDF.
     * @param asset the asset for which to write the purchase price.
     * @throws IOException if an IO error occurs.
     */
    private void addPurchasePrice(PDPageContentStream contentStream, Asset asset) throws IOException {
        String price = asset.getPurchasePrice() != null ?
                asset.getPurchasePrice().toPlainString() : "N/A";
        contentStream.showText(price);
        contentStream.newLineAtOffset(100, 0);
    }

    /**
     * Writes the current value of an asset to the PDF and moves the stream to the next line.
     * If the current value is null, the string "N/A" is written instead.
     * @param contentStream the stream for writing to the PDF.
     * @param asset the asset for which to write the current value.
     * @throws IOException if an IO error occurs.
     */
    private void addCurrentValue(PDPageContentStream contentStream, Asset asset) throws IOException {
        String value = asset.getCurrentValue() != null ?
                asset.getCurrentValue().toPlainString() : "N/A";
        contentStream.showText(value);
        contentStream.newLineAtOffset(100, 0);
    }

    /**
     * Writes the ROI percentage of an asset to the PDF and moves the stream to the next line.
     * If the ROI is null, the string "N/A" is written instead.
     * The ROI is written in green if it is positive, and in red if it is negative.
     * @param contentStream the stream for writing to the PDF.
     * @param asset the asset for which to write the ROI percentage.
     * @throws IOException if an IO error occurs.
     */
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

    /**
     * Sets the color of the ROI percentage based on its value.
     * If the ROI is positive, the color is green.
     * If the ROI is negative, the color is red.
     * @param contentStream the stream for writing to the PDF.
     * @param roi the ROI percentage for which to set the color.
     * @throws IOException if an IO error occurs.
     */
    private void setRoiColor(PDPageContentStream contentStream, BigDecimal roi) throws IOException {
        if (roi.compareTo(BigDecimal.ZERO) >= 0) {
            contentStream.setNonStrokingColor(0, 128/255f, 0); // Green
        } else {
            contentStream.setNonStrokingColor(128/255f, 0, 0); // Red
        }
    }

    /**
     * Resets the text color to black.
     * @param contentStream the stream for writing to the PDF.
     * @throws IOException if an IO error occurs.
     */
    private void resetTextColor(PDPageContentStream contentStream) throws IOException {
        contentStream.setNonStrokingColor(0, 0, 0); // Black
    }

    /**
     * Adds a new line to the PDF and moves the stream to the next line.
     * The y position is decremented by the given spacing.
     * @param contentStream the stream for writing to the PDF.
     * @param currentY the current y position.
     * @param spacing the spacing between lines.
     * @return the new y position.
     * @throws IOException if an IO error occurs.
     */
    private float addNewLine(PDPageContentStream contentStream, float currentY, float spacing) throws IOException {
        contentStream.newLineAtOffset(0, -spacing);
        return currentY - spacing;
    }

    /**
     * Truncates a string to a given maximum length and appends an ellipsis if the string is longer than the maximum length.
     * If the string is null, the string "N/A" is returned instead.
     * @param text the string to truncate.
     * @param maxLength the maximum length of the string.
     * @return the truncated string.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        return text.length() > maxLength ? text.substring(0, maxLength-1) + "…" : text;
    }

    /**
     * Handles an exception that occurs during PDF generation by printing the stack trace
     * and setting the generated PDF data to an empty array.
     * @param e the exception that occurred.
     */
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