package boerenkool.database.dao.mysql;

import boerenkool.business.model.BlockedUsers;
import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;

import java.util.List;

public interface BlockedUsersDAO {
    // Add a blocked user
    void addBlockedUser(BlockedUsers blockedUsers);

    // Remove a blocked user
    void removeBlockedUser(BlockedUsers blockedUsers);

    // Check if a user is blocked
    boolean isUserBlocked(User blockedUser, User blockedByUser);

    // Retrieve a list of users blocked by a specific user
    List<User> getBlockedUsers(User blockedByUser);


}
