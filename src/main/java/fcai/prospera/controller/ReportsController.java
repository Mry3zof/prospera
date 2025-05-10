package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.Report;
import fcai.prospera.model.ReportType;
import fcai.prospera.model.User;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ReportsController {
    private SceneManager sceneManager;
    private ReportGenerationService reportService;
    private AuthService authService;

    public void init(SceneManager sceneManager, AuthService authService, ReportGenerationService reportService) {
        this.sceneManager = sceneManager;
        this.reportService = reportService;
        this.authService = authService;
    }

    @FXML
    private void handleGeneratePDF() {
        User currentUser = authService.getCurrentUser();
        Report report = reportService.generateReport(currentUser, ReportType.PORTFOLIO, "PDF");
        saveReportToFile(report, "PortfolioReport.pdf");
    }

    @FXML
    private void handleGenerateExcel() {
        User currentUserId = authService.getCurrentUser();
        Report report = reportService.generateReport(currentUserId, ReportType.PORTFOLIO, "Excel");
        saveReportToFile(report, "PortfolioReport.xlsx");
    }

    private void saveReportToFile(Report report, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(defaultFileName);
        File file = fileChooser.showSaveDialog(sceneManager.getStage());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] reportData = report.getData();

                // TODO: remove
                System.out.println("PDF size: " + reportData.length + " bytes");
                if (reportData.length == 0) {
                    throw new IOException("Empty PDF data!");
                }

                fos.write(reportData);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDashboardView() {
        try {
            sceneManager.showDashboardView();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}