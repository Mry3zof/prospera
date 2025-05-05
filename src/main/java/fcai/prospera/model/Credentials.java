package fcai.prospera.model;

import java.util.UUID;

public class Credentials {
    private UUID id;
    private UUID userId;
    private CredentialType type;
    private String encryptedCredentials; // This would be handled by a proper encryption service
    private String name;

    // Constructors
    public Credentials() {
        this.id = UUID.randomUUID();
    }

    public Credentials(UUID userId, CredentialType type, String encryptedCredentials, String name) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.type = type;
        this.encryptedCredentials = encryptedCredentials;
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CredentialType getType() {
        return type;
    }

    public void setType(CredentialType type) {
        this.type = type;
    }

    public String getEncryptedCredentials() {
        return encryptedCredentials;
    }

    public void setEncryptedCredentials(String encryptedCredentials) {
        this.encryptedCredentials = encryptedCredentials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}