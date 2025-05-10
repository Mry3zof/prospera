package fcai.prospera.controller;

import fcai.prospera.model.ReportType;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;

public class ReportsController {
    private final ReportGenerationService reportService;
    private final AuthService authService;

    public ReportsController(ReportGenerationService reportService,
                             AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
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