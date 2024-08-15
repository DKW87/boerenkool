package boerenkool.utilities.authorization;

import java.util.Optional;
import java.util.UUID;
import boerenkool.business.model.User;


public interface TokenUserPairDao {

    public void save(TokenUserPair tokenUserPair);

    public Optional<TokenUserPair> findByKey(UUID key);

    public Optional<TokenUserPair> findByUser(User user);

    public void delete(UUID uuid);
}