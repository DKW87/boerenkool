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
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return true;
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public Optional<User> getOneById(int id) {
        Optional<User> user = userRepository.getOneById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return user;
    }

    public boolean updateOne(User user) {
        boolean updated = userRepository.updateOne(user);
        if (!updated) {
            throw new UserUpdateFailedException("Failed to update user with id " + user.getUserId());
        }
        return true;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username '" + username + "' not found.");
        }
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean updateBoerenkoolcoins(User user, int additionalCoins) {
        int currentCoins = Optional.ofNullable(user.getCoinBalance()).orElse(0);
        int newCoins = user.getCoinBalance() + additionalCoins;
        return userRepository.updateBoerenkoolcoins(user, newCoins);
    }

    public Optional<String> getUsernameById(int id) {
        return userRepository.getUsernameById(id);
    }

}
