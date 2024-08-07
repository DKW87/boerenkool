package boerenkool.communication.dto;

import boerenkool.business.model.User;

import java.util.Optional;

public interface UserDAO {
    void save(User user);

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    //Optional<User> findAuthorOfMessage(Message message);
}
