package boerenkool.database.repository;

import boerenkool.business.model.Message;
import boerenkool.database.dao.mysql.MessageDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageRepository {
    // private final Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    private final MessageDAO messageDAO;

    public MessageRepository(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
//        logger.info("New MessageRepository");
    }

    public void saveMessage(Message message) {
        messageDAO.storeOne(message);
    }

    public Optional<Message> findMessageById(int messageId) {
        return messageDAO.getOne(messageId);
    }

    public List<Message> findMessagesForRecipient(User recipient) {
        List<Message> messages = messageDAO.getAllForRecipient(recipient);
        Collections.sort(messages);
        return messages;
    }

    /**
     * update message, also used for setting the archive flag
     * instead of deleting them from database
     * @param message
     */
    public void updateMessage(Message message) {
        messageDAO.updateOne(message);
    }

    public void archiveMessageForSender(Message message) {
    }

    public void archiveMessageForRecipient(Message message) {
    }
}