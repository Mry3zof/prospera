package fcai.prospera.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFSheetConditionalFormatting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * This class is responsible for generating an Excel report based on the provided report data.
 * It uses the Apache POI library to create and manipulate Excel Files.
 */
public class ExcelReport implements Report {
    private byte[] data;

    // Column constants
    private static final int COL_ASSET_NAME = 0;
    private static final int COL_ASSET_TYPE = 1;
    private static final int COL_PURCHASE_PRICE = 2;
    private static final int COL_CURRENT_VALUE = 3;
    private static final int COL_ROI = 4;

    // Format constants
    private static final String CURRENCY_FORMAT = "#,##0.00";
    private static final String PERCENTAGE_FORMAT = "0.00%";

    /**
     * Generates a report in Excel format based on the given {@link ReportData}.
     *
     * @param reportData the report data to generate the report from
     */
    @Override
    public void generateData(ReportData reportData) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Portfolio");
            int currentRow = 0;

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle percentageStyle = createPercentageStyle(workbook);

            // Header Section
            currentRow = createReportHeader(sheet, reportData, headerStyle, currentRow);
            currentRow++;

            // Asset Table
            currentRow = createAssetTableHeader(sheet, headerStyle, currentRow);
            currentRow = populateAssetRows(sheet, reportData, currencyStyle, percentageStyle, currentRow);
            currentRow++;

            // Asset Distribution
            currentRow = createDistributionHeader(sheet, headerStyle, currentRow);
            populateDistributionRows(sheet, reportData, currentRow);


            autoSizeColumns(sheet);

            workbook.write(baos);
            this.data = baos.toByteArray();
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Creates the header section of the report.
     *
     * @param sheet     the sheet to write the header to
     * @param reportData the report data to get the username and net worth from
     * @param headerStyle the style to use for the header cells
     * @param rowNum     the starting row number to write the header to
     * @return the next row number to use for writing the report
     */
    private int createReportHeader(Sheet sheet, ReportData reportData, CellStyle headerStyle, int rowNum) {
        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Portfolio Report for: " + reportData.getUser().getUsername());
        headerCell.setCellStyle(headerStyle);

        Row netWorthRow = sheet.createRow(rowNum++);
        netWorthRow.createCell(0).setCellValue("Net Worth: ");
        netWorthRow.createCell(1).setCellValue(reportData.getNetWorth().doubleValue());
        netWorthRow.getCell(1).setCellStyle(createCurrencyStyle((XSSFWorkbook) sheet.getWorkbook()));

        return rowNum;
    }

    /**
     * Creates the header row for the asset table in the Excel sheet.
     *
     * @param sheet the sheet to write the asset table header to
     * @param headerStyle the style to apply to the header cells
     * @param rowNum the starting row number for the asset table header
     * @return the next row number after the header row
     */
    private int createAssetTableHeader(Sheet sheet, CellStyle headerStyle, int rowNum) {
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Asset Name", "Type", "Purchase Price", "Current Value", "ROI"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        return rowNum;
    }

    /**
     * Populates the asset rows in the Excel sheet.
     *
     * @param sheet the sheet to write the asset rows to
     * @param reportData the report data to get the assets from
     * @param currencyStyle the style to apply to the currency cells
     * @param percentageStyle the style to apply to the ROI percentage cells
     * @param rowNum the starting row number for the asset rows
     * @return the next row number after the asset rows
     */
    private int populateAssetRows(Sheet sheet, ReportData reportData,
                                  CellStyle currencyStyle, CellStyle percentageStyle, int rowNum) {
        int firstDataRow = rowNum;
        for (Asset asset : reportData.getAssets()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(COL_ASSET_NAME).setCellValue(asset.getName());

            if (asset.getType() != null) {
                row.createCell(COL_ASSET_TYPE).setCellValue(asset.getType().toString());
            }

            createNumericCell(row, COL_PURCHASE_PRICE,
                    asset.getPurchasePrice(), currencyStyle);

            createNumericCell(row, COL_CURRENT_VALUE,
                    asset.getCurrentValue(), currencyStyle);

            Cell roiCell = row.createCell(COL_ROI);
            if (asset.calculateROI() != null) {
                double roi = asset.calculateROI().doubleValue() / 100;
                roiCell.setCellValue(roi);
                roiCell.setCellStyle(percentageStyle);
            }
        }

        int lastDataRow = rowNum - 1;

        if (lastDataRow >= firstDataRow) {
            applyConditionalFormatting(
                    (XSSFWorkbook) sheet.getWorkbook(),
                    sheet,
                    firstDataRow,
                    lastDataRow
            );
        }
        return rowNum;
    }

