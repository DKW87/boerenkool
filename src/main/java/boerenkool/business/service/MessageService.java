package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.database.repository.MessageRepository;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bart Notelaers
 */
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public boolean saveMessage(MessageDTO messageDTO) throws UserNotFoundException {
        if (userRepository.getOneById(messageDTO.getReceiverId()).isPresent()  // TODO verplaats check naar controller
                && userRepository.getOneById(messageDTO.getSenderId()).isPresent()) {
            messageDTO.setDateTimeSent(LocalDateTime.now());
            return messageRepository.saveMessage(convertDTOToMessage(messageDTO));
        } else {
            throw new UserNotFoundException("SenderId and / or userId not linked to existing user(s)");
        }
    }

    public MessageDTO getByMessageId(int messageId) throws MessageDoesNotExistException {
        return convertMessageToDTO(messageRepository.getMessageById(messageId));
    }

    public List<MessageDTO> getAllMessages() {
        List<MessageDTO> listOfAllMessageDTOs = new ArrayList<>();
        List<Message> listOfMessages = messageRepository.getAll();
        // convert Messages to MessageDTOs aparte methode maken

        for (Message message : listOfMessages) {
            listOfAllMessageDTOs.add(convertMessageToDTO(message));
        }
        Collections.sort(listOfAllMessageDTOs);
        return listOfAllMessageDTOs;
    }

    public List<MessageDTO> getAllByUserId(int userId) {
        List<Message> listOfMessages = messageRepository.getAllByUserId(userId);
        return convertMessagesToDTOs(listOfMessages);
    }

    public List<MessageDTO> getAllFromSenderId(int senderId) {
        List<Message> listOfMessages = messageRepository.getAllFromSenderId(senderId);
        return convertMessagesToDTOs(listOfMessages);
    }

    public List<MessageDTO> getAllToReceiverId(int receiverId) {
        List<Message> listOfMessages = messageRepository.getAllToReceiverId(receiverId);
        return convertMessagesToDTOs(listOfMessages);
    }

    public boolean updateMessage(MessageDTO messageDTO) throws MessageDoesNotExistException {
        return messageRepository.updateMessage(convertDTOToMessage(messageDTO));
    }

    public boolean deleteMessage(int messageId) {
        return messageRepository.deleteMessage(messageId);
    }

    private Message convertDTOToMessage(MessageDTO dto) {
        User sender = userRepository.getOneById(dto.getSenderId()).orElse(null); // TODO verplaats check naar controller
        User receiver = userRepository.getOneById(dto.getReceiverId()).orElse(null); // TODO verplaats check naar controller
        if (sender != null & receiver != null) {
            return new Message(
                    dto.getMessageId(),
                    sender,
                    receiver,
                    dto.getDateTimeSent(),
                    dto.getSubject(),
                    dto.getBody(),
                    dto.isReadByReceiver(),
                    dto.isArchivedBySender(),
                    dto.isArchivedByReceiver());
        } else {
            logger.info("sender and/or receiver is null, message will be null");
            return null;
        }
    }

    private MessageDTO convertMessageToDTO(Message message) {
        int senderId = message.getSender().getUserId();
        int receiverId = message.getReceiver().getUserId();
        return new MessageDTO(message.getMessageId(),
                senderId,
                receiverId,
                message.getDateTimeSent(),
                message.getSubject(),
                message.getBody(),
                message.isReadByReceiver(),
                message.isArchivedBySender(),
                message.isArchivedByReceiver());
    }

    private List<MessageDTO> convertMessagesToDTOs(List<Message> listOfMessages) {
        List<MessageDTO> listOfDTOs = new ArrayList<>();
        for (Message message : listOfMessages) {
            listOfDTOs.add(convertMessageToDTO(message));
        }
        return listOfDTOs;
    }
}
