package fcai.prospera.model;

/**
 * Interface for generating reports
 */
public interface Report {
    void generateData(ReportData reportData);
    byte[] getData();
}