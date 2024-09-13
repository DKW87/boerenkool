package boerenkool.business.model;

import boerenkool.communication.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Voor elke test maken we een nieuwe gebruiker aan
        user = new User(1, "Huurder", "testUser", "hashedPassword", "salt", "test@example.com", "1234567890", "John", "", "Doe", 500);
    }

    @Test
    void testDefaultConstructor() {
        // Test default constructor
        User defaultUser = new User();
        assertEquals(0, defaultUser.getUserId());
        assertEquals("", defaultUser.getUsername());
        assertEquals(500, defaultUser.getCoinBalance());
    }

    @Test
    void testConstructorWithDto() {
        // Test constructor met een UserDto
        UserDto dto = new UserDto();
        dto.setUsername("newUser");
        dto.setTypeOfUser("Huurder");
        dto.setEmail("new@example.com");
        dto.setPhone("9876543210");
        dto.setFirstName("Jane");
        dto.setInfix("");
        dto.setLastName("Doe");
        dto.setCoinBalance(1000);

        User userFromDto = new User(dto, "hashedPassword", "salt");
        assertEquals("newUser", userFromDto.getUsername());
        assertEquals("Huurder", userFromDto.getTypeOfUser());
        assertEquals(1000, userFromDto.getCoinBalance());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("newUsername");
        assertEquals("newUsername", user.getUsername());
    }

    @Test
    void testEquals() {
        User anotherUser = new User(1, "Huurder", "testUser", "hashedPassword", "salt", "test@example.com", "1234567890", "John", "", "Doe", 500);
        assertTrue(user.equals(anotherUser));
    }

    @Test
    void testCoinBalance() {
        user.setCoinBalance(600);
        assertEquals(600, user.getCoinBalance());
    }
}
