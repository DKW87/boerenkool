package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.UserNotFoundException;
import boerenkool.utilities.exceptions.UserUpdateFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void storeOne(User user) {
        userRepository.storeOne(user);
    }

    public boolean removeOneById(int id) {
        boolean removed = userRepository.removeOneById(id);
        if (!removed) {
            throw new UserNotFoundException();
        }
        return true;
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public Optional<User> getOneById(int id) {
        return userRepository.getOneById(id);
    }

    public boolean updateOne(User user) {
        boolean updated = userRepository.updateOne(user);
        if (!updated) {
            throw new UserUpdateFailedException();
        }
        return true;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user;
        } else {
            throw new UserNotFoundException();
        }
    }
}
