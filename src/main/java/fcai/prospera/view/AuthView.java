package fcai.prospera.view;

import fcai.prospera.controller.AuthController;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Map;

public class AuthView {
    private AuthController controller;

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;

    public AuthView(AuthController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        usernameField = new TextField();
        passwordField = new PasswordField();
        loginButton = new Button("Login");
        statusLabel = new Label();


        VBox root = new VBox(10, usernameField, passwordField, loginButton, statusLabel);


        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            controller.handleLogin(username, password);
        });
    }

    public void onLoginSucceeded() {
        statusLabel.setText("Login successful!");
    }

    public void onLoginFailed(String error) {
        statusLabel.setText("Error: " + error);
    }

    public void onSignupSucceeded() {
        statusLabel.setText("Signup successful!");
    }

    public void onSignupFailed(Map<String, String> errors) {
        statusLabel.setText("Signup failed: " + errors.toString());
    }
}
