package boerenkool.business.service.registration;

import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationServiceAcceptanceTest {

    @Autowired
    private RegistrationService registrationService;

    @Test
    void testRegisterUser_Acceptance_Success() throws RegistrationFailedException {
        // Arrange: Create a new user DTO
        UserDto userDto = new UserDto();
        userDto.setUsername("acceptanceTestUser");
        userDto.setEmail("acceptance@test.com");
        userDto.setPassword("Password123!");

        // Act: Register the user
        registrationService.register(userDto);

        // Assert: If the user was registered without exceptions, the test passes
        assertTrue(true);
    }

    @Test
    void testRegisterUser_Acceptance_Fail_UsernameExists() {
        // Arrange: Create a user DTO with an existing username
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        userDto.setEmail("acceptance@test.com");
        userDto.setPassword("Password123!");

        // First, register the user to ensure the username already exists
        try {
            registrationService.register(userDto);
        } catch (RegistrationFailedException e) {
            // If this fails, it's okay, as the user will already exist
        }

        // Act & Assert: Attempting to register the same user should fail
        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () -> registrationService.register(userDto));
        assertEquals("Gebruikersnaam bestaat al.", exception.getMessage());
    }
}
