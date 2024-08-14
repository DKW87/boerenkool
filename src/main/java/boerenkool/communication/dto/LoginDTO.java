package boerenkool.communication.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginDTO {

    private String username;
    private String password;

    private final Logger logger = LoggerFactory.getLogger(LoginDTO.class);

    public LoginDTO() {
        logger.info("New LoginDTO");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
