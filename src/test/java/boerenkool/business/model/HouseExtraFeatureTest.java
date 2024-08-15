package boerenkool.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HouseExtraFeature.
 *
 * @author Emine Sernur YILDIRIM
 */
public class HouseExtraFeatureTest {

    private HouseExtraFeature houseExtraFeature;

    @BeforeEach
    public void setUp() {
        houseExtraFeature = new HouseExtraFeature(101, 11);
    }

    @Test
    public void testGetHouseId() {
        assertEquals(101, houseExtraFeature.getHouseId());
    }

    @Test
    public void testSetHouseId() {
        houseExtraFeature.setHouseId(202);
        assertEquals(202, houseExtraFeature.getHouseId());
    }

    @Test
    public void testGetFeatureId() {
        assertEquals(11, houseExtraFeature.getFeatureId());
    }

    @Test
    public void testSetFeatureId() {
        houseExtraFeature.setFeatureId(22);
        assertEquals(22, houseExtraFeature.getFeatureId());
    }
}
