package fcai.prospera.repository;

import fcai.prospera.model.User;

import java.util.UUID;

/**
 * An interface to handle database communication for user data
 */
public interface UserRepository {


    /**
     * adds user to database
     * @param user : user to be added
     * @return true if user was added, false otherwise
     */
    boolean add(User user);

    /**
     * gets user by id
     * @param userId : user id
     * @return user
     */
    User getUserById(UUID userId);

    /**
     * removes user from database
     * @param userId : user id to be removed
     * @return true if user was removed, false otherwise
     */
    boolean removeUser(UUID userId);

    /**
     * gets user by email
     * @param email : user email
     * @return user
     */
    User getUserByEmail(String email);

    /**
     * gets user by username
     * @param username : user username
     * @return user
     */
    User getUserByUsername(String username);

    /**
     * updates user password hash
     * @param userId : user id
     * @param hash : new password hash
     */
    void updatePasswordHash(UUID userId, String hash);
}
