package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public User register(UserDto userDto) throws RegistrationFailedException {
        Optional<User> existing = userRepository.findByUsername(userDto.getUsername());
        if (existing.isPresent()) {
            throw new RegistrationFailedException();
        }

        // Maak een nieuwe gebruiker aan met de gegenereerde salt en gehashte wachtwoord
        User user = new User(userDto, passwordService);

        // Sla de gebruiker op
        userRepository.storeOne(user);

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
}
