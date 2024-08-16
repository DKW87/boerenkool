package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.LoginException;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthorizationService authorizationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthorizationService authorizationService) {
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUserHandler(@RequestBody UserDto userDto) throws RegistrationFailedException {
        User user = registrationService.register(userDto);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("login")
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


    @PostMapping("validate")
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
