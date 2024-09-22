package boerenkool.business.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
@author Timothy Houweling
@project Boerenkool
 */

class PictureTest {

    private House mockHouse;
    private Picture mockPicture;
    private Picture mockPicture2;
    private final byte[] VALID_PICTURE_SIZE = new byte[1024 * 1024]; // 1MB
    private final byte[] OVERSIZED_PICTURE = new byte[6 * 1024 * 1024]; // 6MB


    @BeforeEach
    void setUp() {
        mockHouse = new House("Schuurtje aan de Wallen", "Noord-Holland", "Amsterdam",
                "De Wallen 69","1234AB", 4, 4, 69,
                "Mooi uitzicht", false);
        mockHouse.setHouseId(1);

        mockPicture = new Picture(mockHouse, VALID_PICTURE_SIZE, "Mooi formaat plaatje 1");
        mockPicture.setPictureId(1);
        mockPicture2 = new Picture(mockHouse, OVERSIZED_PICTURE, "Te groot plaatje 2");
        mockPicture2.setPictureId(2);

    }

    @Test
    @DisplayName("Test toString() returns correct string representation")
    void testToString_ReturnsCorrectStringRepresentation() {
        assertEquals("Picture id:" + mockPicture.getPictureId(),mockPicture.toString());
    }

    @Test
    @DisplayName("Test getPictureId() returns the correct picture ID")
    void getPictureId_ReturnsCorrectPictureId() {
        int actualPictureId = mockPicture.getPictureId();
        assertEquals(1, actualPictureId);
    }

    @Test
    @DisplayName("Test setPictureId() updates the picture ID")
    void setPictureId_UpdatesPictureId() {
        mockPicture.setPictureId(3);
        int actualPictureId = mockPicture.getPictureId();
        assertEquals(3, actualPictureId);
    }

    @Test
    @DisplayName("Test getHouse() returns the correct House object")
    void getHouse_ReturnsCorrectHouse() {
        House actualHouse = mockPicture.getHouse();
        assertEquals(mockHouse, actualHouse);
    }

    @Test
    @DisplayName("Test setHouse() updates the House object")
    void setHouse_UpdatesHouse() {
        House newHouse = new House("Villa aan het strand", "Zuid-Holland", "Den Haag",
                "Strandweg 12", "2345CD", 5, 5, 150, "Strandzicht", true);
        mockPicture.setHouse(newHouse);
        assertEquals(newHouse, mockPicture.getHouse());
    }

    @Test
    @DisplayName("Test getPicture() returns the correct byte array")
    void getPicture_ReturnsCorrectByteArray() {
        byte[] actualPicture = mockPicture.getPicture();
        assertArrayEquals(VALID_PICTURE_SIZE, actualPicture);
    }

    @Test
    @DisplayName("Test setPicture() updates the byte array")
    void setPicture_UpdatesByteArray() {
        byte[] newPicture = new byte[2048 * 2048]; // 4MB
        mockPicture.setPicture(newPicture);
        assertArrayEquals(newPicture, mockPicture.getPicture());
    }

    @Test
    @DisplayName("Test getDescription() returns the correct description")
    void getDescription_ReturnsCorrectDescription() {
        String actualDescription = mockPicture.getDescription();
        assertEquals("Mooi formaat plaatje 1", actualDescription);
    }

    @Test
    @DisplayName("Test setDescription() updates the description")
    void setDescription_UpdatesDescription() {
        mockPicture.setDescription("Nieuw plaatje");
        assertEquals("Nieuw plaatje", mockPicture.getDescription());
    }
}