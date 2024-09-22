package boerenkool.business.service;

import boerenkool.business.model.ExtraFeature;
import boerenkool.database.repository.ExtraFeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtraFeatureServiceTest {

    @Mock
    private ExtraFeatureRepository extraFeatureRepository;

    @InjectMocks
    private ExtraFeatureService extraFeatureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllExtraFeatures_ShouldReturnListOfExtraFeatures() {
        // Arrange
        List<ExtraFeature> mockFeatures = Arrays.asList(
                new ExtraFeature(1, "Feature 1"),
                new ExtraFeature(2, "Feature 2")
        );
        when(extraFeatureRepository.getAll()).thenReturn(mockFeatures);

        // Act
        List<ExtraFeature> result = extraFeatureService.getAllExtraFeatures();

        // Assert
        assertEquals(2, result.size());
        verify(extraFeatureRepository, times(1)).getAll();
    }

    @Test
    void getExtraFeatureById_ShouldReturnExtraFeature_WhenFeatureExists() {
        // Arrange
        ExtraFeature mockFeature = new ExtraFeature(1, "Feature 1");
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.of(mockFeature));

        // Act
        Optional<ExtraFeature> result = extraFeatureService.getExtraFeatureById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Feature 1", result.get().getExtraFeatureName());
        verify(extraFeatureRepository, times(1)).getOneById(1);
    }

    @Test
    void getExtraFeatureById_ShouldReturnEmpty_WhenFeatureDoesNotExist() {
        // Arrange
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.empty());

        // Act
        Optional<ExtraFeature> result = extraFeatureService.getExtraFeatureById(1);

        // Assert
        assertFalse(result.isPresent());
        verify(extraFeatureRepository, times(1)).getOneById(1);
    }

    @Test
    void saveExtraFeature_ShouldSaveFeature_WhenFeatureNameIsUnique() {
        // Arrange
        ExtraFeature newFeature = new ExtraFeature("New Feature");
        when(extraFeatureRepository.findByName("New Feature")).thenReturn(Optional.empty());

        // Act
        ExtraFeature result = extraFeatureService.saveExtraFeature(newFeature);

        // Assert
        assertNotNull(result);
        verify(extraFeatureRepository, times(1)).findByName("New Feature");
        verify(extraFeatureRepository, times(1)).storeOne(newFeature);
    }

    @Test
    void saveExtraFeature_ShouldThrowException_WhenFeatureNameExists() {
        // Arrange
        ExtraFeature existingFeature = new ExtraFeature("Existing Feature");
        when(extraFeatureRepository.findByName("Existing Feature")).thenReturn(Optional.of(existingFeature));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            extraFeatureService.saveExtraFeature(existingFeature);
        });

        assertEquals("Een Extra Feature met deze naam bestaat al.", exception.getMessage());
        verify(extraFeatureRepository, times(1)).findByName("Existing Feature");
        verify(extraFeatureRepository, times(0)).storeOne(existingFeature);
    }

    @Test
    void deleteExtraFeatureById_ShouldDeleteFeature_WhenFeatureExists() {
        // Arrange
        ExtraFeature mockFeature = new ExtraFeature(1, "Feature 1");
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.of(mockFeature));

        // Act
        boolean result = extraFeatureService.deleteExtraFeatureById(1);

        // Assert
        assertTrue(result);
        verify(extraFeatureRepository, times(1)).getOneById(1);
        verify(extraFeatureRepository, times(1)).removeOneById(1);
    }

    @Test
    void deleteExtraFeatureById_ShouldThrowException_WhenFeatureDoesNotExist() {
        // Arrange
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            extraFeatureService.deleteExtraFeatureById(1);
        });

        assertEquals("Een ExtraFeature met ID 1 bestaat niet.", exception.getMessage());
        verify(extraFeatureRepository, times(1)).getOneById(1);
        verify(extraFeatureRepository, times(0)).removeOneById(1);
    }

    @Test
    void updateExtraFeature_ShouldUpdateFeature_WhenFeatureExists() {
        // Arrange
        ExtraFeature existingFeature = new ExtraFeature(1, "Existing Feature");
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.of(existingFeature));
        when(extraFeatureRepository.updateOne(existingFeature)).thenReturn(true);  // Mock updateOne to return true

        // Act
        boolean result = extraFeatureService.updateExtraFeature(existingFeature);

        // Assert
        assertTrue(result);  // This checks if updateOne returns true
        verify(extraFeatureRepository, times(1)).getOneById(1);
        verify(extraFeatureRepository, times(1)).updateOne(existingFeature);
    }


    @Test
    void updateExtraFeature_ShouldThrowException_WhenFeatureDoesNotExist() {
        // Arrange
        ExtraFeature nonExistingFeature = new ExtraFeature(1, "Non-existing Feature");
        when(extraFeatureRepository.getOneById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            extraFeatureService.updateExtraFeature(nonExistingFeature);
        });

        assertEquals("Een ExtraFeature met ID 1 bestaat niet.", exception.getMessage());
        verify(extraFeatureRepository, times(1)).getOneById(1);
        verify(extraFeatureRepository, times(0)).updateOne(nonExistingFeature);
    }
}
