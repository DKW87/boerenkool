package boerenkool.database.dao.mysql;

import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

/**
 * Interface for User Data Access Object.
 * Provides methods for CRUD operations and other specific user-related queries.
 */
public interface UserDAO extends GenericDAO<User> {

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users.
     */
    @Override
    List<User> getAll(); // Read

    /**
     * Retrieves a user from the database by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an Optional containing the user if found, or an empty Optional if not found.
     */
    @Override
    Optional<User> getOneById(int id);

    /**
     * Stores a user in the database.
     * If the user already exists, it updates the existing user.
     *
     * @param user the user to store.
     * @return
     */
    @Override
    boolean storeOne(User user);

    /**
     * Removes a user from the database by their ID.
     *
     * @param id the ID of the user to remove.
     * @return true if the user was successfully removed, false otherwise.
     */
    @Override
    boolean removeOneById(int id);

    /**
     * Updates an existing user in the database.
     *
     * @param user the user with updated information.
     * @return true if the user was successfully updated, false otherwise.
     */
    @Override
    boolean updateOne(User user); // Update

    /**
     * Retrieves a user from the database by their username.
     *
     * @param username the username of the user to retrieve.
     * @return an Optional containing the user if found, or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    void addBlockedUser(User blockedUser, User user);

    boolean isUserBlocked(User blockedUser, User blockedByUser);

    boolean removeBlockedUser(User blockedUser, User user);

    List<User> getBlockedUsers(User user);

    Optional<User> getSenderByMessageId(int messageId);
    Optional<User> getReceiverByMessageId(int messageId);

    boolean updateBoerenkoolCoins (int userId, int newCoins);
}


