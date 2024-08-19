package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.exceptions.UserNotFoundException;
import boerenkool.utilities.exceptions.UserUpdateFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Autowired
    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
        logger.info("New UserController created");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getOneById(@PathVariable("id") int id) {
        User user = userService.getOneById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        return ResponseEntity.ok(user);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token) {
        Optional<User> userOpt = authorizationService.validate(UUID.fromString(token));
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getUsername().equals(userDto.getUsername())) {
                userService.updateOne(user);
                return ResponseEntity.ok("User updated successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or username mismatch");
    }

  /*  @PostMapping
    public ResponseEntity<Void> createOne(@RequestBody User user) {
        userService.storeOne(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable("id") int id) {
        userService.getOneById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        userService.removeOneById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<User> findOneByUsername(@PathVariable("username") String name) {
        User user = userService.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + name + "' not found."));
        return ResponseEntity.ok(user);
    }
}
