package boerenkool.database.dao.mysql;

import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserDAO implements UserDAO {

    private final Logger logger = LoggerFactory.getLogger(JdbcUserDAO.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcUserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("JdbcUserDAO instantiated");
    }

    private void setCommonParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getTypeOfUser());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getHashedPassword());
        ps.setString(4, user.getSalt()); // Ensure salt is set here
        ps.setString(5, user.getFirstName());
        if (user.getInfix() != null) {
            ps.setString(6, user.getInfix());
        } else {
            ps.setNull(6, java.sql.Types.VARCHAR);
        }
        ps.setString(7, user.getLastName());
        ps.setInt(8, user.getCoinBalance());
        ps.setString(9, user.getPhone());
        ps.setString(10, user.getEmail());
    }



    private PreparedStatement insertUserStatement(User user, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                "INSERT INTO `User`(typeOfUser, username, hashedPassword, salt, firstName, infix, lastName," +
                        " coinBalance, phoneNumber, emailaddress)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        setCommonParameters(ps, user);
        return ps;
    }



    private PreparedStatement updateUserStatement(User user, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE `User` " +
                        "SET typeOfUser=?, username=?, hashedPassword=?, salt=?, firstName=?, infix=?, lastName=?," +
                        " coinBalance=?, phoneNumber=?, emailaddress=?" +
                        " WHERE userId=?");

        setCommonParameters(ps, user);
        ps.setInt(11, user.getUserId()); // Zorg ervoor dat de userId correct wordt ingesteld als de laatste parameter.
        return ps;
    }


    @Override
    public boolean storeOne(User user) {
        if (user.getUserId() == 0) {
            insert(user);
            return user.getUserId() > 0;
        } else {
            return updateOne(user);
        }
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM `User` WHERE userId = ?";
        return jdbcTemplate.update(sql, id) != 0;
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM `User`", new UserRowMapper());
    }

    @Override
    public Optional<User> getOneById(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE userId = ?", new UserRowMapper(), id);
        if (users.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    private void insert(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertUserStatement(user, connection), keyHolder);
        int newKey = keyHolder.getKey().intValue();
        user.setUserId(newKey);
    }

    @Override
    public boolean updateOne(User user) {
        logger.info("Attempting to update user with ID: {}", user.getUserId());
        logger.info("Hashed password to be saved: {}", user.getHashedPassword());
        logger.info("Salt to be saved: {}", user.getSalt());

        boolean isUpdated = jdbcTemplate.update(connection -> updateUserStatement(user, connection)) != 0;

        if (isUpdated) {
            logger.info("User with ID {} successfully updated.", user.getUserId());
        } else {
            logger.warn("No user was updated for ID {}. Check if the user ID exists.", user.getUserId());
        }
        return isUpdated;
    }


    @Override
    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE username = ?", new UserRowMapper(), username);
        if (users.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE emailaddress = ?", new UserRowMapper(), email);
        if (users.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    @Override
    public void addBlockedUser(User blockedUser, User user) {
        String sql = "INSERT INTO BlockedList (blockedUser, userId) VALUES (?, ?)";
        jdbcTemplate.update(sql, blockedUser.getUserId(), user.getUserId());
    }

    @Override
    public boolean removeBlockedUser(User blockedUser, User user) {
        String sql = "DELETE FROM BlockedList WHERE blockedUser = ? AND userId = ?";
        return jdbcTemplate.update(sql, blockedUser.getUserId(), user.getUserId()) != 0;
    }

    @Override
    public boolean isUserBlocked(User blockedUser, User blockedByUser) {
        String sql = "SELECT COUNT(*) FROM BlockedList WHERE blockedUser = ? AND userId = ?";
        // If the query returns a numeric value, JdbcTemplate will convert it to an Integer object.
        int count = jdbcTemplate.queryForObject(sql, Integer.class, blockedUser.getUserId(), blockedByUser.getUserId());
        return count > 0;
    }

    @Override
    public List<User> getBlockedUsers(User user) {
        String sql = "SELECT u.* FROM `User` u INNER JOIN BlockedList b ON u.userId = b.blockedUser WHERE b.userId = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), user.getUserId());
    }

    @Override
    public Optional<User> getSenderByMessageId(int messageId) {
        List<User> users = jdbcTemplate.query(
                "SELECT User.*, Message.receiverId, Message.senderId FROM `User` JOIN `Message` ON userId = senderId WHERE messageId = ? LIMIT 1;",
                new JdbcUserDAO.UserRowMapper(),
                messageId
        );
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }
    @Override
    public Optional<User> getReceiverByMessageId(int messageId) {
        List<User> users = jdbcTemplate.query(
                "SELECT User.*, Message.receiverId, Message.senderId FROM `User` JOIN `Message` ON userId = receiverId WHERE messageId = ? LIMIT 1;",
                new JdbcUserDAO.UserRowMapper(),
                messageId
        );

        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("userId");
            String typeOfUser = rs.getString("typeOfUser");
            String username = rs.getString("username");
            String hashedPassword = rs.getString("hashedPassword");
            String salt = rs.getString("salt");  // Retrieve the salt from the ResultSet
            String firstName = rs.getString("firstName");
            String infix = rs.getString("infix");
            String lastName = rs.getString("lastName");
            int coinBalance = rs.getInt("COINBALANCE");
            String phoneNumber = rs.getString("phoneNumber");
            String email = rs.getString("emailaddress");

            // Pass salt to the User constructor
            User user = new User(typeOfUser, username, hashedPassword, salt, email, phoneNumber, firstName, infix, lastName, coinBalance);
            user.setUserId(id);
            return user;
        }
    }

}
