package boerenkool.utilities.authorization;

import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class TokenUserPair {

    private UUID key;
    private User user;

    private final Logger logger = LoggerFactory.getLogger(TokenUserPair.class);

    public TokenUserPair(UUID key, User user) {
        super();
        this.key = key;
        this.user = user;
        logger.info("New TokenUserPair");
    }

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
