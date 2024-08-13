package boerenkool.communication.controller;

import boerenkool.business.model.Message;
import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    // send a new message
    @PostMapping("/messages")
    Message sendMessage(@RequestBody Message message) {
        logger.info("MessageController.sendMessage is called");
        Optional<Message> optionalMessage = messageService.saveMessage(message);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        } else {
            logger.info("sendMessage failed");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message not saved");
        }
    }

    @GetMapping("/testmessage/{messageId}")
    Message getMessageByIdTest(@PathVariable int messageId) {
        logger.info("MessageController.getMessageByIdTest is called");
        Optional<Message> optionalMessage = messageService.findMessageById(messageId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        } else {
            logger.info("getMessageByIdTest optionalMessage is NOT present");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

    @GetMapping("user/{userId}/messages")
    List<Message> getAllMessagesforUserId(@PathVariable int userId) {
        // TODO the user can only request his/her OWN messages.
        //  where to check for authorisation? in MessageService?
        // TODO return only a list of 'stripped' Message objects (sender, date, subject)
        //  without body text?
        logger.info("getAllMessagesforUserId is called");
//        User receiver = userService.getOneById(userId);
        List listOfUsersMessages = messageService.findMessagesForReceiverId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return listOfUsersMessages;
        } else {
            logger.info("getAllMessagesforUserId list is EMPTY");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

    @GetMapping("user/{userId}/messages/{messageId}")
    Message getMessageforUserById(@PathVariable int userId, @PathVariable int messageId) {
        logger.info("getMessageforUserById is called");
        // TODO check user validated
        Optional<Message> optionalMessage = messageService.findMessageById(messageId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        } else {
            logger.info("getMessageforUserById optionalMessage is NOT present");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

    @PutMapping("/messages")
    Message updateMessage(@RequestBody Message message) {
        logger.info("updateMessage is called");
        if (messageService.updateMessage(message)) {
            return message;
        } else {
            logger.info("updateMessage results in FALSE");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message not updated");
        }
    }

//    @PutMapping("/messages")
//    String updateMessage() {
//        return "updateMessage called";
//    }

    @DeleteMapping("/messages")
    String deleteMessage() {
        return "deleteMessage called";
    }

}
