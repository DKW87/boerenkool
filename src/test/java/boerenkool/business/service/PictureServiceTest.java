package boerenkool.business.service;
import boerenkool.business.model.Picture;
import boerenkool.business.model.House;
import boerenkool.database.repository.PictureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@author Timothy Houweling
@project Boerenkool
 */

@SpringBootTest
@Transactional // rolls back everything after every test
@AutoConfigureMockMvc // simulates and asserts request responses
class PictureServiceTest {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private PictureRepository pictureRepository;

    private Picture mockPicture;
    private Picture mockPicture2;
    private House mockHouse;

    @BeforeEach
    void setUp() {
        mockHouse = new House("Schuurtje aan de Wallen", "Noord-Holland", "Amsterdam",
                "De Wallen 69", "1234AB", 4, 4, 69, "Mooi uitzicht", false);
        mockHouse.setHouseId(1);

        byte[] validPictureSize = new byte[1024 * 1024]; // 1MB
        byte[] oversizedPicture = new byte[6 * 1024 * 1024]; // 6MB

        mockPicture = new Picture(mockHouse, validPictureSize, "Mooi formaat plaatje 1");
        mockPicture.setPictureId(1);

        mockPicture2 = new Picture(mockHouse, oversizedPicture, "Te groot plaatje 2");
        mockPicture2.setPictureId(2);
    }

    @Test
    @DisplayName("Test savePicture() saves a picture")
    void testSavePicture() {
        boolean saved = pictureService.savePicture(mockPicture);
        assertTrue(saved);

        Optional<Picture> retrievedPicture = pictureRepository.getOneById(mockPicture.getPictureId());
        assertTrue(retrievedPicture.isPresent());
        assertEquals(mockPicture.getDescription(), retrievedPicture.get().getDescription());
    }

    @Test
    @DisplayName("Test getAllByHouseId() retrieves pictures by house ID")
    void testGetAllByHouseId() {
        pictureService.savePicture(mockPicture);
        pictureService.savePicture(mockPicture2);

        List<Picture> pictures = pictureService.getAllByHouseId(mockHouse.getHouseId());
        assertEquals(2, pictures.size());
    }

    @Test
    @DisplayName("Test removeOneById() removes a picture by ID")
    void testRemoveOneById() {
        pictureService.savePicture(mockPicture);
        boolean removed = pictureService.removeOneById(mockPicture.getPictureId());
        assertTrue(removed);

        Optional<Picture> retrievedPicture = pictureRepository.getOneById(mockPicture.getPictureId());
        assertFalse(retrievedPicture.isPresent());
    }

    @Test
    @DisplayName("Test getOneById() retrieves a picture by ID")
    void testGetOneById() {
        pictureService.savePicture(mockPicture);

        Optional<Picture> retrievedPicture = pictureService.getOneById(mockPicture.getPictureId());
        assertTrue(retrievedPicture.isPresent());
        assertEquals(mockPicture.getDescription(), retrievedPicture.get().getDescription());
    }

    @Test
    @DisplayName("Test buildImageResponse() returns the correct response entity")
    void testBuildImageResponse() {
        pictureService.savePicture(mockPicture);
        byte[] imageBytes = new byte[] {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 // Valid JPEG header
        };

        ResponseEntity<byte[]> responseEntity = pictureService.buildImageResponse(imageBytes);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertArrayEquals(imageBytes, responseEntity.getBody());
    }

    @Test
    @DisplayName("Test buildImageResponse() for PNG")
    void testBuildImageResponse_PNG() {
        byte[] imageBytes = new byte[] {
                (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47 // PNG header
        };

        ResponseEntity<byte[]> responseEntity = pictureService.buildImageResponse(imageBytes);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Test buildImageResponse() for GIF")
    void testBuildImageResponse_GIF() {
        byte[] imageBytes = new byte[] {
                (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38 // GIF header
        };

        ResponseEntity<byte[]> responseEntity = pictureService.buildImageResponse(imageBytes);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


}
