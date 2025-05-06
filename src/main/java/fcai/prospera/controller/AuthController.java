package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.User;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.AuthView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;

public class AuthController {
    private AuthService authService;
    private SceneManager sceneManager;

    @FXML
    public Label error_label;

    @FXML
    public Label username_error;

    @FXML
    public Label email_error;

    @FXML
    public Label password_error;

    @FXML
    public TextField username_field;

    @FXML
    public TextField email_field;

    @FXML
    public TextField password_field;

    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
    }

    public void login(String username, String password) {
        if (!authService.isPasswordCorrect(username, password)) {
            error_label.setText("Password is incorrect");
            return;
        }

        User user = authService.login(username, password);

        if (user == null) {
            error_label.setText("Something went wrong, user not found");
            return;
        }

        try {
            sceneManager.showDashboardView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    public void signup(String name, String email, String username, String password) {
        if (!authService.isUsernameValid(username)) {
            username_error.setText("Username is invalid"); // TODO: specify what is valid
            return;
        }

        if (!authService.isEmailValid(email)) {
            email_error.setText("Email is invalid"); // TODO: specify what is valid
            return;
        }

        if (!authService.isPasswordValid(password)) {
            password_error.setText("Password is invalid"); // TODO: specify what is valid
            return;
        }

        if (authService.doesUsernameExist(username)) {
            username_error.setText("Username already exists");
            return;
        }

        if (authService.doesEmailExist(email)) {
            email_error.setText("Email already exists");
            return;
        }

        authService.signup(name, email, username, password);
        User user = authService.login(username, password); // TODO: handle errors here

        try {
            sceneManager.showDashboardView();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: handle this
        }
    }

    public void showLoginView() {
        try {
            sceneManager.showLoginView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showSignupView() {
        try {
            sceneManager.showSignupview();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
