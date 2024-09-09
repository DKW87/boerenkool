package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.communication.dto.PasswordResetDto;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final AuthorizationService authorizationService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordService passwordService, AuthorizationService authorizationService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.authorizationService = authorizationService;
    }

    public User register(UserDto userDto) throws RegistrationFailedException {
        logger.info("Starting registration process for username: {}", userDto.getUsername());

        validateUsernameAndEmail(userDto);

        User user = createUser(userDto);
        userRepository.storeOne(user);

        return user;
    }

    private void validateUsernameAndEmail(UserDto userDto) throws RegistrationFailedException {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            logger.warn("Registration failed: username {} already exists", userDto.getUsername());
            throw new RegistrationFailedException("Gebruikersnaam bestaat al.");
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            logger.warn("Registration failed: email {} already in use", userDto.getEmail());
            throw new RegistrationFailedException("E-mailadres is al in gebruik.");
        }
    }


    private User createUser(UserDto userDto) {
        String salt = passwordService.generateSalt();
        String hashedPassword = passwordService.hashPassword(userDto.getPassword(), salt);
        return new User(userDto, hashedPassword, salt);
    }

    public User validateLogin(String username, String plainPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.filter(user -> isPasswordValid(user, plainPassword))
                .orElse(null);
    }

    private boolean isPasswordValid(User user, String plainPassword) {
        String hashedInputPassword = passwordService.hashPassword(plainPassword, user.getSalt());
        if (user.getHashedPassword().equals(hashedInputPassword)) {
            logger.info("Login successful for user: {}", user.getUsername());
            return true;
        } else {
            logger.warn("Login failed for user: {}. Passwords do not match.", user.getUsername());
            return false;
        }
    }

    public boolean resetPassword(PasswordResetDto passwordResetDto) throws RegistrationFailedException {
        User user = validateTokenAndEmail(passwordResetDto);

        updatePassword(user, passwordResetDto.getNewPassword());

        return true;
    }

    private User validateTokenAndEmail(PasswordResetDto passwordResetDto) throws RegistrationFailedException {
        UUID token = parseToken(passwordResetDto.getToken());
        Optional<User> userOpt = authorizationService.validate(token);

        if (!userOpt.isPresent() || !userOpt.get().getEmail().equals(passwordResetDto.getEmail())) {
            logger.warn("Invalid token or email mismatch for token: {}", passwordResetDto.getToken());
            throw new RegistrationFailedException("Ongeldige token of e-mail komt niet overeen.");
        }

        return userOpt.get();
    }

    private UUID parseToken(String token) throws RegistrationFailedException {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format for token: {}", token, e);
            throw new RegistrationFailedException("Ongeldig tokenformaat.");
        }
    }

    private void updatePassword(User user, String newPassword) throws RegistrationFailedException {
        String newSalt = passwordService.generateSalt();
        String hashedNewPassword = passwordService.hashPassword(newPassword, newSalt);

        user.setHashedPassword(hashedNewPassword);
        user.setSalt(newSalt);

        if (!userRepository.updateOne(user)) {
            logger.error("Failed to update password for user: {}", user.getUsername());
            throw new RegistrationFailedException("Wachtwoord kon niet worden bijgewerkt.");
        }

        logger.info("Password successfully reset for user: {}", user.getUsername());
    }

    public boolean sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found: {}", user.getUsername());

            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            passwordService.sendPasswordResetEmail(email, tokenUserPair.getKey().toString());
            return true; // Gebruiker gevonden, e-mail verstuurd
        } else {
            logger.warn("User not found for email: {}", email);
            return false; // Gebruiker niet gevonden
        }
    }


    // Methode om het wachtwoord te valideren
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password != null && password.matches(passwordRegex);
    }
}
