package fcai.prospera.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * A class to model user data
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String email;
    private String passwordHash;

    public User(String username, String email, String passwordHash) {
        this.id = UUID.nameUUIDFromBytes(email.getBytes());
        this.username = username.toLowerCase().trim();
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase().trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase().trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}