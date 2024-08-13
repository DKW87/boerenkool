package boerenkool.database.dao.mysql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcMessageDAOTest {

//    JdbcTemplate jdbcTemplate;

    private final JdbcMessageDAO testingJdbcMessageDAO;

    @Autowired
    JdbcMessageDAOTest(JdbcTemplate jdbcTemplate) {
        this.testingJdbcMessageDAO = new JdbcMessageDAO(jdbcTemplate);
    }

    @Test
    void storeOne() {
        // save new message
//        testingJdbcMessageDAO.storeOne(null);
        // new message has messageId of 0
        // save message
        // check modified messageId in object
        // check message is stored in database
        // check if stored message is identical to object message
    }

    @Test
    void getOneById() {
        // is this really necessary? successful getOneById follows from other tests
    }

    @Test
    void updateOne_booleans() {
        // test archivedBySender & archivedByReceiver & readByReceiver
        // get message with FALSE boolean
        // change to TRUE
        // update message in database
        // check boolean has been updated

        // get message with TRUE boolean
        // change to FALSE
        // update message in database
        // check boolean has been updated
    }

    @Test
    void getAllForReceiver_noUser() {
        // user doesnt exist
    }

    @Test
    void getAllForReceiver_noMessages() {
        // no messages for user
    }

    @Test
    void getAllForReceiver_oneMessage() {
        // one message for user
    }

    @Test
    void getAllForReceiver_manyMessages() {
        // several messages
    }
}
