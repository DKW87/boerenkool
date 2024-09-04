package boerenkool.database.repository;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.MessageDAO;
import boerenkool.database.dao.mysql.UserDAO;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Bart Notelaers
 */
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

    private Optional<Message> addUsersToMessage(Optional<Message> message) {
        if (message.isPresent()) {
            message.get().setSender(userDAO.getSenderByMessageId(message.get().getMessageId())
                    .orElse(null));
            message.get().setReceiver(userDAO.getReceiverByMessageId(message.get().getMessageId())
                    .orElse(null));
        }
        return message;
    }

    /**
     * add users to EXISTING (already stored) message
     * not used for new messages (that do not have a messageId yet)
     * @param message
     * @return
     */
    private void addUsersToMessage(Message message) {
        message.setSender(userDAO.getSenderByMessageId(message.getMessageId()).orElse(null));
        message.setReceiver(userDAO.getReceiverByMessageId(message.getMessageId()).orElse(null));
    }

    private void addUsersToMessages(List<Message> listOfMessages) {
        for (Message message : listOfMessages) {
            addUsersToMessage(message);
        }
    }

    public boolean saveMessage(Message message) {
        return messageDAO.storeOne(message);
    }

    public Message getMessageById(int messageId) throws MessageDoesNotExistException {
        Optional<Message> message = messageDAO.getOneById(messageId);
        if (message.isPresent()) {
            addUsersToMessage(message.get());
            return message.get();
        }
        throw new MessageDoesNotExistException();
    }

    public List<Message> getAllToReceiverId(int receiverId) {
        List<Message> listOfMessages = messageDAO.getAllToReceiverId(receiverId);
        addUsersToMessages(listOfMessages);
        return listOfMessages;
    }

    public List<Message> getAllFromSenderId(int senderId) {
        List<Message> listOfMessages = messageDAO.getAllFromSenderId(senderId);
        addUsersToMessages(listOfMessages);
        return listOfMessages;
    }

    public List<Message> getAllByUserId(int userId) {
        List<Message> listOfMessages = messageDAO.getAllByUserId(userId);
        addUsersToMessages(listOfMessages);
        return listOfMessages;
    }

    public List<Message> getAll() {
        List<Message> listOfMessages = messageDAO.getAll();
        addUsersToMessages(listOfMessages);
        return listOfMessages;
    }

    public int numberOfUnreadMessages(int receiverId) {
        return messageDAO.numberOfUnreadMessages(receiverId);
    }

    public boolean updateMessage(Message message) throws MessageDoesNotExistException {
        return messageDAO.updateOne(message);
    }

    public boolean setReadByReceiver(Message message) {
        return messageDAO.setReadByReceiver(message);
    }

    public boolean deleteMessage(int messageId) {
        return messageDAO.removeOneById(messageId);
    }
}