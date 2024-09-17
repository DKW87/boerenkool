package boerenkool.business.service.blockeduser;

import boerenkool.business.model.User;
import boerenkool.business.service.BlockedUserService;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockedUserServiceTest {

    @Mock // Mock voor unit tests
    private UserRepository userRepository;

    @InjectMocks // Injecteer gemockte objecten in de service voor unit tests
    private BlockedUserService blockedUserService;

    private User userToBlock;
    private User userBlocking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Open Mockito mocks

        userToBlock = new User();
        userToBlock.setUserId(1);
        userToBlock.setUsername("userToBlock");

        userBlocking = new User();
        userBlocking.setUserId(2);
        userBlocking.setUsername("userBlocking");
    }

    // UNIT TESTS

    @Test
    void testBlockUser_Success() {
        // Unit Test: Succesvol blokkeren van een gebruiker
        when(userRepository.getOneById(1)).thenReturn(Optional.of(userToBlock));
        when(userRepository.getOneById(2)).thenReturn(Optional.of(userBlocking));

        blockedUserService.blockUser(1, 2);

        verify(userRepository).addBlockedUser(userToBlock, userBlocking);
    }

    @Test
    void testBlockUser_UserNotFound() {
        // Unit Test: Gebruiker niet gevonden bij blokkeren
        when(userRepository.getOneById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                blockedUserService.blockUser(1, 2));
        assertEquals("User with id 1 not found.", exception.getMessage());

        verify(userRepository, never()).addBlockedUser(any(User.class), any(User.class));
    }

    @Test
    void testUnblockUser_Success() {
        // Unit Test: Succesvol deblokkeren van een gebruiker
        when(userRepository.getOneById(1)).thenReturn(Optional.of(userToBlock));
        when(userRepository.getOneById(2)).thenReturn(Optional.of(userBlocking));

        blockedUserService.unblockUser(1, 2);

        verify(userRepository).removeBlockedUser(userToBlock, userBlocking);
    }

    @Test
    void testBlockUser_NonExistentUser_ShouldThrowUserNotFoundException() {
        // Unit Test: Gebruiker bestaat niet bij blokkeren
        int nonExistentUserId = 999;  // Niet-bestaande gebruikers-ID
        when(userRepository.getOneById(nonExistentUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                blockedUserService.blockUser(nonExistentUserId, 2));
        assertEquals("User with id 999 not found.", exception.getMessage());

        verify(userRepository, never()).addBlockedUser(any(User.class), any(User.class));
    }

    @Test
    void testUnblockUser_UserNotFound() {
        // Unit Test: Gebruiker niet gevonden bij deblokkeren
        when(userRepository.getOneById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                blockedUserService.unblockUser(1, 2));
        assertEquals("User with id 1 not found.", exception.getMessage());
/*controleert dat de methode removeBlockedUser nooit is aangeroepen op het userRepository.
 Dit is belangrijk omdat als de gebruiker niet bestaat, de poging om deze te deblokkeren niet zou moeten plaatsvinden.*/
        verify(userRepository, never()).removeBlockedUser(any(User.class), any(User.class));
    }

    @Test
    void testBlockUser_NullUserId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> blockedUserService.blockUser(-1, 2));
    }

    @Test
    void testBlockUser_EmptyBlockedList_ShouldStillBlockUser() {
        // Arrange
        User userToBlock = new User();
        userToBlock.setUserId(1);
        userToBlock.setBlockedUser(new ArrayList<>()); // Lege lijst

        User userBlocking = new User();
        userBlocking.setUserId(2);

        when(userRepository.getOneById(1)).thenReturn(Optional.of(userToBlock));
        when(userRepository.getOneById(2)).thenReturn(Optional.of(userBlocking));

        // Act
        blockedUserService.blockUser(1, 2);

        // Assert
        verify(userRepository).addBlockedUser(userToBlock, userBlocking);
    }


}
