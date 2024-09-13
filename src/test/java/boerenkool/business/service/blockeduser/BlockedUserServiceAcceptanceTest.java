package boerenkool.business.service.blockeduser;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Voor acceptatietests
@ActiveProfiles("test")
@Transactional
class BlockedUserServiceAcceptanceTest {

    @Autowired
    private BlockedUserService blockedUserService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserCanBlockAnotherUser() {
        // Arrange
        User userBlocking = new User(1, "Huurder", "userBlocking", "hashedPassword", "salt", "email@test.com", "phone", "First", "", "Last", 500);
        User userToBlock = new User(2, "Huurder", "userToBlock", "hashedPassword", "salt", "email2@test.com", "phone2", "First2", "", "Last2", 500);

        userRepository.storeOne(userBlocking);
        userRepository.storeOne(userToBlock);

        // Act
        blockedUserService.blockUser(2, 1);

        // Assert
        List<User> blockedUsers = userRepository.getBlockedUsers(userBlocking);
        assertTrue(blockedUsers.contains(userToBlock));
    }

    @Test
    void testCannotBlockNonExistentUser() {
        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> blockedUserService.blockUser(99, 1));
        assertEquals("User with id 99 not found.", exception.getMessage());
    }
}