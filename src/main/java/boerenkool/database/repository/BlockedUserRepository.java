package boerenkool.database.repository;

import boerenkool.business.model.BlockedUser;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.BlockedUserDAO;
import boerenkool.database.dao.mysql.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockedUserRepository {

    private final Logger logger = LoggerFactory.getLogger(BlockedUserRepository.class);

    private final BlockedUserDAO blockedUserDAO;
    private final UserDAO userDAO;

    public BlockedUserRepository(BlockedUserDAO blockedUserDAO, UserDAO userDAO) {
        this.blockedUserDAO = blockedUserDAO;
        this.userDAO = userDAO;
        logger.info("New BlockedUserRepository");
    }

    // Voeg een geblokkeerde gebruiker toe
    public void addBlockedUser(BlockedUser blockedUser) {
        blockedUserDAO.addBlockedUser(blockedUser);
    }

    // Verwijder een geblokkeerde gebruiker
    public boolean removeBlockedUser(BlockedUser blockedUser) {
        return blockedUserDAO.removeBlockedUser(blockedUser);
    }

    // Controleer of een gebruiker geblokkeerd is
    public boolean isUserBlocked(User blockedUser, User blockedByUser) {
        return blockedUserDAO.isUserBlocked(blockedUser, blockedByUser);
    }

    // Haal een lijst op van gebruikers die door een specifieke gebruiker zijn geblokkeerd
    public List<User> getBlockedUsers(User blockedByUser) {
        return blockedUserDAO.getBlockedUsers(blockedByUser);
    }
}
