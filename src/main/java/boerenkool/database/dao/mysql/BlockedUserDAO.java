package boerenkool.database.dao.mysql;

import boerenkool.business.model.BlockedUser;
import boerenkool.business.model.User;

import java.util.List;

public interface BlockedUserDAO {
    // Add a blocked user
    void addBlockedUser(BlockedUser blockedUser);

    // Remove a blocked user
    boolean removeBlockedUser(BlockedUser blockedUser);

    // Check if a user is blocked
    boolean isUserBlocked(User blockedUser, User blockedByUser);

    // Retrieve a list of users blocked by a specific user
    List<User> getBlockedUsers(User blockedByUser);


}
