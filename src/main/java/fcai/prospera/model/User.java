package fcai.prospera.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String email;
    private String passwordHash; // TODO: how will this be done

//    public User() {
//        this.id = UUID.randomUUID();
//    } TODO: consider removing this because an email is required to create a UUID that can be used as a primary key

    public User(String username, String email, String passwordHash) {
        this.id = UUID.nameUUIDFromBytes(email.getBytes());
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

//    public void setId(UUID id) {
//        this.id = id;
//    } TODO: check if this is needed

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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