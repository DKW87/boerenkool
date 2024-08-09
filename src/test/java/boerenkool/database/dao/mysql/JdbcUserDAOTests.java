package boerenkool.database.dao.mysql;

import boerenkool.business.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcUserDAOTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDAO instanceUnderTest;

    //users not existing in database
    private final User leo = new User("verhuurder", "kurpie", "ww", "leo@email.nl", "123456", "leo", null, "kurpershoek", 500);
    private final User julee = new User("huurder", "julee", "ww", "julee@email.nl", "12345678", "julee", null, "blom", 200);

    @BeforeAll
    public void setUp() {
        this.instanceUnderTest = new JdbcUserDAO(jdbcTemplate);
    }

    @Test
    void save_test_1(){
        /* This test saves a NEW member in the db
         * Prerequisites
         * 1. The member doesn't already exist in db
         * 2. member.getId == 0 */

        // First, ensure the user doesn't exist in the database
        Optional<User> memberOption1 = instanceUnderTest.findByUsername(leo.getUsername());
        assertThat(memberOption1.isPresent()).isFalse();

        // Save the new user
        instanceUnderTest.storeOne(leo);

        // Verify the user has been saved
        memberOption1 = instanceUnderTest.findByUsername(leo.getUsername());
        assertThat(memberOption1).isPresent();
        assertThat(memberOption1.get().getUsername()).isEqualTo(leo.getUsername());
    }
}
