package boerenkool.communication.controller.registration;

import boerenkool.business.model.User;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationControllerUnitTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserHandler_Success() throws RegistrationFailedException {
        UserDto userDto = new UserDto();
        User user = new User();
        when(registrationService.register(any(UserDto.class))).thenReturn(user);

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registratie succesvol!", response.getBody());
    }

    @Test
    void testRegisterUserHandler_Fail() throws RegistrationFailedException {
        UserDto userDto = new UserDto();
        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam bestaat al."));

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam bestaat al.", response.getBody());
    }

    @Test
    void testLoginHandler_Success() throws LoginException {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("password");
        User user = new User();
        TokenUserPair tokenUserPair = new TokenUserPair(UUID.randomUUID(), user);
        when(registrationService.validateLogin(anyString(), anyString())).thenReturn(user);
        when(authorizationService.authorize(any(User.class))).thenReturn(tokenUserPair);

        ResponseEntity<UserDto> response = registrationController.loginHandler(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Authorization"));
        assertNotNull(response.getBody());
    }

    @Test
    void testLoginHandler_Fail() throws LoginException {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("wrongPassword");
        when(registrationService.validateLogin(anyString(), anyString())).thenReturn(null);

        assertThrows(LoginException.class, () -> registrationController.loginHandler(loginDTO));
    }

    @Test
    void testRegisterUserHandler_MissingUsername_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam is verplicht"));

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam is verplicht", response.getBody());
    }

    @Test
    void testRegisterUserHandler_InvalidEmail_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("invalid-email");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Ongeldig e-mailadres"));

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ongeldig e-mailadres", response.getBody());
    }

    @Test
    void testRegisterUserHandler_TooLongUsername_ShouldThrowRegistrationFailedException() throws RegistrationFailedException {
        String longUsername = "a".repeat(256);
        UserDto userDto = new UserDto();
        userDto.setUsername(longUsername);
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class))).thenThrow(new RegistrationFailedException("Gebruikersnaam is te lang"));

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam is te lang", response.getBody());
    }
}
