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

    // Testgebruikers die nog niet in de database staan
    private final User leo = new User("verhuurder", "kurpie", "ww", "leo@email.nl", "123456", "leo", null, "kurpershoek", 500);
    private final User julee = new User("huurder", "julee", "ww", "julee@email.nl", "12345678", "julee", null, "blom", 200);

    @BeforeAll
    public void setUp() {
        // Instantieer de DAO die we gaan testen, met gebruik van JdbcTemplate
        this.instanceUnderTest = new JdbcUserDAO(jdbcTemplate);
    }

    @Test
    void save_test_1(){
        /* Test het opslaan van een NIEUWE gebruiker in de database
         * Voorwaarden:
         * 1. De gebruiker bestaat nog niet in de database
         * 2. user.getId() == 0 */

        // Controleer eerst dat de gebruiker 'leo' nog niet in de database bestaat
        Optional<User> memberOption1 = instanceUnderTest.findByUsername(leo.getUsername());
        assertThat(memberOption1.isPresent()).isFalse();

        // Sla de nieuwe gebruiker op
        instanceUnderTest.storeOne(leo);

        // Controleer of de gebruiker succesvol is opgeslagen
        memberOption1 = instanceUnderTest.findByUsername(leo.getUsername());
        assertThat(memberOption1).isPresent();
        assertThat(memberOption1.get().getUsername()).isEqualTo(leo.getUsername());

        // Herhaal hetzelfde proces voor de gebruiker 'julee'
        Optional<User> memberOption2 = instanceUnderTest.findByUsername(julee.getUsername());
        assertThat(memberOption2.isPresent()).isFalse();

        instanceUnderTest.storeOne(julee);

        memberOption2 = instanceUnderTest.findByUsername(julee.getUsername());
        assertThat(memberOption2).isPresent();
        assertThat(memberOption2.get().getUsername()).isEqualTo(julee.getUsername());
    }

    @Test
    void save_test_2() {
        // Test het opslaan van een bestaande gebruiker in de database
        // Voorwaarden: 1. De gebruiker bestaat al in de database
        //              2. user.getId() > 0

        // Stap 1: Haal een bestaande gebruiker 'leo' op uit de database
        Optional<User> user_option_1 = instanceUnderTest.getOneById(1);

        // Stap 2: Zorg ervoor dat je de gebruiker hebt gevonden
        Assertions.assertThat(user_option_1).isNotNull().isNotEmpty();

        // Stap 3: Onthoud het id van de gebruiker, dit hebben we later nodig
        User user_1 = user_option_1.get();
        Assertions.assertThat(user_1.getUserId()).isGreaterThan(0);
        int id = user_1.getUserId();

        // Stap 4: Wijzig de gebruikersnaam van de gebruiker
        user_1.setUsername("test_name");

        // Stap 5: Sla de gewijzigde gebruiker op
        instanceUnderTest.storeOne(user_1);

        // Stap 6: Controleer dat het id van de gebruiker niet is veranderd
        Assertions.assertThat(user_1.getUserId()).isEqualTo(id);

        // Stap 7: Haal de gebruiker opnieuw op uit de database
        Optional<User> user_option_2 = instanceUnderTest.getOneById(id);

        // Stap 8: Controleer dat de wijziging correct is opgeslagen
        Assertions.assertThat(user_option_2).isNotNull().isNotEmpty();
        User user_2 = user_option_2.get();
        Assertions.assertThat(user_2).isNotNull().isEqualTo(user_1);

        // Herhaal hetzelfde proces voor de gebruiker 'julee'
        Optional<User> user_option_3 = instanceUnderTest.getOneById(2);

        Assertions.assertThat(user_option_3).isNotNull().isNotEmpty();

        User user_3 = user_option_3.get();
        Assertions.assertThat(user_3.getUserId()).isGreaterThan(0);
        int juleeId = user_3.getUserId();

        user_3.setUsername("test_name_julee");

        instanceUnderTest.storeOne(user_3);

        Assertions.assertThat(user_3.getUserId()).isEqualTo(juleeId);

        Optional<User> user_option_4 = instanceUnderTest.getOneById(juleeId);

        Assertions.assertThat(user_option_4).isNotNull().isNotEmpty();
        User user_4 = user_option_4.get();
        Assertions.assertThat(user_4).isNotNull().isEqualTo(user_3);
    }

    @Test
    void find_by_existing_id_test() {
        // Test het vinden van een bestaande gebruiker op basis van ID

        // Controleer of 'leo' gevonden kan worden met ID 1
        Optional<User> actualLeo = instanceUnderTest.getOneById(1);
        Assertions.assertThat(actualLeo).isNotNull().isNotEmpty();
        Assertions.assertThat(actualLeo.get()).isNotNull().isEqualTo(leo);

        // Controleer of 'julee' gevonden kan worden met ID 2
        Optional<User> actualJulee = instanceUnderTest.getOneById(2);
        Assertions.assertThat(actualJulee).isNotNull().isNotEmpty();
        Assertions.assertThat(actualJulee.get()).isNotNull().isEqualTo(julee);
    }

    @Test
    void find_by_non_existing_id_test() {
        // Test het zoeken naar een niet-bestaande gebruiker op basis van ID
        // Verwacht dat de zoekopdracht leeg terugkomt

        Optional<User> actualNonExisting = instanceUnderTest.getOneById(10000);
        Assertions.assertThat(actualNonExisting).isNotNull().isEmpty();
    }
}
