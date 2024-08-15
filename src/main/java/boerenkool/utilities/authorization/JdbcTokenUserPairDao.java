package boerenkool.utilities.authorization;


import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcTokenUserPairDao implements TokenUserPairDao {

    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;
    private final Logger logger = LoggerFactory.getLogger(JdbcTokenUserPairDao.class);

    @Autowired
    public JdbcTokenUserPairDao(JdbcTemplate jdbcTemplate, UserDAO userDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
        logger.info("New JdbcTokenUserPairDao.");
    }

    @Override
    public void save(TokenUserPair tokenUserPair) {
        jdbcTemplate.update(
                "insert into connection_table (uuid, user_fk) values (?, ?)",
                tokenUserPair.getKey().toString(), tokenUserPair.getUser().getUserId());
    }

    @Override
    public Optional<TokenUserPair> findByKey(UUID key) {
        List<TokenUserPair> tokenUserPairs =
                jdbcTemplate.query(
                        "select * from connection_table where uuid = ?", new ConnectionRowMapper(), key.toString());
        if (tokenUserPairs.size() == 1) {
            return Optional.of(tokenUserPairs.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<TokenUserPair> findByUser(User user) {
        List<TokenUserPair> tokenUserPairs =
                jdbcTemplate.query(
                        "select * from connection_table where member_fk = ?", new ConnectionRowMapper(), user.getUserId());
        if (tokenUserPairs.size() == 1) {
            return Optional.of(tokenUserPairs.get(0));
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID uuid) {
        jdbcTemplate.update("delete from connection_table where uuid = ?", uuid.toString());
        logger.info("Deleted uuid " + uuid.toString());
    }

    private class ConnectionRowMapper implements RowMapper<TokenUserPair> {

        @Override
        public TokenUserPair mapRow(ResultSet resultSet, int i) throws SQLException {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            int userId = resultSet.getInt("user_fk");
            User user = userDAO.getOneById(userId).get();
            return new TokenUserPair(uuid, user);
        }
    }
}