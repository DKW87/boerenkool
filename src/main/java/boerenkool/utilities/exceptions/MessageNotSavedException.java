package boerenkool.utilities.exceptions;

import org.springframework.http.ResponseEntity;

public class MessageNotSavedException extends Exception {
    public MessageNotSavedException(String message) {
        super("Message not saved");
    }
}
