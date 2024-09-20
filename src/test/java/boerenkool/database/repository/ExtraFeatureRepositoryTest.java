package boerenkool.database.repository;

import boerenkool.business.model.ExtraFeature;
import boerenkool.database.dao.mysql.ExtraFeatureDAO;
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

class ExtraFeatureRepositoryTest {

    @InjectMocks
    private ExtraFeatureRepository extraFeatureRepository;

    @Mock
    private ExtraFeatureDAO extraFeatureDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeOne_ShouldCallDAOStoreOne() {
        ExtraFeature extraFeature = new ExtraFeature("Swimming Pool");

        extraFeatureRepository.storeOne(extraFeature);

        verify(extraFeatureDAO, times(1)).storeOne(extraFeature);
    }

    @Test
    void removeOneById_ShouldCallDAORemoveOneById() {
        int id = 1;

        extraFeatureRepository.removeOneById(id);

        verify(extraFeatureDAO, times(1)).removeOneById(id);
    }

    @Test
    void getAll_ShouldReturnAllExtraFeatures() {
        List<ExtraFeature> features = Arrays.asList(
                new ExtraFeature(1, "Swimming Pool"),
                new ExtraFeature(2, "Garden")
        );
        when(extraFeatureDAO.getAll()).thenReturn(features);

        List<ExtraFeature> result = extraFeatureRepository.getAll();

        assertEquals(2, result.size());
        assertEquals("Swimming Pool", result.get(0).getExtraFeatureName());
        verify(extraFeatureDAO, times(1)).getAll();
    }

    @Test
    void getOneById_ShouldReturnExtraFeature_WhenFound() {
        ExtraFeature feature = new ExtraFeature(1, "Swimming Pool");
        when(extraFeatureDAO.getOneById(1)).thenReturn(Optional.of(feature));

        Optional<ExtraFeature> result = extraFeatureRepository.getOneById(1);

        assertTrue(result.isPresent());
        assertEquals("Swimming Pool", result.get().getExtraFeatureName());
        verify(extraFeatureDAO, times(1)).getOneById(1);
    }

    @Test
    void getOneById_ShouldReturnEmpty_WhenNotFound() {
        when(extraFeatureDAO.getOneById(1)).thenReturn(Optional.empty());

        Optional<ExtraFeature> result = extraFeatureRepository.getOneById(1);

        assertFalse(result.isPresent());
        verify(extraFeatureDAO, times(1)).getOneById(1);
    }

    @Test
    void updateOne_ShouldReturnTrue_WhenUpdateSucceeds() {
        ExtraFeature feature = new ExtraFeature(1, "Updated Feature");
        when(extraFeatureDAO.updateOne(feature)).thenReturn(true);

        boolean result = extraFeatureRepository.updateOne(feature);

        assertTrue(result);
        verify(extraFeatureDAO, times(1)).updateOne(feature);
    }

    @Test
    void updateOne_ShouldReturnFalse_WhenUpdateFails() {
        ExtraFeature feature = new ExtraFeature(1, "Updated Feature");
        when(extraFeatureDAO.updateOne(feature)).thenReturn(false);

        boolean result = extraFeatureRepository.updateOne(feature);

        assertFalse(result);
        verify(extraFeatureDAO, times(1)).updateOne(feature);
    }

    @Test
    void findByName_ShouldReturnExtraFeature_WhenFound() {
        ExtraFeature feature = new ExtraFeature(1, "Swimming Pool");
        when(extraFeatureDAO.findByName("Swimming Pool")).thenReturn(Optional.of(feature));

        Optional<ExtraFeature> result = extraFeatureRepository.findByName("Swimming Pool");

        assertTrue(result.isPresent());
        assertEquals("Swimming Pool", result.get().getExtraFeatureName());
        verify(extraFeatureDAO, times(1)).findByName("Swimming Pool");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotFound() {
        when(extraFeatureDAO.findByName("Swimming Pool")).thenReturn(Optional.empty());

        Optional<ExtraFeature> result = extraFeatureRepository.findByName("Swimming Pool");

        assertFalse(result.isPresent());
        verify(extraFeatureDAO, times(1)).findByName("Swimming Pool");
    }
}
