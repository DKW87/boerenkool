package boerenkool.business.model;

import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 11/08/2024 - 09:58
 */
class HouseFilterTest {

    @Test
    public void testHouseFilterBuilder_FullConfiguration() {
        List<String> provinces = Arrays.asList("Noord-Holland", "Zuid-Holland");
        List<String> cities = Arrays.asList("Amsterdam", "Rotterdam");
        List<HouseType> houseTypes = Arrays.asList(new HouseType(1, "Villa"),
                new HouseType(2, "Krot"));
        int houseOwnerId = 1;
        int amountOfGuests = 4;
        int desiredRoomCount = 3;
        int minPricePPPD = 50;
        int maxPricePPPD = 200;
        String sortBy = "houseId";
        String sortOrder = "ASC";
        int limit = 10;
        int offset = 20;

        HouseFilter filter = new HouseFilter.Builder()
                .setProvinces(provinces)
                .setCities(cities)
                .setHouseTypes(houseTypes)
                .setHouseOwner(houseOwnerId)
                .setAmountOfGuests(amountOfGuests)
                .setDesiredRoomCount(desiredRoomCount)
                .setMinPricePPPD(minPricePPPD)
                .setMaxPricePPPD(maxPricePPPD)
                .setSortBy(sortBy)
                .setSortOrder(sortOrder)
                .setLimit(limit)
                .setOffset(offset)
                .build();

        assertEquals(provinces, filter.getProvinces());
        assertEquals(cities, filter.getCities());
        assertEquals(houseTypes, filter.getHouseTypes());
        assertEquals(houseOwnerId, filter.getHouseOwnerId());
        assertEquals(amountOfGuests, filter.getAmountOfGuests());
        assertEquals(desiredRoomCount, filter.getDesiredRoomCount());
        assertEquals(minPricePPPD, filter.getMinPricePPPD());
        assertEquals(maxPricePPPD, filter.getMaxPricePPPD());
        assertEquals(sortBy, filter.getSortBy());
        assertEquals(sortOrder, filter.getSortOrder());
        assertEquals(limit, filter.getLimit());
        assertEquals(offset, filter.getOffset());
    }

    @Test
    public void testHouseFilterBuilder_MinimalConfiguration() {
        HouseFilter filter = new HouseFilter.Builder()
                .setProvinces(Arrays.asList("Noord-Holland"))
                .setCities(Arrays.asList("Amsterdam"))
                .setSortOrder("DESC")
                .build();

        assertEquals(Arrays.asList("Noord-Holland"), filter.getProvinces());
        assertEquals(Arrays.asList("Amsterdam"), filter.getCities());
        assertNull(filter.getHouseTypes());
        assertEquals(0, filter.getHouseOwnerId());
        assertEquals(0, filter.getAmountOfGuests());
        assertEquals(0, filter.getDesiredRoomCount());
        assertEquals(0, filter.getMinPricePPPD());
        assertEquals(0, filter.getMaxPricePPPD());
        assertNull(filter.getSortBy());
        assertEquals("DESC", filter.getSortOrder());
        assertEquals(0, filter.getLimit());
        assertEquals(0, filter.getOffset());
    }

    @Test
    public void testHouseFilterBuilder_DefaultValues() {
        HouseFilter filter = new HouseFilter.Builder().build();

        assertNull(filter.getProvinces());
        assertNull(filter.getCities());
        assertNull(filter.getHouseTypes());
        assertEquals(0, filter.getHouseOwnerId());
        assertEquals(0, filter.getAmountOfGuests());
        assertEquals(0, filter.getDesiredRoomCount());
        assertEquals(0, filter.getMinPricePPPD());
        assertEquals(0, filter.getMaxPricePPPD());
        assertNull(filter.getSortBy());
        assertNull(filter.getSortOrder());
        assertEquals(0, filter.getLimit());
        assertEquals(0, filter.getOffset());
    }

    @Test
    public void testHouseFilterBuilder_IncorrectValues() {
        HouseFilter filter = new HouseFilter.Builder()
                .setAmountOfGuests(-10)
                .setDesiredRoomCount(-10)
                .setMinPricePPPD(-999)
                .setMaxPricePPPD(-1234)
                .setSortOrder("abcdefg")
                .setLimit(-15)
                .setOffset(-100)
                .build();

        assertNull(filter.getProvinces());
        assertNull(filter.getCities());
        assertNull(filter.getHouseTypes());
        assertEquals(0, filter.getHouseOwnerId());
        assertEquals(0, filter.getAmountOfGuests());
        assertEquals(0, filter.getDesiredRoomCount());
        assertEquals(0, filter.getMinPricePPPD());
        assertEquals(0, filter.getMaxPricePPPD());
        assertNull(filter.getSortBy());
        assertNull(filter.getSortOrder());
        assertEquals(0, filter.getLimit());
        assertEquals(0, filter.getOffset());
    }

}