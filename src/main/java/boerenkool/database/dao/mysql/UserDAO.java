package boerenkool.database.dao.mysql;

import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface UserDAO extends GenericDAO<User> {

    @Override
    List<User> getAll(); // Read

    @Override
    Optional<User> getOneById(int id);

    @Override
    boolean storeOne(User user);


    @Override
    boolean removeOneById(int id);

    @Override
    boolean updateOne(User user); // Update

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    void addBlockedUser(User blockedUser, User user);

    boolean isUserBlocked(User blockedUser, User blockedByUser);

    boolean removeBlockedUser(User blockedUser, User user);

    List<User> getBlockedUsers(User user);

    Optional<User> getSenderByMessageId(int messageId);
    Optional<User> getReceiverByMessageId(int messageId);

    boolean updateBoerenkoolCoins (int userId, int newCoins);

    Optional<String> getUsernameById(int userId);

    Optional<Map<Integer, String>> getMapOfCorrespondents(int userId);

}


