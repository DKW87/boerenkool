package boerenkool.database.dao.mysql;

import boerenkool.business.model.ExtraFeature;
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
public class JdbcExtraFeatureDAOTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ExtraFeatureDAO instanceUnderTest;

    // Test ExtraFeature-objecten
    private final ExtraFeature feature1 = new ExtraFeature(0, "Feature A");
    private final ExtraFeature feature2 = new ExtraFeature(0, "Feature B");

    @BeforeAll
    public void setUp() {
        this.instanceUnderTest = new JdbcExtraFeatureDAO(jdbcTemplate);
    }

    @Test
    void storeOne_insert_test() {
        // Test het toevoegen van een nieuw ExtraFeature-object

        // Controleer eerst dat het ExtraFeature-object nog niet in de database bestaat
        Optional<ExtraFeature> featureBeforeInsert = instanceUnderTest.findByName(feature1.getExtraFeatureName());
        Assertions.assertThat(featureBeforeInsert.isPresent()).isFalse();

        // Voeg het nieuwe ExtraFeature-object toe
        instanceUnderTest.storeOne(feature1);

        // Controleer of het ExtraFeature-object succesvol is toegevoegd
        Optional<ExtraFeature> featureAfterInsert = instanceUnderTest.findByName(feature1.getExtraFeatureName());
        Assertions.assertThat(featureAfterInsert).isPresent();
        Assertions.assertThat(featureAfterInsert.get().getExtraFeatureName()).isEqualTo(feature1.getExtraFeatureName());
    }

    @Test
    void storeOne_update_test() {
        // Test het bijwerken van een bestaand ExtraFeature-object

        // Voeg eerst een nieuw ExtraFeature-object toe
        instanceUnderTest.storeOne(feature2);

        // Haal het toegevoegde ExtraFeature-object uit de database
        Optional<ExtraFeature> insertedFeature = instanceUnderTest.findByName(feature2.getExtraFeatureName());
        Assertions.assertThat(insertedFeature).isPresent();

        // Werk de naam van het ExtraFeature-object bij
        ExtraFeature featureToUpdate = insertedFeature.get();
        featureToUpdate.setExtraFeatureName("Updated Feature B");

        // Sla het bijgewerkte ExtraFeature-object op
        instanceUnderTest.storeOne(featureToUpdate);

        // Controleer of de update correct is uitgevoerd
        Optional<ExtraFeature> updatedFeature = instanceUnderTest.findByName("Updated Feature B");
        Assertions.assertThat(updatedFeature).isPresent();
        Assertions.assertThat(updatedFeature.get().getExtraFeatureName()).isEqualTo("Updated Feature B");
    }

    @Test
    void getAll_test() {
        // Test het ophalen van alle ExtraFeature-objecten

        // Voeg twee ExtraFeature-objecten toe aan de database
        instanceUnderTest.storeOne(feature1);
        instanceUnderTest.storeOne(feature2);

        // Haal alle ExtraFeature-objecten op
        List<ExtraFeature> features = instanceUnderTest.getAll();

        // Controleer de lijst
        Assertions.assertThat(features).isNotNull().isNotEmpty();
        Assertions.assertThat(features).contains(feature1, feature2);
    }

    @Test
    void getOneById_test() {
        // Test het ophalen van een ExtraFeature-object op basis van ID

        // Voeg een nieuw ExtraFeature-object toe
        instanceUnderTest.storeOne(feature1);

        // Haal het toegevoegde ExtraFeature-object op basis van ID op
        Optional<ExtraFeature> feature = instanceUnderTest.getOneById(feature1.getExtraFeatureId());

        // Controleer of het opgehaalde ExtraFeature-object correct is
        Assertions.assertThat(feature).isPresent();
        Assertions.assertThat(feature.get().getExtraFeatureId()).isEqualTo(feature1.getExtraFeatureId());
    }

    @Test
    void removeOneById_test() {
        // Test het verwijderen van een ExtraFeature-object op basis van ID

        // Voeg een nieuw ExtraFeature-object toe
        instanceUnderTest.storeOne(feature1);

        // Verwijder het toegevoegde ExtraFeature-object op basis van ID
        boolean removed = instanceUnderTest.removeOneById(feature1.getExtraFeatureId());
        Assertions.assertThat(removed).isTrue();

        // Controleer of het ExtraFeature-object daadwerkelijk is verwijderd
        Optional<ExtraFeature> featureAfterDelete = instanceUnderTest.getOneById(feature1.getExtraFeatureId());
        Assertions.assertThat(featureAfterDelete).isNotPresent();
    }
}
