package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.User;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.AuthView;
import java.util.Map;

public class AuthController {
    private AuthService authService;
    private SceneManager sceneManager;

    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
    }

    public void login(String username, String password) {

    }

    public void signup(String name, String email, String username, String password) {

    }
}
