package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
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
@RequestMapping("/api/messages")
//@CrossOrigin(origins = {"http://localhost:5500", "http://localhost:8080", "http://localhost:63342/", "http://127.0.0.1:5500/"})
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final BlockedUserService blockedUserService;


    @Autowired
    public MessageController(MessageService messageService, UserService userService,
                             AuthorizationService authorizationService, BlockedUserService blockedUserService) {
        this.messageService = messageService;
        logger.info("new MessageController");
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.blockedUserService = blockedUserService;
    }

    @PostMapping
    ResponseEntity<?> saveMessage(@RequestHeader("Authorization") String token,
                                  @RequestBody MessageDTO messageDTO) {
        // eventuele verbetering; geef de messageId terug van de bewaarde Messsage
        // (save methode in MessageDAO moet daarvoor een int teruggeven; genericDAO moet weer aangepast... gedoe!)
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        int receiverId = messageDTO.getReceiverId();
        if (validatedUser.isPresent() && userService.getOneById(receiverId).isPresent()) {
            int senderId = validatedUser.get().getUserId();
            // perhaps overkill ; to prevent hacking the dto for sender spoofing
            messageDTO.setSenderId(senderId);
            // check if sender is blocked by receiver
            if (blockedUserService.isUserBlocked(senderId, receiverId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return new ResponseEntity<>(messageService.saveMessage(messageDTO), HttpStatus.CREATED);
            }
        } else  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

    @GetMapping
    ResponseEntity<?> getAllForUser(@RequestParam(name = "box", required = false, defaultValue = "") String box,
                                    @RequestHeader("Authorization") String token) {
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent()) {
            int userId = validatedUser.get().getUserId();
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

    @GetMapping("/{messageid}")
    ResponseEntity<?> getById(@PathVariable("messageid") int messageId) throws MessageDoesNotExistException {
        // TODO user authentication  (user is receiver of this message)
        MessageDTO messageDTO = messageService.getByMessageId(messageId);
        if (messageDTO != null) {
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } else throw new MessageDoesNotExistException();
    }

    @GetMapping("/unreadmessages")
    ResponseEntity<?> numberOfUnreadMessages(@RequestHeader("Authorization") String token) {
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent()) {
            int numberOfUnread = messageService.numberOfUnreadMessages(validatedUser.get().getUserId());
            return new ResponseEntity<>(numberOfUnread, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping
    ResponseEntity<?> updateMessage(@RequestBody MessageDTO messageDTO,
                                    @RequestHeader("Authorization") String token)
            throws MessageDoesNotExistException, MessageNotSavedException {
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent()) {
            int userId = validatedUser.get().getUserId();
            MessageDTO messageFromDatabase = messageService.getByMessageId(messageDTO.getMessageId());
            if (userId == messageDTO.getSenderId()) {
                // sender can update subject and body
                messageFromDatabase.setSubject(messageDTO.getSubject());
                messageFromDatabase.setBody(messageDTO.getBody());
            } else if (userId == messageDTO.getReceiverId()) {
                // receiver can only update ReadByReceiver and ArchivedByReceiver booleans
                messageFromDatabase.setReadByReceiver(messageDTO.getReadByReceiver());
                messageFromDatabase.setArchivedByReceiver(messageDTO.getArchivedByReceiver());
            } else {
                System.out.println("updateMessage : userId not equal to senderId or receiverId");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (messageService.updateMessage(messageFromDatabase)) {
                System.out.println(messageFromDatabase);
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                throw new MessageNotSavedException();
            }
        } else {
            System.out.println("updateMessage : validateUser not present");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping()
    ResponseEntity<?> deleteMessage(@RequestBody MessageDTO messageDTO,
                                    @RequestHeader("Authorization") String token)
            throws MessageDoesNotExistException {
        Optional<User> validatedUser = authorizationService.validate(UUID.fromString(token));
        if (validatedUser.isPresent()) {
            int userId = validatedUser.get().getUserId();
            MessageDTO messageFromDatabase = messageService.getByMessageId(messageDTO.getMessageId());
            if (userId == messageDTO.getSenderId() & userId == messageFromDatabase.getSenderId()) {
                messageService.deleteMessage(messageDTO.getMessageId());
                return ResponseEntity.ok("Message deleted");
            } else {
                throw new UserIsNotSenderOfMessage();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
