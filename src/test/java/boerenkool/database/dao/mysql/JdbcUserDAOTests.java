package boerenkool.database.dao.mysql;

import boerenkool.business.model.User;

import org.assertj.core.api.Assertions;
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

    @Test
    void save_test_2() {
        // This tests saving an existing member in the database;
        // prerequisites: 1. the member alredy exists in the db
        //                2. member.getId() > 0

        // step 1: get an existing meber from the db:
        Optional<User> user_option_1 = instanceUnderTest.getOneById(1);

        // step 2: make sure that you have found a member_1:
        Assertions.assertThat(user_option_1).isNotNull().isNotEmpty();

        // step 3: remember its id, we will need that later:
        User user_1 = user_option_1.get();
        Assertions.assertThat(user_1.getUserId()).isGreaterThan(0);
        int id = user_1.getUserId();

        // step 4: modify the member_1:
        user_1.setUsername("test_name");

        // step 5: save the modified member_1:
        instanceUnderTest.storeOne(user_1);

        // step 6: assert that member_1.getId() has not changed:
        Assertions.assertThat(user_1.getUserId()).isEqualTo(id);

        // step 7: retrieve the member_1 from the datbase:
        Optional<User> user_option_2 = instanceUnderTest.getOneById(id);

        // step 8: verify that it is correct:
        Assertions.assertThat(user_option_2).isNotNull().isNotEmpty();
        User user_2 = user_option_2.get();
        Assertions.assertThat(user_2).isNotNull().isEqualTo(user_1);
    }

    @Test
    void find_by_existing_id_test() {
        Optional<User> actual = instanceUnderTest.getOneById(1);
        Assertions.assertThat(actual).isNotNull().isNotEmpty();
        Assertions.assertThat(actual.get()).isNotNull().isEqualTo(leo);
    }

    @Test
    void find_by_non_existing_id_test() {
        Optional<User> actual = instanceUnderTest.getOneById(10000);
        Assertions.assertThat(actual).isNotNull().isEmpty();
    }



}
