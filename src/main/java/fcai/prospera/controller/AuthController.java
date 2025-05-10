package fcai.prospera.controller;

import fcai.prospera.SceneManager;
import fcai.prospera.model.User;
import fcai.prospera.service.AuthService;
import fcai.prospera.view.AuthView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
    public PasswordField password_field;

    public void init(SceneManager sceneManager, AuthService authService) {
        this.sceneManager = sceneManager;
        this.authService = authService;
    }

    private void login(String username, String password) {
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
            throw new RuntimeException(e);
        }
    }

    public void login(){
        String username = username_field.getText();
        String password = password_field.getText();
        login(username, password);
    }

    private void signup(String name, String email, String username, String password) {
        boolean error = false;

        if (!authService.isUsernameValid(username)) {
            username_error.setText("Your username should be between 3-20 characters, no spaces or special characters");
            error = true;
        }else{
            username_error.setText("");
        }

        if (!authService.isEmailValid(email)) {
            email_error.setText("Your Email should look something like: example@Email.com");
            error = true;
        }else{
            email_error.setText("");
        }

        if (!authService.isPasswordValid(password)) {
            password_error.setText("Password should be >= 8 characters with at least one digit");
            error = true;
        }else{
            password_error.setText("");
        }

        if (authService.doesUsernameExist(username)) {
            username_error.setText("Username already exists");
            error = true;
        }else{
            username_error.setText("");
        }

        if (authService.doesEmailExist(email)) {
            email_error.setText("Email already exists");
            error = true;
        }else{
            email_error.setText("");
        }

        if (error) {
            return;
        }

        authService.signup(name, email, username, password);
        User user = authService.login(username, password);

        try {
            sceneManager.showDashboardView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void signup(){
        String username = username_field.getText() ;
        String email = email_field.getText() ;
        String password = password_field.getText() ;
        signup(username, email, username, password);
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
