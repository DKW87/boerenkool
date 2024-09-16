package boerenkool.communication.controller.registration;

import boerenkool.communication.dto.UserDto;
import boerenkool.business.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@AutoConfigureMockMvc
@Transactional
class RegistrationControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistrationService registrationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registratie succesvol!"));
    }

    @Test
    void testRegisterUser_Fail_UsernameAlreadyExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        registrationService.register(userDto);

        UserDto duplicateUserDto = new UserDto();
        duplicateUserDto.setUsername("existingUser");
        duplicateUserDto.setEmail("test@example.com");
        duplicateUserDto.setPassword("password");

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gebruikersnaam bestaat al."));
    }

    @Test
    void testRegisterUser_InvalidJsonStructure_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"username\":\"testUser\",\"email\":\"test@example.com\",\"password\":\"password\"";

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
