package fcai.prospera.model;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExcelReport implements Report {
    private byte[] data;

    public ExcelReport() {

    }

    // TODO: Add distribution
    @Override
    public void generateData(ReportData reportData) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Portfolio");
            int rowNum = 0;

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Asset Name");
            headerRow.createCell(1).setCellValue("Purchase Price");
            headerRow.createCell(2).setCellValue("Current Value");
            headerRow.createCell(3).setCellValue("ROI (%)");

            // Data rows
            for (Asset asset : reportData.getAssets()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(asset.getName());
                row.createCell(1).setCellValue(asset.getPurchasePrice().doubleValue());
                row.createCell(2).setCellValue(asset.getCurrentValue().doubleValue());
                row.createCell(3).setCellValue(asset.calculateROI().doubleValue());
            }

            workbook.write(baos);
            this.data = baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
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