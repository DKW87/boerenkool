package boerenkool.utilities.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class MessageNotSavedException extends Exception {
    public MessageNotSavedException() {
        super("Message was not saved.");
    }
}
