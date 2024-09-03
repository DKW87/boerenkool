package boerenkool.utilities.authorization;

import boerenkool.business.model.User;
import org.apache.el.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationService {

    protected final TokenUserPairDao tokenUserPairDao;

    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Autowired
    //injecteer de dao in de service
    public AuthorizationService(TokenUserPairDao tokenUserPairDao) {
        this.tokenUserPairDao = tokenUserPairDao;
        logger.info("New AuthorizationService.");
    }

    //genereer een willekurige uuid (token)
    private UUID createOpaqueKey() {
        return UUID.randomUUID();
    }


    public TokenUserPair authorize(User user) {
        //zoek of er al een tokenpair bestaat voor user
        Optional<TokenUserPair> pairOption = tokenUserPairDao.findByUser(user);
        //verwijder het token zodat er niet versch token voor dezelfde gebruiker bestaan
        if (pairOption.isPresent()) {
            tokenUserPairDao.delete(pairOption.get().getKey());
        }
        //maak nieuwe token aan
        UUID token = createOpaqueKey();
        TokenUserPair tokenUserPair = new TokenUserPair(token, user);
        tokenUserPairDao.save(tokenUserPair);
        return tokenUserPair;
    }

    public Optional<User> validate(UUID token) {
        Optional<TokenUserPair> pair = tokenUserPairDao.findByKey(token);
        if (pair.isPresent()) {
            //als pair bestaat retourneer de gebruiker die er bij hoort
            return Optional.of(pair.get().getUser());
        }
        return Optional.empty();
    }

    public Optional<String> getUsernameByToken(UUID token) {
        Optional<TokenUserPair> pair = tokenUserPairDao.findByKey(token);
        if (pair.isPresent()) {
            return Optional.of(pair.get().getUser().getUsername());
        }
        return Optional.empty();
    }
}
