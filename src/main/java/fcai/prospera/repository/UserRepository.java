package fcai.prospera.repository;

import fcai.prospera.model.User;

import java.util.UUID;


public interface UserRepository {


    boolean add(User user);

    User getUserById(UUID userId);

    boolean removeUser(UUID userId);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    void updatePasswordHash(UUID userId, String hash);

}
