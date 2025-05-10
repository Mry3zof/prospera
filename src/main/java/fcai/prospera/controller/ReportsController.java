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

/**
 * A controller for reports view. Coordinates between reports view and report generation service
 */
public class ReportsController {
    private SceneManager sceneManager;
    private ReportGenerationService reportService;
    private AuthService authService;

    /**
     * Initializes the reports' controller.
     * @param sceneManager : the scene manager
     * @param authService : the authentication service
     * @param reportService : the report generation service
     */
    public void init(SceneManager sceneManager, AuthService authService, ReportGenerationService reportService) {
        this.sceneManager = sceneManager;
        this.reportService = reportService;
        this.authService = authService;
    }

    /**
     * Handles the "Generate PDF" button click event.
     * Generates a portfolio report in PDF format for the current user and prompts the user to save the report to a file.
     */
    @FXML
    private void handleGeneratePDF() {
        User currentUser = authService.getCurrentUser();
        Report report = reportService.generateReport(currentUser, ReportType.PORTFOLIO, "PDF");
        saveReportToFile(report, "PortfolioReport.pdf");
    }

    /**
     * Handles the "Generate Excel" button click event.
     * Generates a portfolio report in Excel format for the current user and prompts the user to save the report to a file.
     */
    @FXML
    private void handleGenerateExcel() {
        User currentUserId = authService.getCurrentUser();
        Report report = reportService.generateReport(currentUserId, ReportType.PORTFOLIO, "Excel");
        saveReportToFile(report, "PortfolioReport.xlsx");
    }

    /**
     * Saves the given report to a file selected by the user.
     * @param report : the report to save
     * @param defaultFileName : the default file name to use if the user doesn't select one
     */
    private void saveReportToFile(Report report, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(defaultFileName);
        File file = fileChooser.showSaveDialog(sceneManager.getStage());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] reportData = report.getData();
                fos.write(reportData);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * An event handler for the return to dashboard button that navigates back to the dashboard view
     * @throws IOException : if an I/O error occurs
     */
    public void showDashboardView() {
        try {
            sceneManager.showDashboardView();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}