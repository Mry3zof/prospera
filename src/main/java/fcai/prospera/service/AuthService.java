package fcai.prospera.service;

import fcai.prospera.model.User;
import fcai.prospera.repository.UserRepository;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepo;
    private User currentUser;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) {
        return null;
    }

    public void signup(String name, String email, String username, String password) {
    }

    public void logout() {
    }

    public User getCurrentUser() {
        return null;
    }

    public boolean isPasswordCorrect(String username, String password) {
        return false;
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,6}$";

        if (email == null) {
            return false;
        }

        return email.matches(emailRegex);
    }

    public boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (password == null) {
            return false;
        }

        return password.matches(passwordRegex);
    }

    public boolean isUsernameValid(String username) {
        String usernameRegex = "^[a-zA-Z0-9_-]{3,20}$";

        if (username == null) {
            return false;
        }

        return username.matches(usernameRegex);
    }

    public boolean doesUsernameExist(String username) {
        return false;
    }

    public boolean doesEmailExist(String email) {
        return false;
    }
}