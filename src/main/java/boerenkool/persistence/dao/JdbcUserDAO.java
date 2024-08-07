package boerenkool.persistence.dao;

import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcUserDAO  implements UserDAO{

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
                "insert into user(typeOfUser, username, hashedPassword, firstName, infix, lastName," +
                        " coinBalance, phoneNumber, emailaddress)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
       setCommonParameters(ps, user);
        return ps;
    }

    private PreparedStatement updateUserStatement(User user, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                """
        update user 
            set 
            typeOfUser=?,
            username=?,
            hashedPassword=?,
            firstName=?,
            infix=?,
            lastName=?,
            coinBalance=?,
            phoneNumber=?,
            emailaddress=?,
            where userId=?,
            """
        );
        setCommonParameters(ps, user);
        ps.setInt(10, user.getUserId());
        return ps;
    }



    @Override
    public void save(User user) {
        if(user.getUserId() == 0) {
            insert(user);
        } else {
            update(user);
        }
    }

    @Override
    public void delete(User user) {
        String sql  = "DELETE FROM user WHERE userId = ?";
        jdbcTemplate.update(sql, user.getUserId());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    private void insert(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> updateUserStatement(user, connection));
        int newKey = keyHolder.getKey().intValue();
        user.setUserId(newKey);
    }

    private void update(User user) {
        jdbcTemplate.update(connection -> updateUserStatement(user, connection));
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }
}
