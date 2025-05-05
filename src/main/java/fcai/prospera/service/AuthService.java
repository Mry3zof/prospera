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

    public void logout(UUID userId) {
    }

    public User getCurrentUser() {
        return null;
    }

    public boolean isPasswordCorrect(String username, String password) {
        return false;
    }

    public boolean isEmailValid(String email) {
        return false;
    }

    public boolean isPasswordValid(String password) {
        return false;
    }

    public boolean isUsernameValid(String username) {
        return false;
    }

    public boolean doesUsernameExist(String username) {
        return false;
    }

    public boolean doesEmailExist(String email) {
        return false;
    }
}