package boerenkool.database.repository;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.database.dao.mysql.JdbcUserDAO;
import boerenkool.database.dao.mysql.MessageDAO;
import boerenkool.database.dao.mysql.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageRepository {
    private static Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    @Autowired
    public MessageRepository(MessageDAO messageDAO, UserDAO userDAO) {
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
        logger.info("New MessageRepository");
    }

    public boolean saveMessage(Message message) {
        return messageDAO.storeOne(message);
    }

    public Optional<Message> findMessageById(int messageId) {
        return messageDAO.getOneById(messageId);
    }

    public List<Message> findMessagesForReceiver(User receiver) {
        List<Message> listOfMessages = messageDAO.getAllForReceiver(receiver);
//        for (Message message : listOfMessages) {
//            // TODO nog te maken, zie John's advies
//            //  de zender en ontvanger van de messages worden via een speciale methode in de UserDAO opgehaald,
//            //  die een messageId als argument heeft, en een join op de Message tabel doet.
//            // dan heb je toch twee methodes nodig in UserDAO? zie hieronder
//            message.setSender(userDAO.getSenderByMessageId(message.getMessageId()));
//            message.setReceiver(userDAO.getReceiverByMessageId(message.getMessageId()));
//        }
        return listOfMessages;
    }

//     voor in Leo's JdbcUserDAO
//    public Optional<User> getSenderByMessageId(int messageId) {
//        Optional<User> sender = jdbcTemplate.query(
//                "SELECT User.*, Message.receiverId, Message.senderId  FROM `User` JOIN `Message` ON userId = senderId WHERE messageId = ? LIMIT 1;",
//                new JdbcUserDAO.UserRowMapper(), messageId);
//        return sender;
//    }

    /**
     * update message, also used for setting the archive flag
     * instead of deleting them from database
     * @param message
     */
    public boolean updateMessage(Message message) {
        return messageDAO.updateOne(message);
    }

    public void archiveMessageForSender(Message message) {
    }

    public void archiveMessageForReceiver(Message message) {
    }
}