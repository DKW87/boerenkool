package boerenkool.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 11/08/2024 - 09:18
 */
class HouseTest {

    private House house;
    private House house2;
    private HouseType houseType;
    private HouseType houseType2;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        user = new User();
        user2 = new User();
        houseType = new HouseType(1, "Apartment");
        houseType2 = new HouseType(2, "Little-House");
        house = new House("Apartement aan de Wallen", houseType, user, "Noord-Holland", "Amsterdam",
                "De Wallen 69","1234AB", 4, 4, 69,
                "Mooi uitzicht", false);
        house.setHouseId(1);
        house2 = new House("Oude Molen", houseType2, user2, "Zuid-Holland", "Kinderdijk",
                "Molenweg 1","4321BA", 2, 1, 123,
                "Lekker krap, maar uniek", false);
        house2.setHouseId(2);
    }

    @Test
    public void testCompareTo_SameHouseId() {
        House house3 = house;
        assertEquals(0, house.compareTo(house3));
    }

    @Test
    public void testCompareTo_DifferentHouseId() {
        assertTrue(house.compareTo(house2) < 0);
        assertTrue(house2.compareTo(house) > 0);
    }

    @Test
    public void testEquals_SameHouseId() {
        House house3 = house;
        assertTrue(house.equals(house3));
        assertTrue(house3.equals(house));
    }

    @Test
    public void testEquals_DifferentHouseId() {
        assertFalse(house.equals(house2));
        assertFalse(house2.equals(house));
    }

    @Test
    public void testEquals_SameObject() {
        assertTrue(house.equals(house));
    }

    @Test
    public void testEquals_NullObject() {
        assertFalse(house.equals(null));
    }

    @Test
    public void testHashCode_SameHouseId() {
        House house3 = house;
        assertEquals(house.hashCode(), house3.hashCode());
    }

    @Test
    public void testHashCode_DifferentHouseId() {
        assertNotEquals(house.hashCode(), house2.hashCode());
    }

    @Test
    public void testSetProvince_ValidInput() {
        house.setProvince("Zuid-Holland");
        assertEquals("Zuid-Holland", house.getProvince());
    }

    @Test
    public void testSetProvince_InvalidInput() {
        house.setProvince("1234");
        assertEquals("Onbekend", house.getProvince());
    }

    @Test
    public void testSetCity_ValidInput() {
        house.setCity("Utrecht");
        assertEquals("Utrecht", house.getCity());
    }

    @Test
    public void testSetCity_InvalidInput() {
        house.setCity("Rotterdam123");
        assertEquals("Onbekend", house.getCity());
    }

    @Test
    public void testSetPricePPPD_ValidInput() {
        house.setPricePPPD(150);
        assertEquals(150, house.getPricePPPD());
    }

    @Test
    public void testSetPricePPPD_InvalidInput() {
        house.setPricePPPD(-50);
        assertEquals(0, house.getPricePPPD());
    }

    @Test
    public void testSetRoomCount_ValidInput() {
        house.setRoomCount(3);
        assertEquals(3, house.getRoomCount());
    }

    @Test
    public void testSetRoomCount_InvalidInput() {
        house.setRoomCount(-1);
        assertEquals(0, house.getRoomCount());
    }

    @Test
    public void testSetMaxGuest_ValidInput() {
        house.setMaxGuest(6);
        assertEquals(6, house.getMaxGuest());
    }

    @Test
    public void testSetMaxGuest_InvalidInput() {
        house.setMaxGuest(-3);
        assertEquals(0, house.getMaxGuest());
    }

    @Test
    public void testSetZipcode_ValidInput() {
        house.setZipcode("5678CD");
        assertEquals("5678CD", house.getZipcode());
    }

    @Test
    public void testSetZipcode_InvalidInput() {
        house.setZipcode("12AB34");
        assertEquals("0000AA", house.getZipcode());
    }

}