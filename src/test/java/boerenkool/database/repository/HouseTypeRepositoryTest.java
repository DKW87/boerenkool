package boerenkool.database.repository;

import boerenkool.business.model.HouseType;
import boerenkool.database.dao.mysql.HouseTypeDAO;
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

class HouseTypeRepositoryTest {

    @InjectMocks
    private HouseTypeRepository houseTypeRepository;

    @Mock
    private HouseTypeDAO houseTypeDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeOne_ShouldCallDAOStoreOne() {
        HouseType houseType = new HouseType(1, "Apartment");

        houseTypeRepository.storeOne(houseType);

        verify(houseTypeDAO, times(1)).storeOne(houseType);
    }

    @Test
    void removeOneById_ShouldCallDAORemoveOneById() {
        int id = 1;

        houseTypeRepository.removeOneById(id);

        verify(houseTypeDAO, times(1)).removeOneById(id);
    }

    @Test
    void getAll_ShouldReturnAllHouseTypes() {
        List<HouseType> houseTypes = Arrays.asList(
                new HouseType(1, "Apartment"),
                new HouseType(2, "Villa")
        );
        when(houseTypeDAO.getAll()).thenReturn(houseTypes);

        List<HouseType> result = houseTypeRepository.getAll();

        assertEquals(2, result.size());
        verify(houseTypeDAO, times(1)).getAll();
    }

    @Test
    void getOneById_ShouldReturnHouseType_WhenFound() {
        HouseType houseType = new HouseType(1, "Apartment");
        when(houseTypeDAO.getOneById(1)).thenReturn(Optional.of(houseType));

        Optional<HouseType> result = houseTypeRepository.getOneById(1);

        assertTrue(result.isPresent());
        assertEquals("Apartment", result.get().getHouseTypeName());
        verify(houseTypeDAO, times(1)).getOneById(1);
    }

    @Test
    void getOneById_ShouldReturnEmpty_WhenNotFound() {
        when(houseTypeDAO.getOneById(1)).thenReturn(Optional.empty());

        Optional<HouseType> result = houseTypeRepository.getOneById(1);

        assertFalse(result.isPresent());
        verify(houseTypeDAO, times(1)).getOneById(1);
    }

    @Test
    void updateOne_ShouldReturnTrue_WhenUpdateSucceeds() {
        HouseType houseType = new HouseType(1, "Apartment");
        when(houseTypeDAO.updateOne(houseType)).thenReturn(true);

        boolean result = houseTypeRepository.updateOne(houseType);

        assertTrue(result);
        verify(houseTypeDAO, times(1)).updateOne(houseType);
    }

    @Test
    void updateOne_ShouldReturnFalse_WhenUpdateFails() {
        HouseType houseType = new HouseType(1, "Apartment");
        when(houseTypeDAO.updateOne(houseType)).thenReturn(false);

        boolean result = houseTypeRepository.updateOne(houseType);

        assertFalse(result);
        verify(houseTypeDAO, times(1)).updateOne(houseType);
    }

    @Test
    void findByName_ShouldReturnHouseType_WhenFound() {
        HouseType houseType = new HouseType(1, "Apartment");
        when(houseTypeDAO.findByName("Apartment")).thenReturn(Optional.of(houseType));

        Optional<HouseType> result = houseTypeRepository.findByName("Apartment");

        assertTrue(result.isPresent());
        assertEquals("Apartment", result.get().getHouseTypeName());
        verify(houseTypeDAO, times(1)).findByName("Apartment");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotFound() {
        when(houseTypeDAO.findByName("NonExisting")).thenReturn(Optional.empty());

        Optional<HouseType> result = houseTypeRepository.findByName("NonExisting");

        assertFalse(result.isPresent());
        verify(houseTypeDAO, times(1)).findByName("NonExisting");
    }
}
