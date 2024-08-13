package boerenkool.database.repository;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
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

    public void saveMessage(Message message) {
        messageDAO.storeOne(message);
    }

    public Optional<Message> findMessageById(int messageId) {
        Optional<MessageDTO> optional = messageDAO.getOneById(messageId);
        if (optional.isPresent()) {
            return Optional.of(convertDtoToMessage(optional.get()));
        } else {
            return Optional.empty();
        }
    }

    public Message convertDtoToMessage(MessageDTO dto) {
        return new Message(dto.getMessageId(),
                userDAO.getOneById(dto.getSenderId()),
                userDAO.getOneById(dto.getReceiverId()),
                dto.getDateTimeSent(),
                dto.getSubject(),
                dto.getBody(),
                dto.isReadByReceiver(),
                dto.isArchivedBySender(),
                dto.isArchivedByReceiver());
    }

    public List<Message> findMessagesForReceiver(User receiver) {
        List<MessageDTO> listOfMessageDTOs = messageDAO.getAllForReceiver(receiver);
        List<Message> messages = new ArrayList<>();
        for (MessageDTO messageDTO : listOfMessageDTOs) {
            messages.add(convertDtoToMessage(messageDTO));
        }
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

    public void archiveMessageForReceiver(Message message) {
    }
}