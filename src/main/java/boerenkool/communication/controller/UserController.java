package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        logger.info("New UserController");
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping(value = "/{id}")
    User getOneById(@PathVariable("id") int id) {
        Optional<User> opt = userService.getOneById(id);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateOne(@RequestBody User user, @PathVariable("id") int id) {
        // Controleren of de gebruiker bestaat
        Optional<User> existingUser = userService.getOneById(id);
        if (existingUser.isPresent()) {
            // Zet het ID van de bestaande gebruiker naar het nieuwe object
            user.setUserId(id);
            // Updaten van de gebruiker
            boolean updated = userService.updateOne(user);
            if (updated) {
                return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to update user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createOne(@RequestBody User user) {
        userService.storeOne(user);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable("id") int id) {
        boolean removed = userService.removeOneById(id);
        if (removed) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @GetMapping(value = "/username/{username}")
    User findOneByUsername(@PathVariable("username") String name) {
        Optional<User> opt = userService.findByUsername(name);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}


