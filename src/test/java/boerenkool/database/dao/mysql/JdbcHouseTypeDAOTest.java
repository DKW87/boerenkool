package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcHouseTypeDAOTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private HouseTypeDAO instanceUnderTest;

    // Test HouseType-objecten
    private final HouseType type1 = new HouseType(0, "Apartment");
    private final HouseType type2 = new HouseType(0, "Villa");

    @BeforeAll
    public void setUp() {
        this.instanceUnderTest = new JdbcHouseTypeDAO(jdbcTemplate);
    }

    @Test
    void storeOne_insert_test() {
        // Test het toevoegen van een nieuw HouseType-object

        // Controleer eerst dat het HouseType-object nog niet in de database bestaat
        Optional<HouseType> typeBeforeInsert = instanceUnderTest.findByName(type1.getHouseTypeName());
        Assertions.assertThat(typeBeforeInsert.isPresent()).isFalse();

        // Voeg het nieuwe HouseType-object toe
        instanceUnderTest.storeOne(type1);

        // Controleer of het HouseType-object succesvol is toegevoegd
        Optional<HouseType> typeAfterInsert = instanceUnderTest.findByName(type1.getHouseTypeName());
        Assertions.assertThat(typeAfterInsert).isPresent();
        Assertions.assertThat(typeAfterInsert.get().getHouseTypeName()).isEqualTo(type1.getHouseTypeName());
    }

    @Test
    void storeOne_update_test() {
        // Test het bijwerken van een bestaand HouseType-object

        // Voeg eerst een nieuw HouseType-object toe
        instanceUnderTest.storeOne(type2);

        // Haal het toegevoegde HouseType-object uit de database
        Optional<HouseType> insertedType = instanceUnderTest.findByName(type2.getHouseTypeName());
        Assertions.assertThat(insertedType).isPresent();

        // Werk de naam van het HouseType-object bij
        HouseType typeToUpdate = insertedType.get();
        typeToUpdate.setHouseTypeName("Updated Villa");

        // Sla het bijgewerkte HouseType-object op
        instanceUnderTest.storeOne(typeToUpdate);

        // Controleer of de update correct is uitgevoerd
        Optional<HouseType> updatedType = instanceUnderTest.findByName("Updated Villa");
        Assertions.assertThat(updatedType).isPresent();
        Assertions.assertThat(updatedType.get().getHouseTypeName()).isEqualTo("Updated Villa");
    }

    @Test
    void getAll_test() {
        // Test het ophalen van alle HouseType-objecten

        // Voeg twee HouseType-objecten toe aan de database
        instanceUnderTest.storeOne(type1);
        instanceUnderTest.storeOne(type2);

        // Haal alle HouseType-objecten op
        List<HouseType> types = instanceUnderTest.getAll();

        // Controleer de lijst
        Assertions.assertThat(types).isNotNull().isNotEmpty();
        Assertions.assertThat(types).contains(type1, type2);
    }

    @Test
    void getOneById_test() {
        // Test het ophalen van een HouseType-object op basis van ID

        // Voeg een nieuw HouseType-object toe
        instanceUnderTest.storeOne(type1);

        // Haal het toegevoegde HouseType-object op basis van ID op
        Optional<HouseType> type = instanceUnderTest.getOneById(type1.getHouseTypeId());

        // Controleer of het opgehaalde HouseType-object correct is
        Assertions.assertThat(type).isPresent();
        Assertions.assertThat(type.get().getHouseTypeId()).isEqualTo(type1.getHouseTypeId());
    }

    @Test
    void removeOneById_test() {
        // Test het verwijderen van een HouseType-object op basis van ID

        // Voeg een nieuw HouseType-object toe
        instanceUnderTest.storeOne(type1);

        // Verwijder het toegevoegde HouseType-object op basis van ID
        boolean removed = instanceUnderTest.removeOneById(type1.getHouseTypeId());
        Assertions.assertThat(removed).isTrue();

        // Controleer of het HouseType-object daadwerkelijk is verwijderd
        Optional<HouseType> typeAfterDelete = instanceUnderTest.getOneById(type1.getHouseTypeId());
        Assertions.assertThat(typeAfterDelete).isNotPresent();
    }
}
