package boerenkool.communication.controller;

import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
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
    @PostMapping()
    ResponseEntity<?> saveMessage(@RequestBody MessageDTO messageDTO) {
        // TODO check user is authenticated as sender of this message
        logger.info("saveMessage is called");
        if (messageService.saveMessage(messageDTO)) {
            // eventueel aanpassen; geef messageDTO de messageId van de bewaarde Messsage
            // (save methode in MessageDAO moet daarvoor een int teruggeven; genericDAO moet weer aangepast... gedoe!)
            return ResponseEntity.ok().build();
        } else {
            logger.info("saveMessage failed");
            return ResponseEntity.badRequest().body("Message not saved");
        }
    }

    @GetMapping("/getall")
    ResponseEntity<?> getAllMessages() {
        logger.info("getAllMessages is called");
        List<MessageDTO> listOfUsersMessages = messageService.getAllMessages();
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            logger.info("getAllMessages list is EMPTY");
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping() // app crasht bij opstarten; Ambiguous mapping. Cannot map 'messageController' method getById(int)
//    @RequestMapping("{userid}") // app start, maar bij GET request " Required request parameter 'messageid' for method parameter type int is not present
    @GetMapping("/getallforuser") // app start, maar bij GET request " Required request parameter 'messageid' for method parameter type int is not present
    ResponseEntity<?> getAllByReceiverId(@RequestParam("userid") int userId) {
        // TODO the user can only request his/her OWN messages;
        //  check authorisation
        logger.info("getAllByReceiverId is called");
        List<MessageDTO> listOfUsersMessages = messageService.getAllByReceiverId(userId);
        if (!listOfUsersMessages.isEmpty()) {
            return ResponseEntity.ok().body(listOfUsersMessages);
        } else {
            logger.info("getAllByReceiverId list is EMPTY");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getone")
    ResponseEntity<?> getById(@RequestParam("messageid") int messageId) {
        logger.info("getById is called");
        // TODO check user is authenticated as receiver of this message
        if (messageService.getByMessageId(messageId) != null) {
            return ResponseEntity.ok().body(messageService.getByMessageId(messageId));
        } else {
            logger.info("getById message not found");
            return new ResponseEntity<>("messageid not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    ResponseEntity<?> updateMessage(@RequestBody MessageDTO messageDTO) {
        // TODO check user is authenticated as sender of this message
        logger.info("updateMessage is called");
        if (messageService.updateMessage(messageDTO)) {
            return ResponseEntity.ok().body(messageService.updateMessage(messageDTO));
        } else {
            logger.info("updateMessage results in FALSE");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{messageid}{userid}")
    ResponseEntity<?> archiveMessage(@PathVariable("messageid") int messageId, @PathVariable("userid") int userId) {
        logger.info("archiveMessage is called");
        // TODO check user is authenticated as sender or receiver of this message
        //  consider using pathvariable to get the userId as well
        messageService.getByMessageId(messageId);
        if (userId == messageDTO.getSenderId()) {
            messageService.archiveMessageForSender(messageDTO);
            return ResponseEntity.ok("message archived for sender");
        } else if (userId == messageDTO.getReceiverId()) {
            messageService.archiveMessageForReceiver(messageDTO);
            return ResponseEntity.ok("message archived for receiver");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable("id") int id) {
        try {
            userService.removeOneById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // oude versie met DTO in de requestbody
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
