package boerenkool.communication.controller;

import boerenkool.business.model.Message;
import boerenkool.business.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController(value = "/api")
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
        logger.info("new MessageController");
    }

    @GetMapping("testmessage/{messageId}")
    Message findMessageByIdTest(@PathVariable int messageId) {
        logger.info("MessageController.findMessageByIdTest is called");
        Optional<Message> optionalMessage = messageService.findMessageById(messageId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

    @GetMapping("user/{userId}/messages/{messageId}")
    Message findMessageByIdforUser(@PathVariable int userId, @PathVariable int messageId) {
        // TODO the user can only request his/her OWN messages.
        //  where to check for authorisation? in MessageService?
        logger.info("MessageController.findMessageByIdforUser is called");
        Optional<Message> optionalMessage = messageService.findMessageById(messageId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }

//    @GetMapping("user/{userId}/messages/")
//    Message getAllMessagesforUser(@PathVariable int userId) {
//        User  userID
//        List<Message> listOfMessages = messageService.findMessagesForReceiver(userId);
//        if (optionalMessage.isPresent()) {
//            return optionalMessage.get();
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
//        }
//    }


}
