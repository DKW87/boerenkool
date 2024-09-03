package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.LoginAttemptService;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.PasswordResetDto;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.LoginException;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;
    private final AuthorizationService authorizationService;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthorizationService authorizationService, LoginAttemptService loginAttemptService) {
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
        this.loginAttemptService = loginAttemptService;
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
        String username = loginDTO.getUsername();

        // Check if the user is currently locked out
        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null); // Or return a custom message indicating the lockout
        }

        try {
            User user = registrationService.validateLogin(username, loginDTO.getPassword());

            if (user != null) {
                // Reset the login attempts on successful login
                loginAttemptService.loginSucceeded(username);

                TokenUserPair tokenUserPair = authorizationService.authorize(user);
                return ResponseEntity.ok()
                        .header("Authorization", tokenUserPair.getKey().toString())
                        .body(new UserDto(user));
            } else {
                // Increment the failed login attempts
                loginAttemptService.loginFailed(username);
                throw new LoginException("Login failed");
            }
        } catch (LoginException e) {
            throw e;
        }
    }

    @GetMapping("/username")
    public ResponseEntity<?> getUsername(@RequestParam UUID token) {
        Optional<String> username = authorizationService.getUsernameByToken(token);
        if (username.isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("username", username.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found or expired");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email");
        logger.debug("Received password reset request for email: {}", email);
        registrationService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody PasswordResetDto passwordResetDto) throws RegistrationFailedException {
        boolean success = registrationService.resetPassword(passwordResetDto);
        if (success) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or email");
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
}
