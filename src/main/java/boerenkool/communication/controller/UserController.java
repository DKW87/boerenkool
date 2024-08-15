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

@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
                .orElseThrow(UserNotFoundException::new);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateOne(@RequestBody User user, @PathVariable("id") int id) {
        userService.getOneById(id).orElseThrow(UserNotFoundException::new);
        try {
            user.setUserId(id);
            userService.updateOne(user);
        } catch (Exception e) {
            throw new UserUpdateFailedException();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createOne(@RequestBody User user) {
        userService.storeOne(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable("id") int id) {
        //zelfde als: () -> new UserNotFoundException().
        userService.getOneById(id).orElseThrow(UserNotFoundException::new);
        userService.removeOneById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<User> findOneByUsername(@PathVariable("username") String name) {
        User user = userService.findByUsername(name)
                //zelfde als: () -> new UserNotFoundException().
                .orElseThrow(UserNotFoundException::new);
        return ResponseEntity.ok(user);
    }
}
