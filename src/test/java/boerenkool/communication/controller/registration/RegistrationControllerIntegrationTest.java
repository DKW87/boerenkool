package boerenkool.communication.controller.registration;

import boerenkool.communication.controller.RegistrationController;
import boerenkool.communication.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RegistrationControllerIntegrationTest {

    @Autowired
    private RegistrationController registrationController;

    @Test
    void testRegisterUserIntegration_Success() {
        // Arrange: Create a UserDto object
        UserDto userDto = new UserDto();
        userDto.setUsername("integrationUser");
        userDto.setEmail("integration@test.com");
        userDto.setPassword("Password123!");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setPhone("0612345678");


        // Act: Call the controller method
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert: Check the response status and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registratie succesvol!", response.getBody());
    }

    @Test
    void testRegisterUserIntegration_Fail() {
        // Arrange: Create a UserDto object with an existing username
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("integrationfail@test.com");
        userDto.setPassword("Password123!");
        userDto.setFirstName("Jane");
        userDto.setLastName("Doe");
        userDto.setPhone("0612345678");

        // First, register the user
        registrationController.registerUserHandler(userDto);

        // Act: Try to register the same user again, which should fail
        ResponseEntity<String> response = registrationController.registerUserHandler(userDto);

        // Assert: The response should indicate failure due to duplicate username
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Gebruikersnaam bestaat al.", response.getBody());
    }
}
