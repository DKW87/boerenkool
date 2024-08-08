package boerenkool.persistence.repository;
import boerenkool.business.model.User;
import boerenkool.persistence.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final UserDAO userDAO;

    @Autowired
    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
        logger.info("New UserRepository");
    }

    public void saveUser(User user) {userDAO.save(user);}

    public void deleteUser(User user) {userDAO.delete(user);}

}
