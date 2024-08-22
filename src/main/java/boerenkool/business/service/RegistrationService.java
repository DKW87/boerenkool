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

        // Genereer salt en hash het wachtwoord hier in de service
        String salt = passwordService.generateSalt();
        String hashedPassword = passwordService.hashPassword(userDto.getPassword(), salt);

        // Maak een nieuw User-object met het gehashte wachtwoord en salt
        User user = new User(userDto, hashedPassword, salt);
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

            // Re-hash the input password using the stored salt
            String hashedInputPassword = passwordService.hashPassword(plainPassword, storedSalt);

            logger.info("Login attempt: Stored salt = {}, Stored hashed password = {}", storedSalt, storedHashedPassword);
            logger.info("Login attempt: Hashed input password = {}", hashedInputPassword);

            if (storedHashedPassword.equals(hashedInputPassword)) {
                logger.info("Login successful for user: {}", username);
                return user;
            } else {
                logger.warn("Login failed for user: {}. Passwords do not match.", username);
            }
        } else {
            logger.warn("Login attempt failed: User not found for username: {}", username);
        }
        return null;
    }

    public boolean resetPassword(PasswordResetDto passwordResetDto) {
        logger.info("Starting password reset for email: {}", passwordResetDto.getEmail());

        // Stap 1: Valideer de reset token en haal de gebruiker op
        try {
            UUID token = UUID.fromString(passwordResetDto.getToken());
            Optional<User> userOpt = authorizationService.validate(token);
            if (!userOpt.isPresent()) {
                logger.warn("Invalid token or user not found for token: {}", passwordResetDto.getToken());
                return false;
            }

            User user = userOpt.get();

            // Stap 2: Controleer of de e-mail in het resetverzoek overeenkomt met de gebruiker
            if (!user.getEmail().equals(passwordResetDto.getEmail())) {
                logger.warn("Email in reset request does not match the user's email.");
                return false;
            }

            // Stap 3: Genereer een nieuwe salt en hash het nieuwe wachtwoord
            String newSalt = passwordService.generateSalt();
            String hashedNewPassword = passwordService.hashPassword(passwordResetDto.getNewPassword(), newSalt);

            // Debug: Log de details voor verificatie
            logger.info("Generated new salt: {}", newSalt);
            logger.info("Hashed new password: {}", hashedNewPassword);

            // Stap 4: Update de gebruiker met het nieuwe gehashte wachtwoord en de nieuwe salt
            user.setHashedPassword(hashedNewPassword);
            user.setSalt(newSalt);

            logger.info("About to update user: HashedPassword={}, Salt={}", user.getHashedPassword(), user.getSalt());


            boolean updateSuccess = userRepository.updateOne(user);
            if (!updateSuccess) {
                logger.error("Failed to update password for user: {}", user.getUsername());
                return false;
            }

            // Debug: Bevestig succesvolle update
            logger.info("Password successfully reset for user: {}", user.getUsername());
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format for token: {}", passwordResetDto.getToken(), e);
            return false;
        }
    }


    public void sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found: {}", user.getUsername());

            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            passwordService.sendPasswordResetEmail(email, tokenUserPair.getKey().toString());
        } else {
            logger.warn("User not found for email: {}", email);
        }
    }

    // Methode om het wachtwoord te valideren
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password != null && password.matches(passwordRegex);
    }
}
