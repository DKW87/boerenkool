package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcMessageDAO implements MessageDAO {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(JdbcMessageDAO.class);


    @Autowired
    public JdbcMessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("new JdbcMessageDAO");
    }

    private class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet resultSet, int rowNumber)
                throws SQLException {
            return new Message(resultSet.getInt("messageId"),
                    null,
                    null,
                    resultSet.getObject("dateTimeSent", LocalDateTime.class),
                    resultSet.getString("subject"),
                    resultSet.getString("body"),
                    resultSet.getBoolean("readByReceiver"),
                    resultSet.getBoolean("archivedBySender"),
                    resultSet.getBoolean("archivedByReceiver")
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
                "Insert into Message(senderId, receiverId, dateTimeSent, subject, body, archivedBySender, " +
                        "readByReceiver, archivedByReceiver) values (?,?,?,?,?,?,?,?);",
                Statement.RETURN_GENERATED_KEYS);
        setCommonParameters(ps, message);
        return ps;
    }

    private PreparedStatement buildUpdateMessageStatement(
            Message message, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE Message SET senderId = ?, receiverId = ?, dateTimeSent = ?, subject = ?, " +
                        "body = ?, archivedBySender = ?, readByReceiver = ?, archivedByReceiver = ?" +
                        "where messageId = ?;");
        setCommonParameters(ps, message);
        ps.setInt(9, message.getMessageId());
        return ps;
    }
    private void setCommonParameters(PreparedStatement ps, Message message) throws SQLException {
//        if (message.getSender().isPresent() {
            ps.setInt(1, message.getSender().get().getUserId());
//        } else ps.set
        ps.setInt(2, message.getReceiver().get().getUserId());
        ps.setObject(3, message.getDateTimeSent());
        ps.setString(4, message.getSubject());
        ps.setString(5, message.getBody());
        ps.setBoolean(6, message.isArchivedBySender());
        ps.setBoolean(7, message.isReadByReceiver());
        ps.setBoolean(8, message.isArchivedByReceiver());
    }

    /**
     * save a message to the database
     * @param message object to be saved
     */
    @Override
    public Optional<Message> storeOne(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection ->
                buildInsertMessageStatement(message, connection), keyHolder);
        int newKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
        message.setMessageId(newKey);
        return Optional.of(message);
    }

    /**
     * read message from database using its unique id
     * @param messageId the unique id for the message
     * @return optional containing the message
     */
    @Override
    public Optional<Message> getOneById(int messageId) {
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
