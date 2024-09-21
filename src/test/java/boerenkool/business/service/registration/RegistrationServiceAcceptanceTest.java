package boerenkool.business.service.registration;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationServiceAcceptanceTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterUser_Acceptance_Success() throws RegistrationFailedException {
        // Arrange: Create a new user DTO
        UserDto userDto = new UserDto();
        userDto.setUsername("acceptanceTestUser");
        userDto.setEmail("acceptance@test.com");
        userDto.setPassword("Password123!");

        // Act: Register the user
        registrationService.register(userDto);

        // Assert: Verify if the user is registered (for example, check the repository or service method)
        Optional<User> registeredUserOpt = userRepository.findByUsername("acceptanceTestUser");
        assertTrue(registeredUserOpt.isPresent(), "User should be present");
        User registeredUser = registeredUserOpt.get(); // Get the actual User object from Optional
        assertEquals("acceptanceTestUser", registeredUser.getUsername());
        assertEquals("acceptance@test.com", registeredUser.getEmail());
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
