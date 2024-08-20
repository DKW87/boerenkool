package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.communication.dto.UserDto;
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

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);


    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public User register(UserDto userDto) throws RegistrationFailedException {
        logger.info("Starting registration process for username: {}", userDto.getUsername());

        Optional<User> existing = userRepository.findByUsername(userDto.getUsername());
        if (existing.isPresent()) {
            logger.warn("Registration failed: username {} already exists", userDto.getUsername());
            throw new RegistrationFailedException("Gebruikersnaam bestaat al.");
        }

        Optional<User> existingEmail = userRepository.findByEmail(userDto.getEmail());
        if (existingEmail.isPresent()) {
            logger.warn("Registration failed: email {} already in use", userDto.getEmail());
            throw new RegistrationFailedException("E-mailadres is al in gebruik.");
        }

        if (!isValidPassword(userDto.getPassword())) {
            logger.warn("Registration failed: password does not meet requirements");
            throw new RegistrationFailedException("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");
        }

        User user = new User(userDto, passwordService);
        logger.info("Saving new user to database: {}", user.getUsername());
        userRepository.storeOne(user);

        logger.info("Registration successful for username: {}", user.getUsername());
        return user;
    }

    public User validateLogin(String username, String plainPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String storedSalt = user.getSalt();
            String storedHashedPassword = user.getHashedPassword();

            String hashedInputPassword = passwordService.hashPassword(plainPassword, storedSalt);

            if (storedHashedPassword.equals(hashedInputPassword)) {
                return user;
            }
        }
        return null;
    }

    // Methode om het wachtwoord te valideren
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password != null && password.matches(passwordRegex);
    }

}
