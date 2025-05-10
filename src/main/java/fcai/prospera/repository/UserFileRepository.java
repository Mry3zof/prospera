package fcai.prospera.repository;

import fcai.prospera.model.User;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * An implementation of UserRepository to handle file database communication for user data
 */
public class UserFileRepository implements UserRepository {

    private final String USERS_FILE_PATH = "data/users.ser";
    private final File usersStorage;
    private HashMap<UUID, User> users;

    public UserFileRepository() {
        usersStorage = new File(USERS_FILE_PATH);
        users = new HashMap<>();
        loadUsers();
    }

    /**
     * Loads users from file
     */
    private void loadUsers() {
        if (!usersStorage.exists() || usersStorage.length() == 0) {
            return;
        }

        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(usersStorage))) {
            users = (HashMap<UUID, User>) objectIn.readObject();

            // TODO: Remove
            System.out.println("USERS LOADED FROM: " + USERS_FILE_PATH + ":");
            for (User user : users.values()) {
                System.out.println(user.toString());
            }
        }
        catch (IOException | ClassNotFoundException exception) {
            System.err.println("Error loading users from " + USERS_FILE_PATH + ": " + exception.getMessage());
        }
    }

    /**
     * Saves users to file
     */
    private void saveUsers() {
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(usersStorage))) {
            objectOut.writeObject(users);

            // TODO: Remove
            System.out.println("USERS SAVED TO: " + USERS_FILE_PATH + ":");
            for (User user : users.values()) {
                System.out.println(user.toString());
            }
        }
        catch (IOException exception) {
            System.err.println("Error saving users in " + USERS_FILE_PATH + ": " + exception.getMessage());
        }
    }

    @Override
    public boolean add(User user) {
        if (user == null || users.containsKey(user.getId())) {
            return false;
        }

        users.put(user.getId(), user);
        saveUsers();
        return true;
    }

    @Override
    public User getUserById(UUID userId) {
        return (userId != null ? users.get(userId) : null);
    }

    @Override
    public boolean removeUser(UUID userId) {
        User removedUser = users.remove(userId);
        if (removedUser == null) {
            return false;
        }

        saveUsers();
        return true;
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }

        String cleanEmail = email.toLowerCase().trim();
        for (User user : users.values()) {
            String currentEmail = user.getEmail();
            if (currentEmail != null && currentEmail.equals(cleanEmail)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        String cleanUsername = username.toLowerCase().trim();
        for (User user : users.values()) {
            String currentUsername = user.getUsername();
            if (currentUsername != null && currentUsername.equals(cleanUsername)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public void updatePasswordHash(UUID userId, String hash) {
        if (userId == null || hash == null) {
            return;
        }

        User user = getUserById(userId);
        if (user == null) {
            return;
        }

        user.setPasswordHash(hash);
        saveUsers();
    }
}
