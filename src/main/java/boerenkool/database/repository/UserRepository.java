package boerenkool.database.repository;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final UserDAO userDAO;

    @Autowired
    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
        logger.info("New UserRepository");
    }

    public void storeOne(User user) {userDAO.storeOne(user);}

    public boolean removeOneById(int id) { return userDAO.removeOneById(id);}

    public List<User> getAll() {
        return userDAO.getAll();
    };//Read

    public Optional<User> getOneById(int id) {
        return userDAO.getOneById(id);
    }

    public boolean updateOne(User user) {
        return userDAO.updateOne(user);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public List<User> getBlockedUsers(User user) {
        return userDAO.getBlockedUsers(user);
    }

    public boolean isUserBlocked(User blockedUser, User blockedByUser) {
        return userDAO.isUserBlocked(blockedUser, blockedByUser);
    }

    public boolean removeBlockedUser(User blockedUser, User user) {
        return userDAO.removeBlockedUser(blockedUser, user);
    }

    public void addBlockedUser(User blockedUser, User user) {
        userDAO.addBlockedUser(blockedUser, user);
    }


    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public Optional<User> getSenderByMessageId(int messageId) {
        return userDAO.getSenderByMessageId(messageId);
    }

    public Optional<User> getReceiverByMessageId(int messageId) {
        return userDAO.getReceiverByMessageId(messageId);
    }
}
