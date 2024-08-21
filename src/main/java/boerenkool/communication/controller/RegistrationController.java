package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.PasswordResetDto;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.LoginException;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthorizationService authorizationService;
    private final PasswordService passwordService;
    private final UserService userService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthorizationService authorizationService, PasswordService passwordService, UserService userService) {
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
        this.passwordService = passwordService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registerUserHandler(@RequestBody UserDto userDto) {
        try {
            User user = registrationService.register(userDto);
            return ResponseEntity.ok("Registratie succesvol!");
        } catch (RegistrationFailedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginHandler(@RequestBody LoginDTO loginDTO) throws LoginException {
        User user = registrationService.validateLogin(
                loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            return ResponseEntity.ok()
                    .header("Authorization", tokenUserPair.getKey().toString())
                    .body(new UserDto(user));
        } else {
            throw new LoginException("Login failed");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validationHandler(@RequestHeader String authorization) throws LoginException {
        try {
            UUID uuid = UUID.fromString(authorization);
            Optional<User> user = authorizationService.validate(uuid);
            if (user.isPresent()) {
                return ResponseEntity.ok().body(user.get().getUsername());
            } else {
                throw new LoginException("Login failed");
            }
        } catch (IllegalArgumentException e) {
            throw new LoginException("Login failed");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            passwordService.sendPasswordResetEmail(email, tokenUserPair.getKey().toString());
            return ResponseEntity.ok("Password reset email sent");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody PasswordResetDto passwordResetDto) {
        Optional<User> userOpt = authorizationService.validate(UUID.fromString(passwordResetDto.getToken()));
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getEmail().equals(passwordResetDto.getEmail())) {
                String salt = passwordService.generateSalt();
                String hashedPassword = passwordService.hashPassword(passwordResetDto.getNewPassword(), salt);
                user.setHashedPassword(hashedPassword);
                user.setSalt(salt);
                userService.updateOne(user);
                return ResponseEntity.ok("Password reset successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or email");
    }
}
