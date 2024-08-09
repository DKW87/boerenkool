package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Date;

@SpringBootTest
@ActiveProfiles("test")
public class JdbcMessageDAOTest {
    private final JdbcMessageDAO jdbcMessageDAO;

//    User testUser = new User("Huurder", "TestUsername", "TestPassword", "TestEmail",
//            "0600000000", "Firstname", "infix", "lastname", 0 );
//    Date testDate = new Date();
//    OffsetDateTime offsetDateTime = OffsetDateTime.now();
//    Message testMessage = new Message(testUser, testUser,"subject line", "body text" );

    @Autowired
    public JdbcMessageDAOTest(JdbcTemplate jdbcTemplate, UserDAO userDAO) {
        super();
        this.jdbcMessageDAO = new JdbcMessageDAO(jdbcTemplate, userDAO);
    }

    @Test
    void contextLoads() {
        System.out.println("JdbcMessageDAOTest contextLoads");
    }

    @Test
    void storeOne() {
        // save new message
//        messageDAO.storeOne(testMessage);
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
        // no messages
    }

    @Test
    void getAllForReceiver_noMessages() {
        // no messages
    }

    @Test
    void getAllForReceiver_oneMessage() {
        // one message
    }

    @Test
    void getAllForReceiver_manyMessages() {
        // several messages
    }
}
