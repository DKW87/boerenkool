package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.database.repository.MessageRepository;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public boolean saveMessage(MessageDTO messageDTO) {
        messageDTO.setDateTimeSent(LocalDateTime.now());
        return messageRepository.saveMessage(convertDtoToMessage(messageDTO));
    }

    public MessageDTO getByMessageId(int messageId) throws MessageDoesNotExistException {
        return convertMessageToDTO(messageRepository.getMessageById(messageId));
    }

    public List<MessageDTO> getAllMessages() {
        logger.info("getAllMessages called");
        List<MessageDTO> listOfAllMessageDTOs = new ArrayList<>();
        List<Message> listOfMessages = messageRepository.getAll();
        for (Message message : listOfMessages) {
            listOfAllMessageDTOs.add(convertMessageToDTO(message));
        }
        Collections.sort(listOfAllMessageDTOs);
        return listOfAllMessageDTOs;
    }

    public List<MessageDTO> getAllByReceiverId(int receiverId) {
        List<MessageDTO> listOfMessageDTOsForReceiver = new ArrayList<>();
        if (userRepository.getOneById(receiverId).isPresent()) {
            logger.info("findMessagesForReceiver User found");
//            listOfMessagesForReceiver = messageRepository.findMessagesForReceiver((User) userRepository.getOneById(receiverId).get());
            List<Message> listOfMessages = messageRepository.getAllByReceiverId(receiverId);
            // convert Messages to MessageDTOs
            for (Message message : listOfMessages) {
                listOfMessageDTOsForReceiver.add(convertMessageToDTO(message));
            }
            Collections.sort(listOfMessageDTOsForReceiver);
            return listOfMessageDTOsForReceiver;
        } else return null;
    }

    public boolean updateMessage(MessageDTO messageDTO) throws MessageDoesNotExistException {
        return messageRepository.updateMessage(convertDtoToMessage(messageDTO));
    }

    public boolean deleteMessage(int messageId) {
        return messageRepository.deleteMessage(messageId);
    }

    public boolean archiveMessageForSender(MessageDTO messageDTO) {
        return messageRepository.archiveMessageForSender(convertDtoToMessage(messageDTO));
    }

    public boolean archiveMessageForReceiver(MessageDTO messageDTO) {
        return messageRepository.archiveMessageForReceiver(convertDtoToMessage(messageDTO));
    }

    private Message convertDtoToMessage(MessageDTO dto) {
        User sender = userRepository.getOneById(dto.getSenderId()).orElse(null);
        User receiver = userRepository.getOneById(dto.getReceiverId()).orElse(null);
//        System.out.println(dto);
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
}
