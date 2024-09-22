package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import boerenkool.business.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 18/09/2024 - 13:01
 */
@ActiveProfiles("test")
@JdbcTest
@Import(JdbcHouseDAO.class)
class JdbcHouseDAOTest {

    @Autowired
    private JdbcHouseDAO jdbcHouseDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS House;" +
                "CREATE TABLE House (" +
                "houseId INT AUTO_INCREMENT PRIMARY KEY, " +
                "houseName VARCHAR(150), " +
                "houseTypeId INT," +
                "houseOwnerId INT," +
                "province VARCHAR(150), " +
                "city VARCHAR(100), " +
                "streetAndNumber VARCHAR(250)," +
                "zipcode VARCHAR(6)," +
                "maxGuest INT, " +
                "roomCount INT, " +
                "pricePPPD INT," +
                "description VARCHAR(2550)," +
                "isNotAvailable TINYINT)");

        jdbcTemplate.execute("INSERT INTO House (houseName, houseTypeId, houseOwnerId, province, city, " +
                "streetAndNumber, zipcode, maxGuest, roomCount, pricePPPD, description, isNotavailable) VALUES " +
                "('Melkhuis', '3', '1', 'Friesland', 'Appelscha', '7387sadad', '1111dd', '1', '1', '1', 'dsahdgajhdgadgas', '0')," +
                "('Schaaphuis', '2', '1', 'Friesland', 'Leeuwarden', '7387sadad', '1111dd', '1', '1', '1', 'dsahdgajhdgadgas', '0')," +
                "('Kippenhok', '1', '1', 'Friesland', 'Heerenveen', '7387sadad', '1111dd', '1', '1', '1', 'dsahdgajhdgadgas', '0')," +
                "('Kaasboerderij', '4', '1', 'Friesland', 'Wolvega', '7387sadad', '1111dd', '1', '1', '1', 'dsahdgajhdgadgas', '0')," +
                "('Varkensstal', '5', '1', 'Friesland', 'Heerenveen', '7387sadad', '1111dd', '1', '1', '1', 'dsahdgajhdgadgas', '0')");
    }

    @Test
    void test_getHousesByOwner() {
        List<House> houses = jdbcHouseDAO.getHousesByOwner(1);
        assertNotNull(houses);
        assertEquals(5, houses.size());
    }

    @Test
    void test_getHousesByFilter() {
        HouseFilter filter = new HouseFilter.Builder()
                .setCities(Arrays.asList("Heerenveen"))
                .build();

        List<House> houses = jdbcHouseDAO.getHousesByFilter(filter);
        assertNotNull(houses);
        assertEquals(2, houses.size());
        assertEquals("Kippenhok", houses.get(0).getHouseName());
        assertEquals("Varkensstal", houses.get(1).getHouseName());
    }

    @Test
    void test_countHousesByFilter() {
        HouseFilter filter = new HouseFilter.Builder()
                .setCities(Arrays.asList("Heerenveen"))
                .build();

        int count = jdbcHouseDAO.countHousesByFilter(filter);
        assertEquals(2, count);
    }

    @Test
    void test_getOneById() {
        House house = jdbcHouseDAO.getOneById(1).get();
        assertNotNull(house);
        assertEquals("Melkhuis", house.getHouseName());
    }

    @Test
    void test_getUniqueCities() {
        List<String> cities = jdbcHouseDAO.getUniqueCities();
        assertNotNull(cities);
        assertEquals(4, cities.size());
        assertTrue(cities.contains("Appelscha"));
        assertTrue(cities.contains("Wolvega"));
        assertTrue(cities.contains("Heerenveen"));
        assertTrue(cities.contains("Leeuwarden"));
    }

    @Test
    void test_storeOne() {
        House newHouse = new House("Flatje", "Utrecht", "Utrecht", "Straat 1", "1234AB", 4, 2, 60, "Heel mooi huisje", false);

        HouseType houseType = new HouseType(1, "Schuur");
        newHouse.setHouseType(houseType);

        User user = new User();
        user.setUserId(1);
        user.setUsername("Henk");
        newHouse.setHouseOwner(user);

        jdbcHouseDAO.storeOne(newHouse);

        House retrievedHouse = jdbcHouseDAO.getOneById(newHouse.getHouseId()).get();
        assertNotNull(retrievedHouse);
        assertEquals("Flatje", retrievedHouse.getHouseName());
    }

    @Test
    void test_updateOne() {
        House houseToUpdate = jdbcHouseDAO.getOneById(1).get();
        houseToUpdate.setHouseName("Vreetschuur");

        HouseType houseType = new HouseType(1, "Schuur");
        houseToUpdate.setHouseType(houseType);

        User user = new User();
        user.setUserId(1);
        user.setUsername("Henk");
        houseToUpdate.setHouseOwner(user);

        jdbcHouseDAO.updateOne(houseToUpdate);

        House updatedHouse = jdbcHouseDAO.getOneById(1).get();
        assertEquals("Vreetschuur", updatedHouse.getHouseName());
    }

    @Test
    void test_removeOneById() {
        jdbcHouseDAO.removeOneById(1);
        assertThrows(NoSuchElementException.class, () -> jdbcHouseDAO.getOneById(1).get());
    }
}