package boerenkool;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.JdbcMessageDAO;

import java.time.OffsetDateTime;
import java.util.Date;
import java.time.LocalDateTime;

public class LauncherBart {
    public static void main(String[] args) {
        JdbcMessageDAO jdbcMessageDAO;
        User testUser = new User("Huurder", "TestUsername", "TestPassword", "TestEmail",
                "0600000000", "Firstname", "infix", "lastname", 0 );
        Date testDate = new Date();
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        Message testMessage = new Message(testUser, testUser,"subject line", "body text" );

        System.out.println(testMessage);
    }
}
