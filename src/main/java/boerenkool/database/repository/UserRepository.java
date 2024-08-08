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

    public void removeOne(int id) {userDAO.removeOne(id);}

    List<User> getAll() {
        return userDAO.getAll();
    };//Read

    Optional getOne(int id) {
        return userDAO.getOne(id);
    }

    boolean updateOne(User user) {
        return userDAO.updateOne(user);
    }

    Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    };
}
