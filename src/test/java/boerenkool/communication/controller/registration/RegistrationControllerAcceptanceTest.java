package boerenkool.communication.controller.registration;

import boerenkool.business.model.User;
import boerenkool.communication.dto.UserDto;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.controller.RegistrationController;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Zorgt ervoor dat MockMvc beschikbaar is
@Transactional // Zorgt ervoor dat databaseacties worden teruggedraaid na elke test
class RegistrationControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationService registrationService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Geen mocks nodig, we gebruiken echte services
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registratie succesvol!"));
    }

    @Test
    void testRegisterUser_Fail_UsernameAlreadyExists() throws Exception {
        // Arrange: Eerst een gebruiker registreren
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        registrationService.register(userDto); // Eerste registratie

        // Probeer dezelfde gebruiker opnieuw te registreren
        UserDto duplicateUserDto = new UserDto();
        duplicateUserDto.setUsername("existingUser");
        duplicateUserDto.setEmail("test@example.com");
        duplicateUserDto.setPassword("password");

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUserDto)))
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

        // Act & Assert
        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wachtwoord is verplicht."));
    }
}
