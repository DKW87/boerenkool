package boerenkool.utilities.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "User update failed.")
public class UserUpdateFailedException extends RuntimeException {
    public UserUpdateFailedException(String message) {
        super(message);
    }

}