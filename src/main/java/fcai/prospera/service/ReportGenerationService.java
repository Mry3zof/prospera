package fcai.prospera.service;

import fcai.prospera.model.*;
import fcai.prospera.repository.AssetRepository;
import java.util.UUID;

public class ReportGenerationService {
    private final AssetRepository assetRepo;

    public ReportGenerationService(AssetRepository assetRepo) {
        this.assetRepo = assetRepo;
    }

    public Report generateReport(ReportType type, String format) {
        return new PDFReport();
    }
}