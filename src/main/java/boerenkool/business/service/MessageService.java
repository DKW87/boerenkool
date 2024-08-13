package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

//    private final Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
//        logger.info("New MessageService");
    }

//        message.setDateTimeSent(Local.now()); datumtijd instellen (logic) moet in Services laag staan

    public void saveMessage(Message message) {
        messageRepository.saveMessage(message);
    }

    public Message findMessageById(int messageId) {
        Optional<Message> optionalMessage = messageRepository.findMessageById(messageId);
        return optionalMessage.orElse(null);

    }

    public List<Message> findMessagesForReceiver(User receiver) {
//        Optional<Message> optionalMessage = messageRepository.findMessageById(messageId);
//        if (optionalMessage.isPresent()) {
//            Message message = optionalMessage.get();
            return messageRepository.findMessagesForReceiver(receiver);
//        }
//        return new ArrayList<>();
    }


    public void updateMessage(Message message) {
        messageRepository.updateMessage(message);
    }
}
