package boerenkool.business.service;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.repository.HouseExtraFeatureRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class HouseExtraFeatureServiceTest {

    @Mock
    private HouseExtraFeatureRepository houseExtraFeatureRepository;

    @InjectMocks
    private HouseExtraFeatureService houseExtraFeatureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHouseExtraFeatures() {
        // Arrange
        List<HouseExtraFeature> houseExtraFeatures = Arrays.asList(
                new HouseExtraFeature(1, 1),
                new HouseExtraFeature(2, 2)
        );
        when(houseExtraFeatureRepository.getAll()).thenReturn(houseExtraFeatures);

        // Act
        List<HouseExtraFeature> result = houseExtraFeatureService.getAllHouseExtraFeatures();

        // Assert
        assertEquals(2, result.size());
        verify(houseExtraFeatureRepository, times(1)).getAll();
    }

    @Test
    void testGetHouseExtraFeatureByIds_Found() {
        // Arrange
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.of(feature));

        // Act
        Optional<HouseExtraFeature> result = houseExtraFeatureService.getHouseExtraFeatureByIds(1, 1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getHouseId());
        verify(houseExtraFeatureRepository, times(1)).getOneByIds(1, 1);
    }

    @Test
    void testGetHouseExtraFeatureByIds_NotFound() {
        // Arrange
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.empty());

        // Act
        Optional<HouseExtraFeature> result = houseExtraFeatureService.getHouseExtraFeatureByIds(1, 1);

        // Assert
        assertFalse(result.isPresent());
        verify(houseExtraFeatureRepository, times(1)).getOneByIds(1, 1);
    }

    @Test
    void testSaveHouseExtraFeature_Success() {
        // Arrange
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.empty());

        // Act
        HouseExtraFeature savedFeature = houseExtraFeatureService.saveHouseExtraFeature(feature);

        // Assert
        assertEquals(1, savedFeature.getHouseId());
        verify(houseExtraFeatureRepository, times(1)).storeOne(feature);
    }

    @Test
    void testSaveHouseExtraFeature_AlreadyExists() {
        // Arrange
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.of(feature));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            houseExtraFeatureService.saveHouseExtraFeature(feature);
        });
        assertEquals("Een HouseExtraFeature met deze houseId en featureId bestaat al.", exception.getMessage());
    }

    @Test
    void testDeleteHouseExtraFeatureByIds_Success() {
        // Arrange
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.of(feature));

        // Act
        boolean result = houseExtraFeatureService.deleteHouseExtraFeatureByIds(1, 1);

        // Assert
        assertTrue(result);
        verify(houseExtraFeatureRepository, times(1)).removeOneByIds(1, 1);
    }

    @Test
    void testDeleteHouseExtraFeatureByIds_NotFound() {
        // Arrange
        when(houseExtraFeatureRepository.getOneByIds(1, 1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            houseExtraFeatureService.deleteHouseExtraFeatureByIds(1, 1);
        });
        assertEquals("Een HouseExtraFeature met houseId 1 en featureId 1 bestaat niet.", exception.getMessage());
    }
}
