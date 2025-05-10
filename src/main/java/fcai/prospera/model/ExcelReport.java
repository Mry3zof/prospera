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

    private int createDistributionHeader(Sheet sheet, CellStyle headerStyle, int rowNum) {
        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Asset Distribution");
        headerCell.setCellStyle(headerStyle);
        return rowNum;
    }

    private void populateDistributionRows(Sheet sheet, ReportData reportData, int rowNum) {
        for (Map.Entry<AssetType, BigDecimal> entry : reportData.getAssetDistribution().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey().toString());
            row.createCell(1).setCellValue(entry.getValue().doubleValue() + "%");
        }
    }

    // Style creation helpers
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createCurrencyStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(CURRENCY_FORMAT));
        return style;
    }

    private CellStyle createPercentageStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(PERCENTAGE_FORMAT));
        return style;
    }

    // Utility methods
    private void createNumericCell(Row row, int column, BigDecimal value, CellStyle style) {
        if (value != null) {
            Cell cell = row.createCell(column);
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(style);
        }
    }

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

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

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