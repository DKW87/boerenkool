package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import boerenkool.utilities.exceptions.MessageNotSavedException;
import boerenkool.utilities.exceptions.UserIsNotSenderOfMessage;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Bart Notelaers
 */
@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:5500", "http://localhost:8080", "http://localhost:63342/", "http://127.0.0.1:5500/"})
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UserService userService;
    private final AuthorizationService authorizationService;


    @Autowired
    public MessageController(MessageService messageService, UserService userService,
                             AuthorizationService authorizationService) {
        this.messageService = messageService;
        logger.info("new MessageController");
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    // save ("send") a new message
    @PostMapping("/messages")
    ResponseEntity<?> saveMessage(@RequestBody MessageDTO messageDTO)
            throws UserNotFoundException {
        // TODO check user is authenticated as sender of this message
        // eventuele verbetering; geef de messageId terug van de bewaarde Messsage
        // (save methode in MessageDAO moet daarvoor een int teruggeven; genericDAO moet weer aangepast... gedoe!)
        if (userService.getOneById(messageDTO.getReceiverId()).isPresent()
                && userService.getOneById(messageDTO.getSenderId()).isPresent()) {
            return new ResponseEntity<>(messageService.saveMessage(messageDTO), HttpStatus.CREATED);
        } else throw new UserNotFoundException("SenderId and / or userId not linked to existing user(s)");
    }
// not used in front end (security risk)
//    @GetMapping("/messages")
//    ResponseEntity<?> getAllMessages() throws MessageDoesNotExistException {
//        List<MessageDTO> listOfUsersMessages = messageService.getAllMessages();
//        if (!listOfUsersMessages.isEmpty()) {
//            return ResponseEntity.ok().body(listOfUsersMessages);
//        } else {
//            throw new MessageDoesNotExistException();
//        }
//    }

    @GetMapping("/users/{userid}/messages")
    ResponseEntity<?> getAllByUserId(@PathVariable("userid") int userId,
                                     // hier zou de defaultValue misschien "in" kunnen worden; standaard inbox tonen
                                     @RequestParam(name = "box", required = false, defaultValue = "") String box,
                                     @RequestHeader("Authorization") String token)
            throws MessageDoesNotExistException {
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent() && validatedUser.get().getUserId() == userId) {
            List<MessageDTO> listOfUsersMessages;
            if (box.equals("in")) {
                listOfUsersMessages = messageService.getAllToReceiverId(userId);
            } else if (box.equals("out")) {
                listOfUsersMessages = messageService.getAllFromSenderId(userId);
            } else {
                logger.info("box parameter : value is not null, but invalid value");
                listOfUsersMessages = messageService.getAllByUserId(userId);
            }
            if (listOfUsersMessages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.ok().body(listOfUsersMessages);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/messages/{messageid}")
    ResponseEntity<?> getById(@PathVariable("messageid") int messageId) throws MessageDoesNotExistException {
        // TODO user authentication  (user is receiver of this message)
        MessageDTO messageDTO = messageService.getByMessageId(messageId);
        if (messageDTO != null) {
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } else throw new MessageDoesNotExistException();
    }

    @GetMapping("/messages/unreadmessages")
    ResponseEntity<?> checkForUnreadMessages(@RequestHeader("Authorization") String token) {
        // TODO user authentication  (user is receiver of this message)
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent()) {
            int numberOfUnread = messageService.checkForUnreadMessages(validatedUser.get().getUserId());
            return new ResponseEntity<>(numberOfUnread, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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
