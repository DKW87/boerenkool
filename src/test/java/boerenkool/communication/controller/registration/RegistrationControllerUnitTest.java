package boerenkool.communication.controller.registration;

import boerenkool.business.model.User;
import boerenkool.business.service.LoginAttemptService;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.controller.RegistrationController;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.PasswordResetDto;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.authorization.TokenUserPair;
import boerenkool.utilities.exceptions.LoginException;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationControllerUnitTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Normale tests
    @Test
    void testRegisterUserHandler_Success() throws RegistrationFailedException {
        // Arrange
        UserDto userDto = new UserDto();
        User user = new User();
        when(registrationService.register(any(UserDto.class))).thenReturn(user);

        // Act
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registratie succesvol!", response.getBody());
    }

    @Test
    void testRegisterUserHandler_Fail() throws RegistrationFailedException {
        // Arrange
        UserDto userDto = new UserDto();
        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam bestaat al."));

        // Act
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam bestaat al.", response.getBody());
    }

    @Test
    void testLoginHandler_Success() throws LoginException {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("password");
        User user = new User();
        TokenUserPair tokenUserPair = new TokenUserPair(UUID.randomUUID(), user);
        when(registrationService.validateLogin(anyString(), anyString())).thenReturn(user);
        when(authorizationService.authorize(any(User.class))).thenReturn(tokenUserPair);

        // Act
        ResponseEntity<UserDto> response = registrationController.loginHandler(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Authorization"));
        assertNotNull(response.getBody());
    }

    @Test
    void testLoginHandler_Fail() throws LoginException {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("wrongPassword");
        when(registrationService.validateLogin(anyString(), anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(LoginException.class, () -> registrationController.loginHandler(loginDTO));
    }

    // Gevaarlijke tests
    @Test
    void testRegisterUserHandler_MissingUsername_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        // Arrange
        UserDto userDto = new UserDto(); // Username ontbreekt
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam is verplicht"));

        // Act
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam is verplicht", response.getBody());
    }

    @Test
    void testRegisterUserHandler_InvalidEmail_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("invalid-email"); // Ongeldig e-mailadres
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Ongeldig e-mailadres"));

        // Act
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ongeldig e-mailadres", response.getBody());
    }

    @Test
    void testLoginHandler_UserBlocked_ShouldReturnForbidden() throws LoginException {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("blockedUser");
        loginDTO.setPassword("password");

        when(loginAttemptService.isBlocked(loginDTO.getUsername())).thenReturn(true); // Gebruiker is geblokkeerd

        // Act
        ResponseEntity<UserDto> response = registrationController.loginHandler(loginDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testResetPassword_EmptyToken_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        // Arrange
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setToken(null); // Geen token opgegeven
        passwordResetDto.setEmail("test@example.com");
        passwordResetDto.setNewPassword("newPassword");

        when(registrationService.resetPassword(any(PasswordResetDto.class))).thenThrow(new RegistrationFailedException("Token is verplicht"));

        // Act
        ResponseEntity<String> response = registrationController.confirmPasswordReset(passwordResetDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token is verplicht", response.getBody());
    }

    @Test
    void testResetPassword_InvalidToken_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        // Arrange
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setToken("invalid-token"); // Ongeldig token
        passwordResetDto.setEmail("test@example.com");
        passwordResetDto.setNewPassword("newPassword");

        when(registrationService.resetPassword(any(PasswordResetDto.class))).thenThrow(new RegistrationFailedException("Ongeldig tokenformaat"));

        // Act
        ResponseEntity<String> response = registrationController.confirmPasswordReset(passwordResetDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Ongeldig tokenformaat", response.getBody());
    }

    @Test
    void testLoginHandler_SQLInjectionAttempt_ShouldFailLogin() throws LoginException {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin' OR 1=1 --");
        loginDTO.setPassword("password");

        when(registrationService.validateLogin(anyString(), anyString())).thenReturn(null); // Simuleer dat de gebruiker niet gevonden wordt

        // Act & Assert
        assertThrows(LoginException.class, () -> registrationController.loginHandler(loginDTO));
    }

    @Test
    void testRegisterUserHandler_TooLongUsername_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        // Arrange
        String longUsername = "a".repeat(256); // 256 karakters lang
        UserDto userDto = new UserDto();
        userDto.setUsername(longUsername);
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam is te lang"));

        // Act
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam is te lang", response.getBody());
    }
}
