package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
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

/**
 * @author Bart Notelaers
 */
@Repository
public class JdbcMessageDAO implements MessageDAO {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(JdbcMessageDAO.class);


    @Autowired
    public JdbcMessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
                        "body = ?, archivedBySender = ?, readByReceiver = ?, archivedByReceiver = ? " +
                        "where messageId = ?;");
        setCommonParameters(ps, message);
        ps.setInt(9, message.getMessageId());
        return ps;
    }

    private void setCommonParameters(PreparedStatement ps, Message message) throws SQLException {
        ps.setInt(1, message.getSender().getUserId());
        ps.setInt(2, message.getReceiver().getUserId());
        ps.setObject(3, message.getDateTimeSent());
        ps.setString(4, message.getSubject());
        ps.setString(5, message.getBody());
        ps.setBoolean(6, message.getArchivedBySender());
        ps.setBoolean(7, message.getReadByReceiver());
        ps.setBoolean(8, message.getArchivedByReceiver());
    }

    @Override
    public boolean storeOne(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (jdbcTemplate.update(connection ->
                buildInsertMessageStatement(message, connection), keyHolder) != 0) {
            int newKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
            message.setMessageId(newKey);
            return true;
        } else {
            return false;
        }
    }

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

    @Override
    public List<Message> getAll() {
        return jdbcTemplate.query("Select * From Message", new MessageRowMapper());
    }

    @Override
    public List<Message> getAllToReceiverId(int receiverId) {
        return jdbcTemplate.query(
                "Select * From Message where receiverId = ? and archivedByReceiver = false;",
                new MessageRowMapper(),
                receiverId);
    }

    @Override
    public List<Message> getAllFromSenderId(int senderId) {
        return jdbcTemplate.query(
                "Select * From Message where senderId = ?;",
                new MessageRowMapper(),
                senderId);
    }

    public List<Message> getAllByUserId(int userId) {
        return jdbcTemplate.query(
                "Select * From Message where ? IN (senderId, receiverId);",
                new MessageRowMapper(),
                userId);
    }

    public int numberOfUnreadMessages(int receiverId) {
        String sql = "SELECT SUM(!readByReceiver) as number FROM Message WHERE receiverId = ?;";
        List<Integer> result = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("number"), receiverId);
        System.out.println("resultaat query numberOfUnreadMessages; " + result.getFirst());
        return result.getFirst();
    }

    /**
     * update existing message in database
     * @param message to be updated
     * @return true when updated (when modified rowcount is not zero)
     */
    @Override
    public boolean updateOne(Message message) {
        int success = jdbcTemplate.update(connection ->
                buildUpdateMessageStatement(message, connection));
        return (success == 1);
    }

    @Override
    public boolean setReadByReceiver(Message message) {
        return (jdbcTemplate.update(
                "UPDATE Message SET readByReceiver = TRUE WHERE messageId = ?;",
                message.getMessageId()) != 0);
    }

    public boolean archiveMessageForSender(Message message) {
        return (jdbcTemplate.update(
                "UPDATE Message SET archivedBySender = TRUE WHERE messageId = ?;",
                message.getMessageId()) != 0);
    }

    public boolean archiveMessageForReceiver(Message message) {
        return (jdbcTemplate.update(
                "UPDATE Message SET archivedByReceiver = TRUE WHERE messageId = ?;",
                message.getMessageId()) != 0);
    }

    @Override
    public boolean removeOneById(int messageId) {
        String sql = "DELETE FROM `Message` WHERE messageId = ?";
        return jdbcTemplate.update(sql, messageId) != 0;
    }
}
