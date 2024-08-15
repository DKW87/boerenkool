package boerenkool.communication.controller;

import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.utilities.exceptions.MessageDoesNotExist;
import boerenkool.utilities.exceptions.MessageNotSavedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
//        logger.info("saveMessage is called");
        // eventueel aanpassen; geef messageDTO de messageId van de bewaarde Messsage
        // (save methode in MessageDAO moet daarvoor een int teruggeven; genericDAO moet weer aangepast... gedoe!)
        if (messageService.saveMessage(messageDTO)) {
            return ResponseEntity.ok().build();
        } else {
            logger.info("saveMessage failed");
            return ResponseEntity.badRequest().body("Message not saved");
        }
    }

    @GetMapping("/messages")
    ResponseEntity<?> getAllMessages() throws MessageDoesNotExist {
//        logger.info("getAllMessages is called");
        List<MessageDTO> listOfUsersMessages = messageService.getAllMessages();
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            logger.info("getAllMessages list is EMPTY");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{userid}/messages")
    ResponseEntity<?> getAllByReceiverId(@PathVariable("userid") int userId) {
        // TODO user authentication (user can only request his/her OWN messages)
//        logger.info("getAllByReceiverId is called");
//        userService.getOneById(userId);
        List<MessageDTO> listOfUsersMessages = messageService.getAllByReceiverId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            logger.info("getAllByReceiverId list is EMPTY");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/messages/{messageid}")
    ResponseEntity<?> getById(@PathVariable("messageid") int messageId) throws MessageDoesNotExist {
        // TODO user authentication  (user is receiver of this message)
        try {
            MessageDTO messageDTO = messageService.getByMessageId(messageId);
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (MessageDoesNotExist message) {
            return new ResponseEntity<>(message.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{userid}/messages/{messageid}")
    ResponseEntity<?> updateMessage(@PathVariable("userid") int userId,
                                    @PathVariable("messageid") int messageId,
                                    @RequestBody MessageDTO messageDTO) throws MessageDoesNotExist {
        // TODO user authentication (user is sender of this message)
        if (userId == messageDTO.getSenderId() && messageService.updateMessage(messageDTO)) {
            return new ResponseEntity<>("Message updated", HttpStatus.OK);
        } else {
            throw new MessageDoesNotExist();
        }
    }

    @DeleteMapping("/messages/{messageid}")
    ResponseEntity<?> deleteMessage(@PathVariable("messageid") int messageId, @RequestBody MessageDTO messageDTO) {
        logger.info("deleteMessage is called");
        // TODO user authentication (user must be sender to delete a message)
        // if (userId == messageDTO.getSenderId() ...
        if (messageService.deleteMessage(messageDTO.getMessageId())) {
            return ResponseEntity.ok("message deleted by sender");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // oude versie met delete naar archive methode, met DTO in de requestbody
//    @DeleteMapping()
//    ResponseEntity<?> archiveMessage(@RequestBody MessageDTO messageDTO, @PathVariable int userId) {
//        logger.info("archiveMessage is called");
//        // TODO check user is authenticated as sender or receiver of this message
//        //  now using pathvariab to get the userId as
//        if (userId == messageDTO.getSenderId()) {
//            messageService.archiveMessageForSender(messageDTO);
//            return ResponseEntity.ok("message archived for sender");
//        } else if (userId == messageDTO.getReceiverId()) {
//            messageService.archiveMessageForReceiver(messageDTO);
//            return ResponseEntity.ok("message archived for receiver");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
