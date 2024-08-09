package boerenkool.database.dao.mysql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class JdbcMessageDAOTests {
    @Test
    void contextLoads() {
        System.out.println("JdbcMessageDAOTests contextLoads");
    }
}
