package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * A controller for the dashboard view
 */
public class DashboardController {
    private SceneManager sceneManager;
    private AuthService authService;

    @FXML private Label welcome_label;

    /**
     * Initializes the dashboard controller
     * @param sceneManager : the scene manager
     * @param authService : the authentication service
     */
    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
        welcome_label.setText("Welcome, " + authService.getCurrentUser().getUsername() + "!");
    }

    /**
     * An event handler for the logout button, logs user out
     */
    public void logout() {
        authService.logout();
        try {
            sceneManager.showLoginView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An event handler for the zakat button, switches to the zakat view
     */
    public void showZakatView() {
        try {
            sceneManager.showZakatView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An event handler for the assets button, switches to the assets view
     */
    public void showAssetsView() {
        try {
            sceneManager.showAssetsView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showReportsView() {
        try {
            sceneManager.showReportsView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
