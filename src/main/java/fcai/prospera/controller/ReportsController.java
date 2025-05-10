package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.ReportType;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;
import javafx.fxml.FXML;

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

    public void showDashboardView() {
        try {
            sceneManager.showDashboardView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public byte[] generateReport(ReportType type, String format) {
//        byte[] report = reportService.generateReport(
//                authService.getCurrentUser().getId(),
//                type,
//                format
//        );
//        reportsView.displayReport("Report generated");
//        return report;
//    }
//
//    public boolean exportReport(String format) {
//        return reportService.exportReport(
//                authService.getCurrentUser().getId(),
//                format
//        );
//    }
}