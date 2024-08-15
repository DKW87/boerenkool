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
    private Message addUsersToMessage(Message message) {
//        if (message.getMessageId() != 0) {
        message.setSender(userDAO.getSenderByMessageId(message.getMessageId()).orElse(null));
        message.setReceiver(userDAO.getReceiverByMessageId(message.getMessageId()).orElse(null));
        return message;
    }

    public boolean saveMessage(Message message) {
//        addUsersToNewMessage(message);
        return messageDAO.storeOne(message);
    }

    public Message getMessageById(int messageId) throws MessageDoesNotExistException {
        Optional<Message> message = messageDAO.getOneById(messageId);
        if (message.isPresent()) {
            addUsersToMessage(message);
            return message.get();
        }
        throw new MessageDoesNotExistException();
    }

    public List<Message> getAllForReceiver(User receiver) {
        List<Message> listOfMessages = messageDAO.getAllForReceiver(receiver);
        for (Message message : listOfMessages) {
            addUsersToMessage(message);
        }
        return listOfMessages;
    }

    public List<Message> getAllByReceiverId(int receiverId) {
        List<Message> listOfMessages = messageDAO.getAllByReceiverId(receiverId);
        for (Message message : listOfMessages) {
            addUsersToMessage(message);
        }
        return listOfMessages;
    }

    public List<Message> getAll() {
        List<Message> listOfMessages = messageDAO.getAll();
        for (Message message : listOfMessages) {
            addUsersToMessage(message);
        }
        return listOfMessages;
    }

    public boolean updateMessage(Message message) throws MessageDoesNotExistException {
        return messageDAO.updateOne(message);
    }

    public boolean deleteMessage(int messageId) {
        return messageDAO.removeOneById(messageId);
    }

    public boolean archiveMessageForSender(Message message) {
        logger.info("archiveMessageForSender called");
        return messageDAO.archiveMessageForSender(message);
    }

    public boolean archiveMessageForReceiver(Message message) {
        return messageDAO.archiveMessageForReceiver(message);
    }
}