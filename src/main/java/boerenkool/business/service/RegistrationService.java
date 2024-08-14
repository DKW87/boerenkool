package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.Optional;

@Service
public class RegistrationService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        logger.info("New RegistrationService");
    }

    public User register(User user, String plainPassword) throws RegistrationFailedException {
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            throw new RegistrationFailedException();
        }

        String salt = passwordService.generateSalt();
        String hashedPassword = passwordService.hashPassword(plainPassword, salt);

        user.setHashedPassword(hashedPassword);
        user.setSalt(salt);

        userRepository.storeOne(user);

        return user;
    }


    public User validateLogin(String username, String plainPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Hash the provided password with the stored salt and compare
            String hashedInputPassword = PasswordService.hashPassword(plainPassword, user.getSalt());

            if (user.getHashedPassword().equals(hashedInputPassword)) {
                return user;
            }
        }
        return null;  // Return null if the user doesn't exist or the password doesn't match
    }

}
