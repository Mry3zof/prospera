package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashboardController {
    private SceneManager sceneManager;
    private AuthService authService;

    @FXML private Label welcome_label;

    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
        welcome_label.setText("Welcome, " + authService.getCurrentUser().getUsername() + "!");
    }

    public void logout() {
        authService.logout();
        try {
            sceneManager.showLoginView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showZakatView() {
        try {
            sceneManager.showZakatView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
