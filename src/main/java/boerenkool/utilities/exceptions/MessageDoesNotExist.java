package boerenkool.utilities.exceptions;

public class MessageDoesNotExist extends RuntimeException {
    public MessageDoesNotExist() {
        super("Message does not exist");
    }
}
