package boerenkool.business.service.blockeduser;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Volledige Spring context inclusief H2 database
@Transactional // Zorgt ervoor dat database-transacties worden teruggedraaid na elke test
class BlockedUserServiceIntegrationTest {

    @Autowired
    private BlockedUserService blockedUserService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testBlockUser_Integration_Success() {
        // Arrange: We voegen testdata toe aan de H2-database
        User userBlocking = new User(1, "Huurder", "userBlocking", "hashedPassword", "salt", "email@test.com", "phone", "First", "", "Last", 500);
        User userToBlock = new User(2, "Huurder", "userToBlock", "hashedPassword", "salt", "email2@test.com", "phone2", "First2", "", "Last2", 500);

        userRepository.storeOne(userBlocking);
        userRepository.storeOne(userToBlock);

        // Act: We roepen de service aan om de gebruiker te blokkeren
        blockedUserService.blockUser(2, 1);

        // Assert: We controleren of de gebruiker daadwerkelijk geblokkeerd is
        List<User> blockedUsers = userRepository.getBlockedUsers(userBlocking);
        assertTrue(blockedUsers.contains(userToBlock));
    }

    @Test
    void testBlockUser_Integration_UserNotFound() {
        // Act & Assert: Probeer een niet-bestaande gebruiker te blokkeren en controleer of de juiste exceptie wordt gegooid
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> blockedUserService.blockUser(99, 1));
        assertEquals("User with id 99 not found.", exception.getMessage());
    }

    @Test
    void testUnblockUser_Integration_Success() {
        // Arrange: We voegen testdata toe en blokkeren een gebruiker
        User userBlocking = new User(1, "Huurder", "userBlocking", "hashedPassword", "salt", "email@test.com", "phone", "First", "", "Last", 500);
        User userToBlock = new User(2, "Huurder", "userToBlock", "hashedPassword", "salt", "email2@test.com", "phone2", "First2", "", "Last2", 500);

        userRepository.storeOne(userBlocking);
        userRepository.storeOne(userToBlock);

        blockedUserService.blockUser(2, 1); // Blokkeer de gebruiker

        // Act: We deblokkeren de gebruiker
        blockedUserService.unblockUser(2, 1);

        // Assert: Controleer of de gebruiker niet langer geblokkeerd is
        List<User> blockedUsers = userRepository.getBlockedUsers(userBlocking);
        assertFalse(blockedUsers.contains(userToBlock));
    }
}
