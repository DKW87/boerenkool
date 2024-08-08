package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcMessageDAO implements MessageDAO {
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    public JdbcMessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet resultSet, int rowNumber)
                throws SQLException {
            User fromUser = userDAO.getOne(resultSet.getInt("sender"));
            User toUser = userDAO.getOne(resultSet.getInt("receiver"));
            return new Message(resultSet.getInt("messageId"), // messageId
                    fromUser,
                    toUser,
                    resultSet.getDate("dateTimeSent"),
                    resultSet.getString("subject"),
                    resultSet.getString("body"));
        }
    }

    private PreparedStatement buildInsertMessageStatement(
            Message message, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "Insert into Message(fromUserId, toUserId, dateTimeSent, subject, body) values (?,?,?,?,?);",
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, message.getFromUser().getUserId());
        ps.setString(2, message.getToUser().getUserId());
        ps.setString(3, message.getDateTimeSent().toString());
        ps.setString(4, message.getSubject());
        ps.setString(5, message.getBody());
        return ps;
    }

    private PreparedStatement buildUpdateMessageStatement(
            Message message, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "Insert into Message(fromUserId, toUserId, dateTimeSent, subject, body) values (?,?,?,?,?);",
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, message.getFromUser().getUserId());
        ps.setString(2, message.getToUser().getUserId());
        ps.setString(3, message.getDateTimeSent().toString());
        ps.setString(4, message.getSubject());
        ps.setString(5, message.getBody());
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
    }

    /**
     * read message from database using its unique id
     * @param messageId the unique id for the message
     * @return optional containing the message
     */
    @Override
    public Optional<Message> getOne(int messageId) {
        String sql = "Select * From Message where messageId = ?;";
        List<Message> resultList =
                jdbcTemplate.query(sql, new MessageRowMapper(), messageId);
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
        List<Message> allMessages = jdbcTemplate.query("Select * From Message", new MessageRowMapper());
        return allMessages;
    }

    @Override
    public List<Message> getAllForRecipient(User recipient) {
        int recipientId = recipient.getId();
        List<Message> messagesForRecipient = jdbcTemplate.query(
                "Select * From Message where toUserId = ?;",
                new MessageRowMapper(),
                recipientId);
        return messagesForRecipient;
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
    public boolean removeOne(int messageId) {
        // TODO do we ever remove a message from the database? If so, when?
        //  Or do we just set messages as archived for users, and keep them in database "forever" ?
        // useful when resolving (legal) conflicts?
        return false;
    }
}
