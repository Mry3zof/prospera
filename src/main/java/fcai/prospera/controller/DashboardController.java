package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.service.AuthService;

public class DashboardController {
    private SceneManager sceneManager;
    private AuthService authService;

    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
    }
}
