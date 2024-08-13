package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.UserService;
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
    public ResponseEntity<?> getOneById(@PathVariable("id") int id) {
        try {
            Optional<User> user = userService.getOneById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateOne(@RequestBody User user, @PathVariable("id") int id) {
        try {
            user.setUserId(id);
            userService.updateOne(user);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserUpdateFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createOne(@RequestBody User user) {
        userService.storeOne(user);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable("id") int id) {
        try {
            userService.removeOneById(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<?> findOneByUsername(@PathVariable("username") String name) {
        try {
            User user = userService.findByUsername(name);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
