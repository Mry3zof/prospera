package fcai.prospera.view;

import fcai.prospera.controller.ReportsController;
import fcai.prospera.model.ReportType;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ReportsView {
    private ReportsController controller;

    private ComboBox<ReportType> reportTypeComboBox;
    private Button generateReportButton;
    private TextArea reportOutput;

    public ReportsView(ReportsController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        reportTypeComboBox = new ComboBox<>();
        reportTypeComboBox.getItems().addAll(ReportType.values());

        generateReportButton = new Button("Generate Report");
        reportOutput = new TextArea();
        reportOutput.setEditable(false);

        VBox root = new VBox(10,
                new Label("Select report type:"),
                reportTypeComboBox,
                generateReportButton,
                reportOutput
        );

//        generateReportButton.setOnAction(e -> {
//            ReportType selectedType = reportTypeComboBox.getValue();
//            if (selectedType != null) {
//                controller.handleGenerateReport(selectedType);
//            }
//        });
    }

    public void displayReport(String reportContent) {
        reportOutput.setText(reportContent);
    }

    public void displayError(String error) {
        reportOutput.setText("Error: " + error);
    }
}
