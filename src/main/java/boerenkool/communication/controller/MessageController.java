package boerenkool.communication.controller;

import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import boerenkool.utilities.exceptions.MessageNotSavedException;
import boerenkool.utilities.exceptions.UserIsNotSenderOfMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bart Notelaers
 */
@RestController
@RequestMapping("/api")
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final UserService userService;

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        logger.info("new MessageController");
        this.userService = userService;
    }

    // save ("send") a new message
    @PostMapping("/messages")
    ResponseEntity<?> saveMessage(@RequestBody MessageDTO messageDTO) throws MessageNotSavedException {
        // TODO check user is authenticated as sender of this message
        // eventuele verbetering; geef de messageId terug van de bewaarde Messsage
        // (save methode in MessageDAO moet daarvoor een int teruggeven; genericDAO moet weer aangepast... gedoe!)
        if (messageService.saveMessage(messageDTO)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            throw new MessageNotSavedException();
        }
    }

    @GetMapping("/messages")
    ResponseEntity<?> getAllMessages() throws MessageDoesNotExistException {
        List<MessageDTO> listOfUsersMessages = messageService.getAllMessages();
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            throw new MessageDoesNotExistException();
        }
    }

    @GetMapping("/users/{userid}/messages")
    ResponseEntity<?> getAllByUserId(@PathVariable("userid") int userId,
                                     @RequestParam(name = "box", required = false) String box) throws MessageDoesNotExistException {
        // TODO user authentication (user can only request his/her OWN messages)
        List<MessageDTO> listOfUsersMessages;
        if (box == "in") {
            listOfUsersMessages = messageService.getAllFromSenderId(userId);
        } else if (box == "out") {
            listOfUsersMessages = messageService.getAllToReceiverId(userId);
        } else {
            listOfUsersMessages = messageService.getAllByUserId(userId);
        }
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            throw new MessageDoesNotExistException();
        }
    }

    @GetMapping("/users/{userid}/messages/inbox")
    ResponseEntity<?> getAllToReceiverId(@PathVariable("userid") int userId) throws MessageDoesNotExistException {
        // TODO user authentication (user can only request his/her OWN messages)
        List<MessageDTO> listOfUsersMessages = messageService.getAllToReceiverId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            throw new MessageDoesNotExistException();
        }
    }

    @GetMapping("/users/{userid}/messages/outbox")
    ResponseEntity<?> getAllFromSenderId(@PathVariable("userid") int userId) throws MessageDoesNotExistException {
        // TODO user authentication (user can only request his/her OWN messages)
        List<MessageDTO> listOfUsersMessages = messageService.getAllFromSenderId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            throw new MessageDoesNotExistException();
        }
    }

    @GetMapping("/messages/{messageid}")
    ResponseEntity<?> getById(@PathVariable("messageid") int messageId) throws MessageDoesNotExistException {
        // TODO user authentication  (user is receiver of this message)
        MessageDTO messageDTO = messageService.getByMessageId(messageId);
        if (messageDTO != null) {
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } else throw new MessageDoesNotExistException();
    }

    @PutMapping("/users/{userid}/messages")
    ResponseEntity<?> updateMessage(@PathVariable("userid") int userId,
                                    @RequestBody MessageDTO messageDTO)
            throws MessageDoesNotExistException, MessageNotSavedException, UserIsNotSenderOfMessage {
        // TODO user authentication (user is sender of this message)
        if (userId == messageDTO.getSenderId()) {
            if (messageService.updateMessage(messageDTO)) {
                return new ResponseEntity<>("Message updated", HttpStatus.OK);
            } else {
                throw new MessageNotSavedException();
            }
        } else {
            throw new UserIsNotSenderOfMessage();
        }
    }

    @DeleteMapping("/messages/{messageid}")
    ResponseEntity<?> deleteMessage(@PathVariable("messageid") int messageId)
            throws MessageDoesNotExistException {
        // TODO user authentication (user must be sender to delete a message)
        if (messageService.deleteMessage(messageId)) {
            return ResponseEntity.ok("Message deleted");
        } else {
            throw new MessageDoesNotExistException();
        }
    }
}
