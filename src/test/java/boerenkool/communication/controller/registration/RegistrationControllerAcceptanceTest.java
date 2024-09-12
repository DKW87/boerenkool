package boerenkool.communication.controller.registration;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.controller.RegistrationController;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistrationControllerAcceptanceTest {

    private MockMvc mockMvc;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private ObjectMapper objectMapper = new ObjectMapper(); // Geen @Autowired meer

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User registeredUser = new User();
        registeredUser.setUsername("testUser");

        when(registrationService.register(any(UserDto.class))).thenReturn(registeredUser);

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registratie succesvol!"));
    }

    @Test
    void testRegisterUser_Fail_UsernameAlreadyExists() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class)))
                .thenThrow(new RegistrationFailedException("Gebruikersnaam bestaat al."));

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gebruikersnaam bestaat al."));
    }

    @Test
    void testRegisterUser_EmptyUsername_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername(""); // Lege gebruikersnaam
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class)))
                .thenThrow(new RegistrationFailedException("Gebruikersnaam is verplicht."));

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gebruikersnaam is verplicht."));
    }

    @Test
    void testRegisterUser_InvalidJsonStructure_ShouldReturnBadRequest() throws Exception {
        // Ongeldige JSON: ontbrekend sluitingshaakje
        String invalidJson = "{\"username\":\"testUser\",\"email\":\"test@example.com\",\"password\":\"password\"";

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUser_TooLongUsername_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String longUsername = "a".repeat(300); // Een extreem lange gebruikersnaam
        UserDto userDto = new UserDto();
        userDto.setUsername(longUsername);
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        when(registrationService.register(any(UserDto.class)))
                .thenThrow(new RegistrationFailedException("Gebruikersnaam is te lang."));

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gebruikersnaam is te lang."));
    }

    @Test
    void testRegisterUser_MissingPassword_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        // Wachtwoord ontbreekt

        when(registrationService.register(any(UserDto.class)))
                .thenThrow(new RegistrationFailedException("Wachtwoord is verplicht."));

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wachtwoord is verplicht."));
    }

}
