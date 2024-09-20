package boerenkool.communication.controller;

import boerenkool.business.model.ExtraFeature;
import boerenkool.business.model.HouseExtraFeature;
import boerenkool.business.service.ExtraFeatureService;
import boerenkool.business.service.HouseExtraFeatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtraFeatureControllerTest {

    @InjectMocks
    private ExtraFeatureController extraFeatureController;

    @Mock
    private ExtraFeatureService extraFeatureService;

    @Mock
    private HouseExtraFeatureService houseExtraFeatureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllExtraFeatures_ShouldReturnList() {
        List<ExtraFeature> features = new ArrayList<>();
        features.add(new ExtraFeature(1, "Feature1"));
        when(extraFeatureService.getAllExtraFeatures()).thenReturn(features);

        List<ExtraFeature> result = extraFeatureController.getAllExtraFeatures();

        assertEquals(1, result.size());
        assertEquals("Feature1", result.get(0).getExtraFeatureName());
        verify(extraFeatureService, times(1)).getAllExtraFeatures();
    }

    @Test
    void getExtraFeatureById_ShouldReturnFeature_WhenExists() {
        ExtraFeature feature = new ExtraFeature(1, "Feature1");
        when(extraFeatureService.getExtraFeatureById(1)).thenReturn(Optional.of(feature));

        ResponseEntity<?> response = extraFeatureController.getExtraFeatureById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(feature, response.getBody());
        verify(extraFeatureService, times(1)).getExtraFeatureById(1);
    }

    @Test
    void getExtraFeatureById_ShouldReturnNotFound_WhenNotExists() {
        when(extraFeatureService.getExtraFeatureById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = extraFeatureController.getExtraFeatureById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ExtraFeature niet gevonden", response.getBody());
        verify(extraFeatureService, times(1)).getExtraFeatureById(1);
    }

    @Test
    void createExtraFeature_ShouldReturnCreated() {
        ExtraFeature feature = new ExtraFeature(1, "Feature1");
        when(extraFeatureService.saveExtraFeature(feature)).thenReturn(feature);

        ResponseEntity<String> response = extraFeatureController.createExtraFeature(feature);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("ExtraFeature succesvol aangemaakt met ID:"));
        verify(extraFeatureService, times(1)).saveExtraFeature(feature);
    }

    @Test
    void createExtraFeature_ShouldReturnBadRequest_WhenExceptionThrown() {
        ExtraFeature feature = new ExtraFeature(1, "Feature1");
        when(extraFeatureService.saveExtraFeature(feature)).thenThrow(new IllegalArgumentException("Duplicate feature"));

        ResponseEntity<String> response = extraFeatureController.createExtraFeature(feature);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Fout bij het aanmaken van ExtraFeature:"));
        verify(extraFeatureService, times(1)).saveExtraFeature(feature);
    }

    @Test
    void updateExtraFeature_ShouldReturnOk_WhenUpdated() {
        ExtraFeature feature = new ExtraFeature(1, "UpdatedFeature");
        when(extraFeatureService.updateExtraFeature(feature)).thenReturn(true);

        ResponseEntity<?> response = extraFeatureController.updateExtraFeature(1, feature);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ExtraFeature succesvol bijgewerkt", response.getBody());
        verify(extraFeatureService, times(1)).updateExtraFeature(feature);
    }

    @Test
    void updateExtraFeature_ShouldReturnInternalServerError_WhenNotUpdated() {
        ExtraFeature feature = new ExtraFeature(1, "UpdatedFeature");
        when(extraFeatureService.updateExtraFeature(feature)).thenReturn(false);

        ResponseEntity<?> response = extraFeatureController.updateExtraFeature(1, feature);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Mislukt om ExtraFeature bij te werken", response.getBody());
        verify(extraFeatureService, times(1)).updateExtraFeature(feature);
    }

    @Test
    void deleteExtraFeature_ShouldReturnOk_WhenDeleted() {
        when(extraFeatureService.deleteExtraFeatureById(1)).thenReturn(true);

        ResponseEntity<?> response = extraFeatureController.deleteExtraFeature(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ExtraFeature succesvol verwijderd", response.getBody());
        verify(extraFeatureService, times(1)).deleteExtraFeatureById(1);
    }

    @Test
    void deleteExtraFeature_ShouldReturnInternalServerError_WhenNotDeleted() {
        when(extraFeatureService.deleteExtraFeatureById(1)).thenReturn(false);

        ResponseEntity<?> response = extraFeatureController.deleteExtraFeature(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Mislukt om ExtraFeature te verwijderen", response.getBody());
        verify(extraFeatureService, times(1)).deleteExtraFeatureById(1);
    }

    @Test
    void findExtraFeatureByName_ShouldReturnFeature_WhenFound() {
        ExtraFeature feature = new ExtraFeature(1, "Feature1");
        when(extraFeatureService.findExtraFeatureByName("Feature1")).thenReturn(Optional.of(feature));

        ResponseEntity<?> response = extraFeatureController.findExtraFeatureByName("Feature1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(feature, response.getBody());
        verify(extraFeatureService, times(1)).findExtraFeatureByName("Feature1");
    }

    @Test
    void findExtraFeatureByName_ShouldReturnNotFound_WhenNotFound() {
        when(extraFeatureService.findExtraFeatureByName("Feature1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = extraFeatureController.findExtraFeatureByName("Feature1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ExtraFeature niet gevonden", response.getBody());
        verify(extraFeatureService, times(1)).findExtraFeatureByName("Feature1");
    }
}
