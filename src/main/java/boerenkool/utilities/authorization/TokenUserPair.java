package boerenkool.utilities.authorization;

import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/*De TokenMemberPair klasse kan worden gebruikt in een authenticatiecontext, waar een gebruiker inlogt en een sessie of
 JWT (JSON Web Token) wordt aangemaakt. De UUID dient als een token dat de sessie identificeert, en de User is de ingelogde gebruiker .*/
public class TokenUserPair {
    //unieke sleutel met 128 bits waarde
    private UUID key;
    //user geassocieerd met het token
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
