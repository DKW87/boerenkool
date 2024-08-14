package boerenkool.communication.controller;

import boerenkool.business.model.Message;
import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
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
    ResponseEntity<?> saveMessage(@RequestBody MessageDTO messageDTO) {
        logger.info("MessageController.sendMessage is called");
        if (messageService.saveMessage(messageDTO)) {
            return ResponseEntity.ok().body(messageDTO);
        } else {
            logger.info("sendMessage failed");
            return ResponseEntity.internalServerError().body("Message not saved");
        }
    }

    @GetMapping("user/{userId}/messages")
    ResponseEntity<?> getAllMessagesforUserId(@PathVariable int userId) {
        // TODO the user can only request his/her OWN messages.
        //  where to check for authorisation? in MessageService?
        // TODO return only a list of 'stripped' Message objects (sender, date, subject)
        //  without body text?
        logger.info("getAllMessagesforUserId is called");
        List<MessageDTO> listOfUsersMessages = messageService.findMessagesForReceiverId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            logger.info("getAllMessagesforUserId list is EMPTY");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("user/{userId}/messages/{messageId}")
    ResponseEntity<?> getMessageforUserById(@PathVariable int userId, @PathVariable int messageId) {
        logger.info("getMessageforUserById is called");
        // TODO check user validated
        messageService.findMessageById(messageId);
        if (messageService.findMessageById(messageId) != null) {
            return ResponseEntity.ok().body(messageService.findMessageById(messageId));
        } else {
            logger.info("getMessageforUserById message not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/messages")
    ResponseEntity<?> updateMessage(@RequestBody MessageDTO messageDTO) {
        logger.info("updateMessage is called");
        if (messageService.updateMessage(messageDTO)) {
            return ResponseEntity.ok().body(messageService.updateMessage(messageDTO));
        } else {
            logger.info("updateMessage results in FALSE");
            return ResponseEntity.internalServerError().body("Message not updated");
        }
    }

    @DeleteMapping("/messages")
    ResponseEntity<?> deleteMessage() {
        // if logged in user = senderId, call archivedBySender method
        // if logged in user = receiverId, call archivedByReceiver method
        return ResponseEntity.ok("deleteMessage called");
    }

}
