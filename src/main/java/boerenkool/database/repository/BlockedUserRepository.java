package boerenkool.database.repository;

import boerenkool.business.model.BlockedUser;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.BlockedUserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockedUserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final BlockedUserDAO blockedUserDAO;


    public BlockedUserRepository(BlockedUserDAO blockedUserDAO) {
        this.blockedUserDAO = blockedUserDAO;
        logger.info("New BlockedUserRepository");
    }

    // Add a blocked user
    void addBlockedUser(BlockedUser blockedUser) {
        blockedUserDAO.addBlockedUser(blockedUser);
    };

    // Remove a blocked user
    boolean removeBlockedUser(BlockedUser blockedUser) {
        return blockedUserDAO.removeBlockedUser(blockedUser);
    };

    // Check if a user is blocked
    boolean isUserBlocked(User blockedUser, User blockedByUser) {
        return blockedUserDAO.isUserBlocked(blockedUser, blockedByUser);
    };

    // Retrieve a list of users blocked by a specific user
    List<User> getBlockedUsers(User blockedByUser) {
        return blockedUserDAO.getBlockedUsers(blockedByUser);
    };
}
