package boerenkool.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PictureTest {

    private House mockHouse;
    private byte[] validPictureSize;
    private byte[] oversizedPicture;

    @BeforeEach
    void setUp() {

        mockHouse = new House();
        mockHouse.setHouseId(1);

        // 1MB
        validPictureSize = new byte[1024 * 1024];

        // 6MB
        oversizedPicture = new byte[6 * 1024 * 1024];
    }

    @Test
    void testToString() {

    }

    @Test
    void getPictureId() {
    }

    @Test
    void setPictureId() {
    }

    @Test
    void getHouse() {
    }

    @Test
    void setHouse() {
    }

    @Test
    void getHouseId() {
    }

    @Test
    void setHouseId() {
    }

    @Test
    void getPicture() {
    }

    @Test
    void setPicture() {
    }

    @Test
    void getDescription() {
    }

    @Test
    void setDescription() {
    }
}