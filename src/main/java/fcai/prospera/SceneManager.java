package fcai.prospera;

import fcai.prospera.controller.AssetController;
import fcai.prospera.controller.AuthController;
import fcai.prospera.controller.DashboardController;
import fcai.prospera.controller.ZakatAndComplianceController;
import fcai.prospera.controller.ReportsController;
// import fcai.prospera.repository.AssetRepository; // Not used directly here
import fcai.prospera.service.AssetService;
import fcai.prospera.service.AuthService;
import fcai.prospera.service.ReportGenerationService;
import fcai.prospera.service.ZakatAndComplianceService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * A class to manage switching stages between different views
 */
public class SceneManager {
    private final Stage stage;

    private final AuthService authService;
    private final AssetService assetService;
    private final ReportGenerationService reportService;
    private final ZakatAndComplianceService zakatService;

    public SceneManager(Stage stage, AuthService authService, AssetService assetService,
                        ReportGenerationService reportService, ZakatAndComplianceService zakatService) {
        this.stage = stage;
        this.authService = authService;
        this.assetService = assetService;
        this.reportService = reportService;
        this.zakatService = zakatService;

        // Initialize assetViewInstance here if you have a concrete implementation
        // e.g., this.assetViewInstance = new DefaultAssetView();
        // If AssetView is just for simple alerts, AssetController can handle it being null.
    }

    /**
     * Switches to a new scene using the provided FXML file and controller injector.
     * @param fxmlFile : the FXML file to load
     * @param controllerInjector : the controller injector
     * @throws IOException : if an I/O error occurs
     */
    private void switchScene(String fxmlFile, Consumer<Object> controllerInjector) throws IOException {
        // Assuming fxmlFile is just the filename e.g. "assets.fxml"
        // and FXML files are in the same package as SceneManager or a subpackage.
        // For fcai.prospera.assets.fxml, path would be "assets.fxml"
        // If in fcai.prospera.fxml.assets.fxml, path would be "fxml/assets.fxml"
        String fxmlResourcePath = fxmlFile; // Adjust if FXMLs are in a subfolder like "fxml/"

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResourcePath));

        if (loader.getLocation() == null) {
            // Try classpath root if not found relative to class
            loader.setLocation(getClass().getResource("/" + fxmlResourcePath));
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML file: " + fxmlResourcePath +
                        ". Searched relative to " + getClass().getPackage().getName() + " and at classpath root.");
            }
        }

        Parent root = loader.load();
        Object controller = loader.getController();
        if (controller == null) {
            throw new IOException("Controller not found for FXML: " + fxmlResourcePath +
                    ". Check fx:controller in FXML and controller class existence.");
        }
        controllerInjector.accept(controller);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }
        stage.sizeToScene(); // Optional: Adjust stage size to scene
        // stage.centerOnScreen(); // Optional
    }

    /**
     * Switches to the authentication view.
     * @throws IOException : if an I/O error occurs
     */
    public void showAuthView() throws IOException {
        switchScene("auth.fxml", controller -> {
            ((AuthController) controller).init(this, authService);
        });
    }

    /**
     * Switches to the login view.
     * @throws IOException : if an I/O error occurs
     */
    public void showLoginView() throws IOException {
        switchScene("auth-login.fxml", controller -> {
            ((AuthController) controller).init(this, authService);
        });
    }

    /**
     * Switches to the signup view.
     * @throws IOException : if an I/O error occurs
     */
    public void showSignupview() throws IOException {
        switchScene("auth-signup.fxml", controller -> {
            ((AuthController) controller).init(this, authService);
        });
    }

    /**
     * Switches to the dashboard view.
     * @throws IOException : if an I/O error occurs
     */
    public void showDashboardView() throws IOException {
        switchScene("dashboard.fxml", controller -> { // Assuming dashboard.fxml is the name
            ((DashboardController) controller).init(this, authService);
        });
    }

    /**
     * Switches to the zakat view.
     * @throws IOException : if an I/O error occurs
     */
    public void showZakatView() throws IOException {
        switchScene("zakat.fxml", controller -> {
            ((ZakatAndComplianceController) controller).init(this, authService, zakatService, assetService, "MAIN");
        });
    }

    /**
     * Switches to the assets view.
     * @throws IOException : if an I/O error occurs
     */
    public void showAssetsView() throws IOException {
        switchScene("assets.fxml", controller -> {
            ((AssetController) controller).init(this, authService, assetService);
        });
    }

    /**
     * Switches to the zakat choose assets view.
     * @throws IOException : if an I/O error occurs
     */
    public void showZakatChooseAssetsView() throws IOException {
        switchScene("zakat-select-assets.fxml", controller -> {
            ((ZakatAndComplianceController) controller).init(this, authService, zakatService, assetService, "SELECTION");
        });
    }

    /**
     * Switches to the zakat result view.
     * @throws IOException : if an I/O error occurs
     */
    public void showZakatResultView() throws IOException {
        switchScene("zakat-result.fxml", controller -> {
            ((ZakatAndComplianceController) controller).init(this, authService, zakatService, assetService, "RESULTS");
        });
    }

    public void showReportsView() throws IOException {
        switchScene("reports.fxml", controller -> {
            ((ReportsController) controller).init(this, authService, reportService);
        });
    }

    public Window getStage() {
        return stage;
    }
}