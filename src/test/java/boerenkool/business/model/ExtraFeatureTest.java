package boerenkool.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExtraFeature.
 *
 * @author Emine Sernur YILDIRIM
 */
public class ExtraFeatureTest {

    private ExtraFeature huisdierenToegestaan;
    private ExtraFeature sauna;
    private ExtraFeature tuin;
    private ExtraFeature barbecue;

    @BeforeEach
    public void setUp() {
        huisdierenToegestaan = new ExtraFeature(11, "Huisdieren Toegestaan");
        sauna = new ExtraFeature("Sauna");
        tuin = new ExtraFeature("Tuin");
        barbecue = new ExtraFeature("Barbecue");
    }

    @Test
    public void testGetExtraFeatureId() {
        assertEquals(11, huisdierenToegestaan.getExtraFeatureId());
    }

    @Test
    public void testSetExtraFeatureId() {
        huisdierenToegestaan.setExtraFeatureId(12);
        assertEquals(12, huisdierenToegestaan.getExtraFeatureId());
    }

    @Test
    public void testGetExtraFeatureName() {
        assertEquals("Huisdieren Toegestaan", huisdierenToegestaan.getExtraFeatureName());
        assertEquals("Sauna", sauna.getExtraFeatureName());
        assertEquals("Tuin", tuin.getExtraFeatureName());
        assertEquals("Barbecue", barbecue.getExtraFeatureName());
    }

    @Test
    public void testSetExtraFeatureName() {
        sauna.setExtraFeatureName("Jacuzzi");
        assertEquals("Jacuzzi", sauna.getExtraFeatureName());
    }

    @Test
    public void testToString() {
        assertEquals("Huisdieren Toegestaan", huisdierenToegestaan.toString());
        assertEquals("Sauna", sauna.toString());
        assertEquals("Tuin", tuin.toString());
        assertEquals("Barbecue", barbecue.toString());
    }
}
