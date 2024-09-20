package boerenkool.database.repository;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.mysql.HouseExtraFeatureDAO;
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

class HouseExtraFeatureRepositoryTest {

    @InjectMocks
    private HouseExtraFeatureRepository houseExtraFeatureRepository;

    @Mock
    private HouseExtraFeatureDAO houseExtraFeatureDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeOne_ShouldCallDAOStoreOne() {
        HouseExtraFeature houseExtraFeature = new HouseExtraFeature(1, 1);

        houseExtraFeatureRepository.storeOne(houseExtraFeature);

        verify(houseExtraFeatureDAO, times(1)).storeOne(houseExtraFeature);
    }

    @Test
    void removeOneByIds_ShouldCallDAORemoveOneByIds() {
        int houseId = 1;
        int featureId = 2;

        houseExtraFeatureRepository.removeOneByIds(houseId, featureId);

        verify(houseExtraFeatureDAO, times(1)).removeOneByIds(houseId, featureId);
    }

    @Test
    void getAll_ShouldReturnAllHouseExtraFeatures() {
        List<HouseExtraFeature> features = Arrays.asList(
                new HouseExtraFeature(1, 1),
                new HouseExtraFeature(2, 2)
        );
        when(houseExtraFeatureDAO.getAll()).thenReturn(features);

        List<HouseExtraFeature> result = houseExtraFeatureRepository.getAll();

        assertEquals(2, result.size());
        verify(houseExtraFeatureDAO, times(1)).getAll();
    }

    @Test
    void getOneByIds_ShouldReturnHouseExtraFeature_WhenFound() {
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureDAO.getOneById(1, 1)).thenReturn(Optional.of(feature));

        Optional<HouseExtraFeature> result = houseExtraFeatureRepository.getOneByIds(1, 1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getHouseId());
        verify(houseExtraFeatureDAO, times(1)).getOneById(1, 1);
    }

    @Test
    void getOneByIds_ShouldReturnEmpty_WhenNotFound() {
        when(houseExtraFeatureDAO.getOneById(1, 1)).thenReturn(Optional.empty());

        Optional<HouseExtraFeature> result = houseExtraFeatureRepository.getOneByIds(1, 1);

        assertFalse(result.isPresent());
        verify(houseExtraFeatureDAO, times(1)).getOneById(1, 1);
    }

    @Test
    void updateOne_ShouldReturnTrue_WhenUpdateSucceeds() {
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureDAO.updateOne(feature)).thenReturn(true);

        boolean result = houseExtraFeatureRepository.updateOne(feature);

        assertTrue(result);
        verify(houseExtraFeatureDAO, times(1)).updateOne(feature);
    }

    @Test
    void updateOne_ShouldReturnFalse_WhenUpdateFails() {
        HouseExtraFeature feature = new HouseExtraFeature(1, 1);
        when(houseExtraFeatureDAO.updateOne(feature)).thenReturn(false);

        boolean result = houseExtraFeatureRepository.updateOne(feature);

        assertFalse(result);
        verify(houseExtraFeatureDAO, times(1)).updateOne(feature);
    }

    @Test
    void getAllFeaturesByHouseId_ShouldReturnFeatures() {
        List<HouseExtraFeature> features = Arrays.asList(
                new HouseExtraFeature(1, 1),
                new HouseExtraFeature(1, 2)
        );
        when(houseExtraFeatureDAO.getAllFeaturesByHouseId(1)).thenReturn(features);

        List<HouseExtraFeature> result = houseExtraFeatureRepository.getAllFeaturesByHouseId(1);

        assertEquals(2, result.size());
        verify(houseExtraFeatureDAO, times(1)).getAllFeaturesByHouseId(1);
    }

    @Test
    void getAllFeaturesByHouseIdWithNames_ShouldReturnFeaturesWithNames() {
        List<HouseExtraFeature> features = Arrays.asList(
                new HouseExtraFeature(1, 1),
                new HouseExtraFeature(1, 2)
        );
        when(houseExtraFeatureDAO.getAllFeaturesByHouseIdWithNames(1)).thenReturn(features);

        List<HouseExtraFeature> result = houseExtraFeatureRepository.getAllFeaturesByHouseIdWithNames(1);

        assertEquals(2, result.size());
        verify(houseExtraFeatureDAO, times(1)).getAllFeaturesByHouseIdWithNames(1);
    }
}
