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
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getFirstName());
        if (user.getInfix() != null) {
            ps.setString(5, user.getInfix());
        } else {
            ps.setNull(5, java.sql.Types.VARCHAR);
        }
        ps.setString(6, user.getLastName());
        ps.setInt(7, user.getCoinBalance());
        ps.setString(8, user.getPhone());
        ps.setString(9, user.getEmail());
    }

    private PreparedStatement insertUserStatement(User user, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                "INSERT INTO \"User\"(typeOfUser, username, hashedPassword, firstName, infix, lastName," +
                        " coinBalance, phoneNumber, emailaddress)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
        setCommonParameters(ps, user);
        return ps;
    }

    private PreparedStatement updateUserStatement(User user, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                """
                UPDATE "User" 
                SET 
                typeOfUser=?,
                username=?,
                hashedPassword=?,
                firstName=?,
                infix=?,
                lastName=?,
                coinBalance=?,
                phoneNumber=?,
                emailaddress=?
                WHERE userId=?
                """
        );
        setCommonParameters(ps, user);
        ps.setInt(10, user.getUserId());
        return ps;
    }

    @Override
    public void storeOne(User user) {
        if (user.getUserId() == 0) {
            insert(user);
        } else {
            updateOne(user);
        }
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM \"User\" WHERE userId = ?";
        return jdbcTemplate.update(sql, id) != 0;
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM \"User\"", new UserRowMapper());
    }

    @Override
    public Optional<User> getOneById(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM \"User\" WHERE userId = ?", new UserRowMapper(), id);
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
        return jdbcTemplate.update(connection -> updateUserStatement(user, connection)) != 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query("SELECT * FROM \"User\" WHERE username = ?", new UserRowMapper(), username);
        if (users.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    public RowMapper<User> getUserRowMapper() {
        return new UserRowMapper();
    }

    private static class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("userId");
            String typeOfUser = rs.getString("typeOfUser");
            String username = rs.getString("username");
            String pw = rs.getString("hashedPassword");
            String firstName = rs.getString("firstName");
            String infix = rs.getString("infix");
            String lastName = rs.getString("lastName");
            int coinBalance = rs.getInt("coinBalance");
            String phoneNumber = rs.getString("phoneNumber");
            String email = rs.getString("email");
            User user = new User(typeOfUser, username, pw, email, phoneNumber, firstName, infix, lastName, coinBalance);
            user.setUserId(id);
            return user;
        }
    }
}
