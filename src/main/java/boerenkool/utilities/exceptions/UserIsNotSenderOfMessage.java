package boerenkool.utilities.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserIsNotSenderOfMessage extends RuntimeException {
    public UserIsNotSenderOfMessage() {
        super("User is not the sender of the message");
    }
}