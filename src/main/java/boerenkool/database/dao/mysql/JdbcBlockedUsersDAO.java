package boerenkool.database.dao.mysql;

import boerenkool.business.model.BlockedUsers;
import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JdbcBlockedUsersDAO is a DAO class responsible for managing blocked users
 * in the database using JDBC.
 */
@Repository
public class JdbcBlockedUsersDAO implements BlockedUsersDAO {

    private final Logger logger = LoggerFactory.getLogger(JdbcBlockedUsersDAO.class);

    private final JdbcTemplate jdbcTemplate;
    private final JdbcUserDAO jdbcUserDAO;

    /**
     * Constructs a new JdbcBlockedUsersDAO with the given JdbcTemplate and JdbcUserDAO.
     *
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @param jdbcUserDAO  the JdbcUserDAO to use for user-related operations
     */
    @Autowired
    public JdbcBlockedUsersDAO(JdbcTemplate jdbcTemplate, JdbcUserDAO jdbcUserDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcUserDAO = jdbcUserDAO;
        logger.info("JdbcBlockedUsersDAO instantiated");
    }

    /**
     * Adds a blocked user relationship to the database.
     *
     * @param blockedUsers the blocked user relationship to add
     */
    @Override
    public void addBlockedUser(BlockedUsers blockedUsers) {
        String sql = "INSERT INTO BlockedList (blockedUser, userId) VALUES (?, ?)";
        jdbcTemplate.update(sql, blockedUsers.getBlockedUser().getUserId(), blockedUsers.getBlockedByUser().getUserId());
    }

    /**
     * Removes a blocked user relationship from the database.
     *
     * @param blockedUsers the blocked user relationship to remove
     */
    @Override
    public void removeBlockedUser(BlockedUsers blockedUsers) {
        String sql = "DELETE FROM BlockedList WHERE blockedUser = ? AND userId = ?";
        jdbcTemplate.update(sql, blockedUsers.getBlockedUser().getUserId(), blockedUsers.getBlockedByUser().getUserId());
    }

    /**
     * Checks if a user is blocked by another user.
     *
     * @param blockedUser   the user who is potentially blocked
     * @param blockedByUser the user who might have blocked the other user
     * @return true if the user is blocked, false otherwise
     */
    @Override
    public boolean isUserBlocked(User blockedUser, User blockedByUser) {
        String sql = "SELECT COUNT(*) FROM BlockedList WHERE blockedUser = ? AND userId = ?";
        // If the query returns a numeric value, JdbcTemplate will convert it to an Integer object.
        int count = jdbcTemplate.queryForObject(sql, Integer.class, blockedUser.getUserId(), blockedByUser.getUserId());
        return count > 0;
    }

    /**
     * Retrieves a list of users blocked by a specific user.
     *
     * @param blockedByUser the user who has blocked others
     * @return a list of users blocked by the given user
     */
    @Override
    public List<User> getBlockedUsers(User blockedByUser) {
        String sql = "SELECT u.* FROM users u INNER JOIN BlockedList b ON u.userId = b.blockedUser WHERE b.userId = ?";
        return jdbcTemplate.query(sql, jdbcUserDAO.getUserRowMapper(), blockedByUser.getUserId());
    }
}
