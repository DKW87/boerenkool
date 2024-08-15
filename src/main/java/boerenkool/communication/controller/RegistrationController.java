package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUserHandler(@RequestBody UserDto userDto) throws RegistrationFailedException {
        User user = registrationService.register(userDto);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginDTO loginDTO) {
        User user = registrationService.validateLogin(
                loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            // Token autorisatie (niet getoond hier)
            return ResponseEntity.ok().body(new UserDto(user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User does not exist or password is incorrect");
        }
    }
}
