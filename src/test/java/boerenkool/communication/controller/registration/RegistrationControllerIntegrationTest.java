package boerenkool.communication.controller.registration;

import boerenkool.communication.controller.RegistrationController;
import boerenkool.communication.dto.LoginDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.exceptions.LoginException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RegistrationControllerIntegrationTest {

    @Autowired
    private RegistrationController registrationController;

    @Test
    void testRegisterUserIntegration_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("integrationUser");
        userDto.setEmail("integration@test.com");
        userDto.setPassword("Password123!");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setPhone("0612345678");

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registratie succesvol!", response.getBody());
    }

    @Test
    void testRegisterUserIntegration_Fail() {
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("integrationfail@test.com");
        userDto.setPassword("Password123!");
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setPhone("0612345678");

        registrationController.registerUserHandler(userDto);

        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam bestaat al.", response.getBody());
    }

    @Test
    void testLoginHandler_SQLInjectionAttempt_ShouldPreventSQLInjection() {
        // Arrange: Simuleer SQL-injectie door een verdachte gebruikersnaam te gebruiken
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin' OR 1=1 --");
        loginDTO.setPassword("password");

        // Act & Assert: Verwacht dat de SQL-injectie wordt afgewezen en een mislukte login retourneert
        assertThrows(LoginException.class, () -> {
            registrationController.loginHandler(loginDTO);
        });
    }


}
