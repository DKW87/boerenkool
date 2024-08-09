package boerenkool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test") // dit betekent dat de application-test.properties gebruikt moet worden
class HuisjeBoompjeBoerenkoolApplicationTests {

    @Autowired
    HuisjeBoompjeBoerenkoolApplication application = null;

    @Test
    void springBootApplicationContextLoadsOk() {
        assertNotNull(application);
        System.out.println("Application auto-configuration has succeeded.");
    }

}
