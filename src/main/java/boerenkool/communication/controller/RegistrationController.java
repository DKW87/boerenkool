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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;



@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);


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
    //geef een http respons terug met statuscode, headers en body. in dit geval bevat het een login dto object
    // de jsonbody wordt automatisch omgezet in een login dto
    public ResponseEntity<UserDto> loginHandler(@RequestBody LoginDTO loginDTO) throws LoginException {
        User user = registrationService.validateLogin(
                loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            return ResponseEntity.ok()
                    //haal de waarde van het token op die als key in het pair zit
                    .header("Authorization", tokenUserPair.getKey().toString())
                    .body(new UserDto(user));
        } else {
            throw new LoginException("Login failed");
        }
    }

    //valideer het reeds verkregen autorisatie token
//methode aanroepen wanneer er een post verzoek wordt gestuurd naar deze url
    @PostMapping("/validate")
    //haal waarde uit de oauthorization header en wijs het toe aan authorization paramater (de token)
    public ResponseEntity<String> validationHandler(@RequestHeader String authorization) throws LoginException {
        try {
            //zet header om naar een uuid.
            UUID uuid = UUID.fromString(authorization);
            //roep methode aan om te kijken of het uuid token overeenkomt met bestaande gebruiker.
            Optional<User> user = authorizationService.validate(uuid);
            if (user.isPresent()) {
                //als gebruiker aanwezig is(en dus het token geldig is) retourneer een 200 respons met gebruikersnaam van betrefende gebruiker
                return ResponseEntity.ok().body(user.get().getUsername());
            } else {
                throw new LoginException("Login failed");
            }
        } catch (IllegalArgumentException e) {
            throw new LoginException("Login failed");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email");
        logger.debug("Received password reset request for email: {}", email);
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.debug("User found: {}", user.getUsername());

            TokenUserPair tokenUserPair = authorizationService.authorize(user);
            passwordService.sendPasswordResetEmail(email, tokenUserPair.getKey().toString());
            return ResponseEntity.ok("Password reset email sent");
        }
        logger.warn("User not found for email: {}", email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/reset-password/confirm")
    // zet json body van post verzoek automatisch om in passwordresetdto, dat gegevens bevat voor resetten van password
    public ResponseEntity<String> confirmPasswordReset(@RequestBody PasswordResetDto passwordResetDto) {
        //zet token om naar een uuid object. retourneer user terug als token geldig is.
        Optional<User> userOpt = authorizationService.validate(UUID.fromString(passwordResetDto.getToken()));
        if (userOpt.isPresent()) {
            //haal user object uit optional
            User user = userOpt.get();
            //extra beveiligings maatregel om te kijken of email van gebruiker is gekoppeld aan token
            if (user.getEmail().equals(passwordResetDto.getEmail())) {
                String salt = passwordService.generateSalt();
                String hashedPassword = passwordService.hashPassword(passwordResetDto.getNewPassword(), salt);
                user.setHashedPassword(hashedPassword);
                user.setSalt(salt);
                //stel nieuw wachtwoord in
                userService.updateOne(user);
                return ResponseEntity.ok("Password reset successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or email");
    }
}
