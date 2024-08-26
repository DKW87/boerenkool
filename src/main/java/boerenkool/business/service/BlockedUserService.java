package boerenkool.business.service;

import boerenkool.business.model.User;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockedUserService {

    private final UserRepository userRepository;

    @Autowired
    public BlockedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void blockUser(int userToBlockId, int userBlockingId) {
        User userToBlock = userRepository.getOneById(userToBlockId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userToBlockId + " not found."));
        User userBlocking = userRepository.getOneById(userBlockingId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userBlockingId + " not found."));

        userRepository.addBlockedUser(userToBlock, userBlocking);
    }

    public void unblockUser(int userToUnblockId, int userBlockingId) {
        User userToUnblock = userRepository.getOneById(userToUnblockId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userToUnblockId + " not found."));
        User userBlocking = userRepository.getOneById(userBlockingId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userBlockingId + " not found."));

        userRepository.removeBlockedUser(userToUnblock, userBlocking);
    }

    public List<User> getBlockedUsers(int userId) {
        User user = userRepository.getOneById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        return userRepository.getBlockedUsers(user);
    }

    public boolean isUserBlocked(int userToCheckId, int userCheckingId) {
        User userToCheck = userRepository.getOneById(userToCheckId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userToCheckId + " not found."));
        User userChecking = userRepository.getOneById(userCheckingId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userCheckingId + " not found."));

        return userRepository.isUserBlocked(userToCheck, userChecking);
    }
}
