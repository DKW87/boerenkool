package boerenkool.communication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/messages");
public class MessageController {
    private MessageService messageService;

    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    
}
