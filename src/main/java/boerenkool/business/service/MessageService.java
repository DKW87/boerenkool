package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
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

    public boolean saveMessage(MessageDTO messageDTO) {
        // TODO convert MessageDTO to Message using convert method (which uses MessageRepository)
        return true;
        // set DateTime for new message that is sent

//        message.setDateTimeSent(LocalDateTime.now());
//        return messageRepository.saveMessage(message);
    }

    public MessageDTO findMessageById(int messageId) {
        if (messageRepository.getMessageById(messageId).isPresent()) {
            return convertMessageToDTO(messageRepository.getMessageById(messageId).get());
        } else return null;
    }

    public List<MessageDTO> findMessagesForReceiverId(int receiverId) {
        List<MessageDTO> listOfMessageDTOsForReceiver = new ArrayList<>();
        if (userRepository.getOneById(receiverId).isPresent()) {
            logger.info("findMessagesForReceiver User found");
//            listOfMessagesForReceiver = messageRepository.findMessagesForReceiver((User) userRepository.getOneById(receiverId).get());
            List<Message> listOfMessages = messageRepository.getAllForReceiverId(receiverId);
            // convert Messages to MessageDTOs
            for (Message message : listOfMessages){
                listOfMessageDTOsForReceiver.add(convertMessageToDTO(message));
            }
            Collections.sort(listOfMessageDTOsForReceiver);
            return listOfMessageDTOsForReceiver;
        } else return null;
    }

    public boolean updateMessage(MessageDTO messageDTO) {
        return messageRepository.updateMessage(convertDtoToMessage(messageDTO));
    }

    private Message convertDtoToMessage(MessageDTO dto) {
        return new Message(dto.getMessageId(),
                userRepository.getOneById(dto.getSenderId()),
                userRepository.getOneById(dto.getReceiverId()),
                LocalDateTime.now(),
                dto.getSubject(),
                dto.getBody(),
                dto.isReadByReceiver(),
                dto.isArchivedBySender(),
                dto.isArchivedByReceiver());
    }

    private MessageDTO convertMessageToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO(message.getMessageId(),
                0,
                0,
                message.getDateTimeSent(),
                message.getSubject(),
                message.getBody(),
                message.isReadByReceiver(),
                message.isArchivedBySender(),
                message.isArchivedByReceiver());
        if (message.getSender().isPresent() & message.getReceiver().isPresent()) {
            messageDTO.setSenderId(message.getSender().get().getUserId());
            messageDTO.setReceiverId(message.getReceiver().get().getUserId());
        }
        return messageDTO;
    }
}
