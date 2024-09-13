package boerenkool.business.service.registration;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.PasswordService;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegistrationServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private RegistrationService registrationService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("Password123!");
    }

    @Test
    void testRegister_Success() throws RegistrationFailedException {
        // Arrange: Mocking repository and service behavior
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordService.generateSalt()).thenReturn("salt");
        when(passwordService.hashPassword(anyString(), anyString())).thenReturn("hashedPassword");

        // Act
        User user = registrationService.register(userDto);

        // Assert
        assertNotNull(user);
        verify(userRepository).storeOne(any(User.class)); // Verifies if storeOne was called
    }

    @Test
    void testRegister_Fail_UsernameExists() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        // Act & Assert
        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () -> registrationService.register(userDto));
        assertEquals("Gebruikersnaam bestaat al.", exception.getMessage());
    }

    @Test
    void testValidateLogin_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setSalt("salt");
        user.setHashedPassword("hashedPassword");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordService.hashPassword(eq("Password123!"), eq("salt"))).thenReturn("hashedPassword");

        // Act
        User result = registrationService.validateLogin("testUser", "Password123!");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testValidateLogin_Fail_WrongPassword() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setSalt("salt");
        user.setHashedPassword("hashedPassword");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordService.hashPassword(eq("wrongPassword"), eq("salt"))).thenReturn("wrongHashedPassword");

        // Act
        User result = registrationService.validateLogin("testUser", "wrongPassword");

        // Assert
        assertNull(result);
    }

    @Test
    void testRegisterUser_MissingFirstName_ShouldThrowValidationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("Password123!");
        userDto.setFirstName(null);  // Voornaam ontbreekt

        // Act & Assert
        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                registrationService.register(userDto));
        assertEquals("Voornaam is verplicht", exception.getMessage());
    }

    @Test
    void testRegisterUser_MissingPassword_ShouldThrowValidationException() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword(null);  // Wachtwoord ontbreekt
        userDto.setFirstName("Test");

        // Act & Assert
        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                registrationService.register(userDto));
        assertEquals("Wachtwoord is verplicht", exception.getMessage());
    }


}
