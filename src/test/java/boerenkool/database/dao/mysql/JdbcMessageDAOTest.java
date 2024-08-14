package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
//        User testUser = new User("Huurder", "TestUsername", "TestPassword", "TestEmail",
//                "0600000000", "Firstname", "infix", "lastname", 0 );
//         create new message with messageId of 0
//        Message testMessage = new Message(Optional.of(testUser), Optional.of(testUser), LocalDateTime.now(),
//                "subject line", "body text", false, false, false );
//         save new message
//        Optional<Message> testMessageAfterSave = testingJdbcMessageDAO.storeOne(testMessage);
//         check modified messageId in object
//        int messageId = testMessageAfterSave.get().getMessageId();
//        // check message is stored in database, and compare message with original message
//        assertEquals(testMessageAfterSave, testingJdbcMessageDAO.getOneById(messageId));
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
