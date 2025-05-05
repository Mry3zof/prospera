package fcai.prospera.controller;

import fcai.prospera.model.ReportType;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.ReportsView;

public class ReportsController {
    private final ReportGenerationService reportService;
    private final AuthService authService;
    private final ReportsView reportsView;

    public ReportsController(ReportGenerationService reportService,
                             AuthService authService,
                             ReportsView reportsView) {
        this.reportService = reportService;
        this.authService = authService;
        this.reportsView = reportsView;
    }

    public byte[] generateReport(ReportType type, String format) {
        byte[] report = reportService.generateReport(
                authService.getCurrentUser().getId(),
                type,
                format
        );
        reportsView.displayReport("Report generated");
        return report;
    }

    public boolean exportReport(String format) {
        return reportService.exportReport(
                authService.getCurrentUser().getId(),
                format
        );
    }
}