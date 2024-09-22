package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@author Timothy Houweling
@project Boerenkool
 */

@ActiveProfiles("test")
@JdbcTest
@Import({JdbcPictureDAO.class, JdbcHouseDAO.class})
class JdbcPictureDAOTest {

    private final byte[] VALID_PICTURE_SIZE = new byte[1024 * 1024];

    @Autowired
    private JdbcPictureDAO jdbcPictureDAO;

    @Autowired
    private JdbcHouseDAO jdbcHouseDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS Picture;");
        jdbcTemplate.execute("DROP TABLE IF EXISTS House;");

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

        jdbcTemplate.execute("DROP TABLE IF EXISTS Picture;" +
                "CREATE TABLE Picture (" +
                "pictureId INT AUTO_INCREMENT PRIMARY KEY, " +
                "houseId INT," +
                "picture BLOB," +
                "pictureDescription VARCHAR(255)," +
                "FOREIGN KEY (houseId) REFERENCES House(houseId))");

        byte[] pictureData = new byte[]{0, 1, 2};
        jdbcTemplate.update("INSERT INTO Picture (houseId, picture, pictureDescription) VALUES (?, ?, ?)",
                1, pictureData, "A beautiful picture");

    }

    @Test
    void test_getAllByHouseId() {
        List<Picture> pictures = jdbcPictureDAO.getAllByHouseId(1);
        assertNotNull(pictures);
        assertEquals(1, pictures.size());
        assertEquals("A beautiful picture", pictures.get(0).getDescription());
    }

    @Test
    void test_getOneById() {
        Optional<Picture> picture = jdbcPictureDAO.getOneById(1);
        assertNotNull(picture);
        assertEquals(1, picture.get().getPictureId());

    }

    @Test
    void test_storeOne() {
        House house = jdbcHouseDAO.getAll().getFirst();

        int houseId = house.getHouseId();

        Picture newPicture = new Picture(house, VALID_PICTURE_SIZE, "A new beautiful picture");

        jdbcPictureDAO.storeOne(newPicture);

        List<Picture> pictures = jdbcPictureDAO.getAllByHouseId(houseId);
        assertEquals(2, pictures.size());
        assertEquals("A new beautiful picture", pictures.get(1).getDescription());

    }

    @Test
    void test_removeOneById() {
        Optional<Picture> picture = jdbcPictureDAO.getOneById(1);
        assertTrue(picture.isPresent());

        jdbcPictureDAO.removeOneById(picture.get().getPictureId());

        Optional<Picture> deletedPicture = jdbcPictureDAO.getOneById(picture.get().getPictureId());
        assertEquals(Optional.empty(), deletedPicture);

    }
}