    /**
     * Creates the header row for the asset distribution section in the Excel sheet.
     *
     * @param sheet     the sheet to write the header to
     * @param headerStyle the style to apply to the header cells
     * @param rowNum     the starting row number for the header row
     * @return the next row number after the header row
     */
    private int createDistributionHeader(Sheet sheet, CellStyle headerStyle, int rowNum) {
        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Asset Distribution");
        headerCell.setCellStyle(headerStyle);
        return rowNum;
    }

    /**
     * Populates the rows of the Excel sheet with asset distribution data.
     *
     * @param sheet the Excel sheet where the distribution data will be written
     * @param reportData the report data containing asset distribution information
     * @param rowNum the starting row number for writing distribution data
     */
    private void populateDistributionRows(Sheet sheet, ReportData reportData, int rowNum) {
        for (Map.Entry<AssetType, BigDecimal> entry : reportData.getAssetDistribution().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey().toString());
            row.createCell(1).setCellValue(entry.getValue().doubleValue() + "%");
        }
    }

    /**
     * Creates and returns a CellStyle for Excel header cells with bold font.
     *
     * @param workbook the XSSFWorkbook to create the style from
     * @return a CellStyle with bold font for header cells
     */
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * Creates and returns a CellStyle for Excel cells to format numbers as currency.
     *
     * @param workbook The XSSFWorkbook to create the style from
     * @return A CellStyle with a currency format applied
     */
    private CellStyle createCurrencyStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(CURRENCY_FORMAT));
        return style;
    }

    /**
     * Creates and returns a CellStyle for Excel cells to format numbers as percentages.
     *
     * @param workbook The XSSFWorkbook to create the style from
     * @return A CellStyle with a percentage format applied
     */
    private CellStyle createPercentageStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(PERCENTAGE_FORMAT));
        return style;
    }

    /**
     * Creates a numeric cell in the given row at the specified column with the given value and style.
     * If the value is null, the cell is not created.
     *
     * @param row the row to create the cell in
     * @param column the column to create the cell in
     * @param value the value for the cell
     * @param style the style to apply to the cell
     */
    private void createNumericCell(Row row, int column, BigDecimal value, CellStyle style) {
        if (value != null) {
            Cell cell = row.createCell(column);
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(style);
        }
    }

    /**
     * Applies conditional formatting to a specified range of rows in the Excel sheet.
     * It highlights the ROI column with green if the value is non-negative and red if negative.
     *
     * @param workbook the XSSFWorkbook to apply formatting rules on
     * @param sheet the Excel sheet where the rules will be applied
     * @param firstDataRow the zero-based index of the first row containing data to format
     * @param lastDataRow the zero-based index of the last row containing data to format
     */
    private void applyConditionalFormatting(XSSFWorkbook workbook, Sheet sheet,
                                            int firstDataRow, int lastDataRow) {
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        // Convert to Excel 1-based row numbers
        int firstExcelRow = firstDataRow + 1;
        int lastExcelRow = lastDataRow + 1;

        // Create relative formulas
        String greenFormula = "E" + firstExcelRow + ">=0";
        String redFormula = "E" + firstExcelRow + "<0";

        // Create rules
        ConditionalFormattingRule greenRule = sheetCF.createConditionalFormattingRule(greenFormula);
        PatternFormatting greenPattern = greenRule.createPatternFormatting();
        greenPattern.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        greenPattern.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        ConditionalFormattingRule redRule = sheetCF.createConditionalFormattingRule(redFormula);
        PatternFormatting redPattern = redRule.createPatternFormatting();
        redPattern.setFillBackgroundColor(IndexedColors.RED.getIndex());
        redPattern.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

        // Define precise range
        String rangeAddress = "E" + firstExcelRow + ":E" + lastExcelRow;
        CellRangeAddress[] regions = { CellRangeAddress.valueOf(rangeAddress) };

        // Apply formatting
        sheetCF.addConditionalFormatting(regions, greenRule, redRule);
    }

    /**
     * Automatically adjusts the width of the columns in the specified Excel sheet
     * to fit the contents. This method resizes the first five columns of the sheet.
     *
     * @param sheet the Excel sheet whose columns are to be resized
     */
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    /**
     * Handles errors encountered during Excel generation by logging the error details
     * and setting the data to an empty byte array.
     *
     * @param e the exception that was thrown during Excel generation
     */
    private void handleError(Exception e) {
        System.err.println("Excel Generation Failed:");
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