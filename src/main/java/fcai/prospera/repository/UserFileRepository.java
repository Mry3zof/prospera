package fcai.prospera.repository;

import fcai.prospera.model.User;

import java.util.UUID;

public class UserFileRepository implements UserRepository {
    @Override
    public boolean add(User user) {
        return false;
    }

    @Override
    public User getUserById(UUID userId) {
        return null;
    }

    @Override
    public boolean removeUser(UUID userId) {
        return false;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public void updatePasswordHash(UUID userId, String hash) {

    }
}
