package boerenkool.utilities.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MessageDoesNotExistException extends Exception {
    public MessageDoesNotExistException() {
        super("Message does not exist");
    }
}