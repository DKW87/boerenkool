package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.repository.MessageRepository;
import boerenkool.database.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        logger.info("New MessageService");
    }

    public Optional<Message> saveMessage(Message message) {
        // set DateTime for new message that is sent
        message.setDateTimeSent(LocalDateTime.now());
        return messageRepository.saveMessage(message);
    }

    public Optional<Message> findMessageById(int messageId) {
        return messageRepository.findMessageById(messageId);
    }

    public List<Message> findMessagesForReceiverId(int receiverId) {
        List<Message> listOfMessagesForReceiver = new ArrayList<>();
        if (userRepository.getOneById(receiverId).isPresent()) {
            logger.info("findMessagesForReceiver User found");
            listOfMessagesForReceiver = messageRepository.findMessagesForReceiver((User) userRepository.getOneById(receiverId).get());
            Collections.sort(listOfMessagesForReceiver);
            return listOfMessagesForReceiver;
        } else {
            logger.info("findMessagesForReceiver User not found");
            return listOfMessagesForReceiver;
        }
    }

    public boolean updateMessage(Message message) {
        return messageRepository.updateMessage(message);
    }
}
