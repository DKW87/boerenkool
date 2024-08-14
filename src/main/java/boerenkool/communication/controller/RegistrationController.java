package boerenkool.communication.controller;

import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import boerenkool.business.model.User;


@RestController
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    RegistrationService registrationService;
    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
        logger.info("New RegistrationController");
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUserHandler (@RequestBody UserDto userDto) throws RegistrationFailedException {
        User user = new User(userDto) ;
        String plainPassword = userDto.getPassword();
        registrationService.register(user, plainPassword);
        return ResponseEntity.ok().body(user);
    }



}
