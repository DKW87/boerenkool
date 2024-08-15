package boerenkool.communication.controller;

import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import boerenkool.business.model.User;

import javax.security.auth.login.LoginException;


@RestController
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final AuthorizationService authorizationService;
    RegistrationService registrationService;
    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthorizationService authorizationService) {
        this.registrationService = registrationService;
        logger.info("New RegistrationController");
        this.authorizationService = authorizationService;
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUserHandler (@RequestBody UserDto userDto) throws RegistrationFailedException {
        User user = new User(userDto) ;
        String plainPassword = userDto.getPassword();
        registrationService.register(user, plainPassword);
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
            throw new LoginException();
        }
    }
}
