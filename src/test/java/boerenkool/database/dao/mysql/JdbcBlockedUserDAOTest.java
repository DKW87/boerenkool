//package boerenkool.database.dao.mysql;
//
//import boerenkool.business.model.User;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class JdbcBlockedUserDAOTest {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private BlockedUserDAO instanceUnderTest;
//
//    // Testgebruikers
//    private final User leo = new User("verhuurder", "kurpie", "ww", "leo@email.nl", "123456", "leo", null, "kurpershoek", 500);
//    private final User julee = new User("huurder", "julee", "ww", "julee@email.nl", "12345678", "julee", null, "blom", 200);
//
//    // Geblokkeerde gebruikersrelatie
//    private final BlockedUser blockedRelation = new BlockedUser(leo, julee);
//
//    @BeforeAll
//    public void setUp() {
//        // Instantieer de DAO die we gaan testen, met gebruik van JdbcTemplate en JdbcUserDAO
//        JdbcUserDAO jdbcUserDAO = new JdbcUserDAO(jdbcTemplate);
//        this.instanceUnderTest = new JdbcBlockedUserDAO(jdbcTemplate, jdbcUserDAO);
//    }
//
//    @Test
//    void addBlockedUser_test() {
//        // Test het toevoegen van een geblokkeerde gebruikersrelatie in de database
//
//        // Controleer eerst dat de relatie nog niet in de database bestaat
//        boolean isBlockedBefore = instanceUnderTest.isUserBlocked(leo, julee);
//        Assertions.assertThat(isBlockedBefore).isFalse();
//
//        // Voeg de geblokkeerde relatie toe
//        instanceUnderTest.addBlockedUser(blockedRelation);
//
//        // Controleer of de relatie succesvol is toegevoegd
//        boolean isBlockedAfter = instanceUnderTest.isUserBlocked(leo, julee);
//        Assertions.assertThat(isBlockedAfter).isTrue();
//    }
//
//    @Test
//    void removeBlockedUser_test() {
//        // Test het verwijderen van een geblokkeerde gebruikersrelatie uit de database
//
//        // Voeg eerst de geblokkeerde relatie toe om te testen
//        instanceUnderTest.addBlockedUser(blockedRelation);
//
//        // Verwijder de relatie
//        boolean removed = instanceUnderTest.removeBlockedUser(blockedRelation);
//        Assertions.assertThat(removed).isTrue();
//
//        // Controleer dat de relatie is verwijderd
//        boolean isBlockedAfterRemoval = instanceUnderTest.isUserBlocked(leo, julee);
//        Assertions.assertThat(isBlockedAfterRemoval).isFalse();
//    }
//
//    @Test
//    void isUserBlocked_test() {
//        // Test het controleren of een gebruiker is geblokkeerd door een andere gebruiker
//
//        // Voeg eerst de geblokkeerde relatie toe
//        instanceUnderTest.addBlockedUser(blockedRelation);
//
//        // Controleer dat de gebruiker is geblokkeerd
//        boolean isBlocked = instanceUnderTest.isUserBlocked(leo, julee);
//        Assertions.assertThat(isBlocked).isTrue();
//
//        // Verwijder de relatie en controleer opnieuw
//        instanceUnderTest.removeBlockedUser(blockedRelation);
//        boolean isBlockedAfterRemoval = instanceUnderTest.isUserBlocked(leo, julee);
//        Assertions.assertThat(isBlockedAfterRemoval).isFalse();
//    }
//
//    @Test
//    void getBlockedUsers_test() {
//        // Test het ophalen van alle gebruikers die door een specifieke gebruiker zijn geblokkeerd
//
//        // Voeg eerst de geblokkeerde relatie toe
//        instanceUnderTest.addBlockedUser(blockedRelation);
//
//        // Haal de lijst met geblokkeerde gebruikers op
//        List<User> blockedUsers = instanceUnderTest.getBlockedUsers(julee);
//        Assertions.assertThat(blockedUsers).isNotNull().isNotEmpty().contains(leo);
//
//        // Verwijder de relatie en controleer opnieuw
//        instanceUnderTest.removeBlockedUser(blockedRelation);
//        blockedUsers = instanceUnderTest.getBlockedUsers(julee);
//        Assertions.assertThat(blockedUsers).isNotNull().isEmpty();
//    }
//}
