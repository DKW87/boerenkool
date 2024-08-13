package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.UserDAO;
import boerenkool.database.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcMessageDAO implements MessageDAO {
    private final JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final Logger logger = LoggerFactory.getLogger(JdbcMessageDAO.class);


    @Autowired
    public JdbcMessageDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
    }

    private class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet resultSet, int rowNumber)
                throws SQLException {
            return new Message(resultSet.getInt("messageId"), // messageId
                    userDAO.getOneById(resultSet.getInt("senderId")).orElse(null),
                    userDAO.getOneById(resultSet.getInt("receiverId")).orElse(null),
                    resultSet.getObject("dateTimeSent", OffsetDateTime.class),
                    resultSet.getString("subject"),
                    resultSet.getString("body")
//                    resultSet.getBoolean("readByReceiver"),
//                    resultSet.getBoolean("archivedBySender"),
//                    resultSet.getBoolean("archivedByReceiver")
            );
        }
    }

    /**
     * build PreparedStatement to insert Message data in database,
     * using database time to save date
     * @param message
     * @param connection
     * @return
     * @throws SQLException
     */
    private PreparedStatement buildInsertMessageStatement(
            Message message, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "Insert into Message(senderId, receiverId, dateTimeSent, subject, body) values (?,?, now(),?,?);",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, message.getSender().getUserId());
        ps.setInt(2, message.getReceiver().getUserId());
        ps.setString(3, message.getSubject());
        ps.setString(4, message.getBody());
        return ps;
    }

    private PreparedStatement buildUpdateMessageStatement(
            Message message, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE Message SET " +
                        "archivedBySender = ?," +
                        "readByReceiver = ?," +
                        "archivedByReceiver = ?" +
                        "where messageId = ?;");
        ps.setBoolean(1, message.isArchivedBySender());
        ps.setBoolean(2, message.isReadByReceiver());
        ps.setBoolean(3, message.isArchivedByReceiver());
        return ps;
    }

    /**
     * save a message to the database
     * @param message object to be saved
     */
    @Override
    public void storeOne(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection ->
                buildInsertMessageStatement(message, connection), keyHolder);
        int newKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
        message.setMessageId(newKey);
        message.setDateTimeSent(OffsetDateTime.now());
    }

    /**
     * read message from database using its unique id
     * @param messageId the unique id for the message
     * @return optional containing the message
     */
    @Override
    public Optional<Message> getOneById(int messageId) {
        logger.info("JdbcMessageDAO.getOneById is called");
        String sql = "Select * From Message where messageId = ?;";
        List<Message> resultList =
                jdbcTemplate.query(sql, new MessageRowMapper(), messageId);
        logger.info("JdbcMessageDAO.getOneById after executing query");
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.getFirst());
        }
    }

    /**
     * read all messages from every user database
     * @return the List of all messages
     */
    @Override
    public List<Message> getAll() {
        return jdbcTemplate.query("Select * From Message", new MessageRowMapper());
    }

    @Override
    public List<Message> getAllForReceiver(User receiver) {
        int receiverId = receiver.getUserId();
        List<Message> messagesForReceiver = jdbcTemplate.query(
                "Select * From Message where receiverId = ?;",
                new MessageRowMapper(),
                receiverId);
        return messagesForReceiver;
    }

    /**
     * update existing message in database
     * @param message to be updated
     * @return true when updated (when modified rowcount is not zero)
     */
    @Override
    public boolean updateOne(Message message) {
        return jdbcTemplate.update(connection ->
                buildInsertMessageStatement(message, connection)) != 0;
    }

    /**
     * delete message from database
     * @param messageId the unique id for the message
     * @return
     */
    @Override
    public boolean removeOneById(int messageId) {
        // TODO do we ever remove a message from the database? If so, when?
        //  Or do we just set messages as archived for users, and keep them in database "forever" ?
        //  useful when resolving (legal) conflicts?
        return false;
    }
}
