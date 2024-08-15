package boerenkool.communication.controller;

import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import boerenkool.utilities.authorization.PasswordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import boerenkool.business.model.User;

import javax.security.auth.login.LoginException;
import java.util.Optional;
import java.util.UUID;


@RestController
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final AuthorizationService authorizationService;
    private final RegistrationService registrationService;
    private final PasswordService passwordService;  // Inject PasswordService

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  AuthorizationService authorizationService,
                                  PasswordService passwordService) {  // Add PasswordService to constructor
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
        this.passwordService = passwordService;  // Assign PasswordService
        logger.info("New RegistrationController");
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUserHandler(@RequestBody UserDto userDto) throws RegistrationFailedException {
        User user = new User(userDto);

        // Ensure salt and hashed password are generated and set
        String plainPassword = userDto.getPassword();
        String salt = passwordService.generateSalt();
        String hashedPassword = passwordService.hashPassword(plainPassword, salt);

        user.setSalt(salt);
        user.setHashedPassword(hashedPassword);

        registrationService.register(user, plainPassword);

        return ResponseEntity.ok().body(user);
    }
    @PostMapping("login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginDTO loginDTO) {
        User user = registrationService.validateLogin(
                loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            return ResponseEntity.ok()
                    .header("Authorization", tokenUserPair.getKey().toString())
                    .body(new UserDto(user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User does not exist or password is incorrect");
        }
    }

    @PostMapping("validate")
    public ResponseEntity<String> validationHandler(@RequestHeader String authorization) throws LoginException {
        try {
            UUID uuid = UUID.fromString(authorization);
            Optional<User> user = authorizationService.validate(uuid);
            if (user.isPresent()) {
                return ResponseEntity.ok().body(user.get().getUsername());
            } else {
                throw new LoginException();
            }
        } catch (IllegalArgumentException e) {
            throw new LoginException();
        }
    }
}
