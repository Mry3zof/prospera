package fcai.prospera.controller;

import fcai.prospera.service.AuthService;
import fcai.prospera.view.AuthView;
import java.util.Map;

public class AuthController {
    private final AuthService authService;
    private final AuthView authView;

    public AuthController(AuthService authService, AuthView authView) {
        this.authService = authService;
        this.authView = authView;
    }

    public void login(String username, String password) {
        boolean success = authService.authenticate(username, password);
        if (success) authView.onLoginSucceeded();
        else authView.onLoginFailed("Invalid credentials");
    }

    public void signup(String name, String email, String username, String password) {
        Map<String, String> errors = authService.registerUser(name, email, username, password);
        if (errors.isEmpty()) authView.onSignupSucceeded();
        else authView.onSignupFailed(errors);
    }
}
