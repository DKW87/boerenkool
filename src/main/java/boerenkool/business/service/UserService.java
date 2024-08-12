package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.database.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public void storeOne(User user) {userRepository.storeOne(user);}

    public boolean removeOneById(int id) {return userRepository.removeOneById(id);}

    public List<User> getAll() {
        return userRepository.getAll();
    };//Read

    public Optional getOneById(int id) {
        return userRepository.getOneById(id);
    }

    public boolean updateOne(User user) {
        return userRepository.updateOne(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
