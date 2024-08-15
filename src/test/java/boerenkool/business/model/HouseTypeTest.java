package boerenkool.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HouseType.
 *
 * @author Emine Sernur YILDIRIM
 */
public class HouseTypeTest {

    private HouseType villa;
    private HouseType boerderij;

    @BeforeEach
    public void setUp() {
        villa = new HouseType(1, "Villa");
        boerderij = new HouseType("Boerderij");
    }

    @Test
    public void testGetHouseTypeId() {
        assertEquals(1, villa.getHouseTypeId());
    }

    @Test
    public void testSetHouseTypeId() {
        villa.setHouseTypeId(2);
        assertEquals(2, villa.getHouseTypeId());
    }

    @Test
    public void testGetHouseTypeName() {
        assertEquals("Villa", villa.getHouseTypeName());
        assertEquals("Boerderij", boerderij.getHouseTypeName());
    }

    @Test
    public void testSetHouseTypeName() {
        villa.setHouseTypeName("Mansion");
        assertEquals("Mansion", villa.getHouseTypeName());
    }

    @Test
    public void testToString() {
        assertEquals("Villa", villa.toString());
        assertEquals("Boerderij", boerderij.toString());
    }
}
