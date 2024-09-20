package boerenkool.business.service;

import boerenkool.business.model.HouseType;
import boerenkool.database.repository.HouseTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HouseTypeServiceTest {

    @Mock
    private HouseTypeRepository houseTypeRepository;

    @InjectMocks
    private HouseTypeService houseTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHouseTypes() {
        // Arrange
        List<HouseType> houseTypes = Arrays.asList(
                new HouseType(1, "Type1"),
                new HouseType(2, "Type2")
        );
        when(houseTypeRepository.getAll()).thenReturn(houseTypes);

        // Act
        List<HouseType> result = houseTypeService.getAllHouseTypes();

        // Assert
        assertEquals(2, result.size());
        verify(houseTypeRepository, times(1)).getAll();
    }

    @Test
    void testGetHouseTypeById_Found() {
        // Arrange
        HouseType houseType = new HouseType(1, "Type1");
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.of(houseType));

        // Act
        Optional<HouseType> result = houseTypeService.getHouseTypeById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getHouseTypeId());
        verify(houseTypeRepository, times(1)).getOneById(1);
    }

    @Test
    void testGetHouseTypeById_NotFound() {
        // Arrange
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.empty());

        // Act
        Optional<HouseType> result = houseTypeService.getHouseTypeById(1);

        // Assert
        assertFalse(result.isPresent());
        verify(houseTypeRepository, times(1)).getOneById(1);
    }

    @Test
    void testSaveHouseType_Success() {
        // Arrange
        HouseType houseType = new HouseType("Type1");
        when(houseTypeRepository.findByName("Type1")).thenReturn(Optional.empty());

        // Act
        HouseType savedHouseType = houseTypeService.saveHouseType(houseType);

        // Assert
        assertEquals("Type1", savedHouseType.getHouseTypeName());
        verify(houseTypeRepository, times(1)).storeOne(houseType);
    }

    @Test
    void testSaveHouseType_AlreadyExists() {
        // Arrange
        HouseType houseType = new HouseType("Type1");
        when(houseTypeRepository.findByName("Type1")).thenReturn(Optional.of(houseType));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            houseTypeService.saveHouseType(houseType);
        });
        assertEquals("Een HouseType met deze naam bestaat al.", exception.getMessage());
    }

    @Test
    void testDeleteHouseTypeById_Success() {
        // Arrange
        HouseType houseType = new HouseType(1, "Type1");
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.of(houseType));

        // Act
        boolean result = houseTypeService.deleteHouseTypeById(1);

        // Assert
        assertTrue(result);
        verify(houseTypeRepository, times(1)).removeOneById(1);
    }

    @Test
    void testDeleteHouseTypeById_NotFound() {
        // Arrange
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            houseTypeService.deleteHouseTypeById(1);
        });
        assertEquals("Een HouseType met ID 1 bestaat niet.", exception.getMessage());
    }

    @Test
    void testUpdateHouseType_Success() {
        // Arrange
        HouseType houseType = new HouseType(1, "Type1");
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.of(houseType));
        when(houseTypeRepository.findByName("Type1")).thenReturn(Optional.empty());

        // Mocking updateOne to return true
        when(houseTypeRepository.updateOne(any(HouseType.class))).thenReturn(true);

        // Act
        boolean result = houseTypeService.updateHouseType(houseType);

        // Assert
        assertTrue(result);
    }


    @Test
    void testUpdateHouseType_NameExists() {
        // Arrange
        HouseType existingHouseType = new HouseType(2, "Type1");
        HouseType houseTypeToUpdate = new HouseType(1, "Type1");
        when(houseTypeRepository.getOneById(1)).thenReturn(Optional.of(houseTypeToUpdate));
        when(houseTypeRepository.findByName("Type1")).thenReturn(Optional.of(existingHouseType));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            houseTypeService.updateHouseType(houseTypeToUpdate);
        });
        assertEquals("Een andere HouseType met deze naam bestaat al.", exception.getMessage());
    }
}
