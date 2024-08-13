package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("New RegistrationService");
    }

    public User register(User user, String plainPassword) throws RegistrationFailedException {
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            throw new RegistrationFailedException();
        }
        PasswordService passwordService = new PasswordService();
        String salt = passwordService.generateSalt();

        String hashedPassword = PasswordService.hashPassword(plainPassword, salt);

        user.setHashedPassword(hashedPassword);
        user.setSalt(salt);

        userRepository.storeOne(user);

        return user;
    }
}
