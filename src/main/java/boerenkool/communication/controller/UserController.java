package boerenkool.communication.controller;

import boerenkool.business.model.User;
import boerenkool.business.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping(value = "/{id}")
    public User getOneById(@PathVariable("id") int id) {
        return userService.getOneById(id).orElse(null);
    }

    @PutMapping(value = "/{id}")
    public void updateOne(@RequestBody User user, @PathVariable("id") int id) {
        user.setUserId(id);
        userService.updateOne(user);
    }

    @PostMapping
    public void createOne(@RequestBody User user) {
        userService.storeOne(user);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteOne(@PathVariable("id") int id) {
        userService.removeOneById(id);
    }

    @GetMapping(value = "/username/{username}")
    public User findOneByUsername(@PathVariable("username") String name) {
        return userService.findByUsername(name).orElse(null);
    }
}
