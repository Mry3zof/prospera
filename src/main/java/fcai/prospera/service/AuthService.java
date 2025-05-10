package fcai.prospera.service;

import fcai.prospera.model.User;
import fcai.prospera.repository.UserRepository;
import java.util.UUID;

/**
 * Handles authentication logic
 */
public class AuthService {
    private final UserRepository userRepo;
    private User currentUser;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * logs in user and sets current user
     * @param username : entered username
     * @param password : enetered password
     * @return the logged in user
     */
    public User login(String username, String password) {
        this.currentUser = userRepo.getUserByUsername(username);
        return currentUser;
    }

    /**
     * signs up user
     * @param name : entered name
     * @param email : entered email
     * @param username : entered username
     * @param password : entered password
     */
    public void signup(String name, String email, String username, String password) {
        userRepo.add(new User(username, email, password));
    }

    /**
     * logs out current user
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * gets currently logged in user
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if password matches the username
     * @param username : entered username
     * @param password : entered password
     * @return true if the password matches, false otherwise
     */
    public boolean isPasswordCorrect(String username, String password) {
        User user = userRepo.getUserByUsername(username);
        return user != null && user.getPasswordHash().equals(password);
    }

    /**
     * Checks whether or not the email is valid
     * @param email : entered email
     * @return true if the email is valid, false otherwise
     */
    public boolean isEmailValid(String email) {
        String emailRegex = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,6}$";

        if (email == null) {
            return false;
        }

        return email.matches(emailRegex);
    }

    /**
     * Checks whether or not the password is valid
     * @param password : entered password
     * @return true if the password is valid, false otherwise
     */
    public boolean isPasswordValid(String password) {
        String passwordRegex = "^(?=.*\\d).{8,}$";

        if (password == null) {
            return false;
        }

        return password.matches(passwordRegex);
    }

    /**
     * Checks whether or not the username is valid
     * @param username : entered username
     * @return true if the username is valid, false otherwise
     */
    public boolean isUsernameValid(String username) {
        String usernameRegex = "^[a-zA-Z0-9_-]{3,20}$";

        if (username == null) {
            return false;
        }

        return username.matches(usernameRegex);
    }

    /**
     * Checks whether or not an account with the username already exists
     * @param username : entered username
     * @return true if the username already exists, false otherwise
     */
    public boolean doesUsernameExist(String username) {
        return userRepo.getUserByUsername(username) != null;

    }

    /**
     * Checks whether or not an account with the email already exists
     * @param email : entered email
     * @return true if the email already exists, false otherwise
     */
    public boolean doesEmailExist(String email) {
        return userRepo.getUserByEmail(email) != null;
    }
}