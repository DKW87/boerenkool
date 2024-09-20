package boerenkool.communication.controller;

import boerenkool.business.model.HouseType;
import boerenkool.business.service.HouseTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseTypeControllerTest {

    @InjectMocks
    private HouseTypeController houseTypeController;

    @Mock
    private HouseTypeService houseTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllHouseTypes_ShouldReturnList() {
        List<HouseType> houseTypes = new ArrayList<>();
        houseTypes.add(new HouseType(1, "Apartment"));
        when(houseTypeService.getAllHouseTypes()).thenReturn(houseTypes);

        List<HouseType> result = houseTypeController.getAllHouseTypes();

        assertEquals(1, result.size());
        assertEquals("Apartment", result.get(0).getHouseTypeName());
        verify(houseTypeService, times(1)).getAllHouseTypes();
    }

    @Test
    void getHouseTypeById_ShouldReturnHouseType_WhenExists() {
        HouseType houseType = new HouseType(1, "Villa");
        when(houseTypeService.getHouseTypeById(1)).thenReturn(Optional.of(houseType));

        ResponseEntity<?> response = houseTypeController.getHouseTypeById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(houseType, response.getBody());
        verify(houseTypeService, times(1)).getHouseTypeById(1);
    }

    @Test
    void getHouseTypeById_ShouldReturnNotFound_WhenNotExists() {
        when(houseTypeService.getHouseTypeById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = houseTypeController.getHouseTypeById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("HouseType niet gevonden", response.getBody());
        verify(houseTypeService, times(1)).getHouseTypeById(1);
    }

    @Test
    void createHouseType_ShouldReturnCreated() {
        HouseType houseType = new HouseType();
        houseType.setHouseTypeName("Villa");

        Map<String, String> body = Map.of("name", "Villa");
        when(houseTypeService.saveHouseType(any(HouseType.class))).thenReturn(houseType);

        ResponseEntity<String> response = houseTypeController.createHouseType(body);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("HouseType succesvol aangemaakt!", response.getBody());
        verify(houseTypeService, times(1)).saveHouseType(any(HouseType.class));
    }

    @Test
    void createHouseType_ShouldReturnBadRequest_WhenExceptionThrown() {
        Map<String, String> body = Map.of("name", "Villa");
        when(houseTypeService.saveHouseType(any(HouseType.class))).thenThrow(new IllegalArgumentException("Duplicate name"));

        ResponseEntity<String> response = houseTypeController.createHouseType(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Fout bij het aanmaken van HouseType", response.getBody());
        verify(houseTypeService, times(1)).saveHouseType(any(HouseType.class));
    }

    @Test
    void updateHouseType_ShouldReturnOk_WhenUpdated() {
        HouseType houseType = new HouseType(1, "Updated House");
        when(houseTypeService.updateHouseType(houseType)).thenReturn(true);

        ResponseEntity<?> response = houseTypeController.updateHouseType(1, houseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("HouseType succesvol bijgewerkt", response.getBody());
        verify(houseTypeService, times(1)).updateHouseType(houseType);
    }

    @Test
    void updateHouseType_ShouldReturnInternalServerError_WhenNotUpdated() {
        HouseType houseType = new HouseType(1, "Updated House");
        when(houseTypeService.updateHouseType(houseType)).thenReturn(false);

        ResponseEntity<?> response = houseTypeController.updateHouseType(1, houseType);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Mislukt om HouseType bij te werken", response.getBody());
        verify(houseTypeService, times(1)).updateHouseType(houseType);
    }

    @Test
    void deleteHouseType_ShouldReturnOk_WhenDeleted() {
        when(houseTypeService.deleteHouseTypeById(1)).thenReturn(true);

        ResponseEntity<?> response = houseTypeController.deleteHouseType(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("HouseType succesvol verwijderd", response.getBody());
        verify(houseTypeService, times(1)).deleteHouseTypeById(1);
    }

    @Test
    void deleteHouseType_ShouldReturnInternalServerError_WhenNotDeleted() {
        when(houseTypeService.deleteHouseTypeById(1)).thenReturn(false);

        ResponseEntity<?> response = houseTypeController.deleteHouseType(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Mislukt om HouseType te verwijderen", response.getBody());
        verify(houseTypeService, times(1)).deleteHouseTypeById(1);
    }

    @Test
    void findHouseTypeByName_ShouldReturnHouseType_WhenFound() {
        HouseType houseType = new HouseType(1, "Villa");
        when(houseTypeService.findHouseTypeByName("Villa")).thenReturn(Optional.of(houseType));

        ResponseEntity<?> response = houseTypeController.findHouseTypeByName("Villa");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(houseType, response.getBody());
        verify(houseTypeService, times(1)).findHouseTypeByName("Villa");
    }

    @Test
    void findHouseTypeByName_ShouldReturnNotFound_WhenNotFound() {
        when(houseTypeService.findHouseTypeByName("Villa")).thenReturn(Optional.empty());

        ResponseEntity<?> response = houseTypeController.findHouseTypeByName("Villa");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("HouseType niet gevonden", response.getBody());
        verify(houseTypeService, times(1)).findHouseTypeByName("Villa");
    }
}
