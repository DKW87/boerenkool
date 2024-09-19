package boerenkool.business.service.registration;

import boerenkool.business.model.User;
import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.UserRepository;
import boerenkool.utilities.exceptions.RegistrationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegister_Success() throws RegistrationFailedException {
        // Arrange: Create a new UserDto
        UserDto userDto = new UserDto();
        userDto.setUsername("integrationTestUser");
        userDto.setEmail("integrationtest@test.com");
        userDto.setPassword("Password123!");

        // Act: Register the user
        User registeredUser = registrationService.register(userDto);

        // Assert: Check if the user is correctly stored in the database
        assertNotNull(registeredUser);
        assertEquals("integrationTestUser", registeredUser.getUsername());
        assertTrue(userRepository.findByUsername("integrationTestUser").isPresent());
    }

    @Test
    void testRegister_Fail_UsernameExists() {
        // Arrange: Create a UserDto with an existing username
        UserDto userDto = new UserDto();
        userDto.setUsername("existingIntegrationUser");
        userDto.setEmail("userintegration@example.com");
        userDto.setPassword("Password123!");

        // Store the user first to simulate the existing user
        User existingUser = new User(userDto, "hashedPassword", "salt");
        userRepository.storeOne(existingUser);

        // Act & Assert: Attempt to register the same user, should throw an exception
        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () -> registrationService.register(userDto));
        assertEquals("Gebruikersnaam bestaat al.", exception.getMessage());
    }
}
