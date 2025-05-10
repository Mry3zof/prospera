package fcai.prospera;

import fcai.prospera.repository.AssetFileRepository;
import fcai.prospera.repository.UserFileRepository;
import fcai.prospera.service.AssetService;
import fcai.prospera.service.AuthService;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.ZakatAndComplianceService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application entry point
 */
public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // repositories
        AssetFileRepository assetRepo = new AssetFileRepository();
        UserFileRepository userRepo = new UserFileRepository();

        // services
        AuthService authService = new AuthService(userRepo);
        AssetService assetService = new AssetService(assetRepo);
        ReportGenerationService reportService = new ReportGenerationService(assetRepo);
        ZakatAndComplianceService zakatService = new ZakatAndComplianceService(assetRepo);

        SceneManager sceneManager = new SceneManager(stage, authService, assetService, reportService, zakatService);

        sceneManager.showAuthView();
        stage.setTitle("Prospera");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}