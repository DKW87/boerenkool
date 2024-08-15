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
    public AuthorizationService(TokenUserPairDao tokenUserPairDao) {
        super();
        this.tokenUserPairDao = tokenUserPairDao;
        logger.info("New AuthorizationService.");
    }

    private UUID createOpaqueKey() {
        return UUID.randomUUID();
    }

    public TokenUserPair authorize(User user) {
        Optional<TokenUserPair> pairOption = tokenUserPairDao.findByUser(user);
        if (pairOption.isPresent()) {
            tokenUserPairDao.delete(pairOption.get().getKey());
        }
        UUID token = createOpaqueKey();
        TokenUserPair tokenUserPair = new TokenUserPair(token, user);
        tokenUserPairDao.save(tokenUserPair);
        return tokenUserPair;
    }

    public Optional<User> validate(UUID token) {
        Optional<TokenUserPair> pair = tokenUserPairDao.findByKey(token);
        if (pair.isPresent()) {
            return Optional.of(pair.get().getUser());
        }
        return Optional.empty();
    }
}
