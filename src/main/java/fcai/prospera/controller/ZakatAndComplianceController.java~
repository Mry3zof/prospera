package fcai.prospera.controller;

import fcai.prospera.model.*;
import fcai.prospera.service.ZakatAndComplianceService;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.ZakatAndComplianceView;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ZakatAndComplianceController {
    private final ZakatAndComplianceService zakatService;
    private final ReportGenerationService reportService;
    private final AuthService authService;
    private final ZakatAndComplianceView zakatView;

    public ZakatAndComplianceController(ZakatAndComplianceService zakatService,
                                        ReportGenerationService reportService,
                                        AuthService authService,
                                        ZakatAndComplianceView zakatView) {
        this.zakatService = zakatService;
        this.reportService = reportService;
        this.authService = authService;
        this.zakatView = zakatView;
    }

    public BigDecimal calculateZakat(UUID userId, Date dueDate) {
        BigDecimal amount = zakatService.calculateZakat(userId, dueDate);
        zakatView.displayZakatResult(amount.doubleValue());
        return amount;
    }

    public ComplianceStatus getComplianceStatus(UUID userId) {
        ComplianceStatus status = zakatService.checkCompliance(userId);
        zakatView.displayComplianceStatus(status.isCompliant());
        return status;
    }

    public Report generateZakatReport(String format) {
        return reportService.generateZakatReport(authService.getCurrentUser().getId(), format);
    }
}