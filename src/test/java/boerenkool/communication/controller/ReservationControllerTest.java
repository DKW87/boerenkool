package boerenkool.communication.controller;

import boerenkool.business.model.Reservation;
import boerenkool.business.service.ReservationService;
import boerenkool.communication.dto.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    void getAllReservations() {
        // Arrange
        List<Reservation> reservations = List.of(new Reservation(/* parameters */));
        when(reservationService.getAllReservations()).thenReturn(reservations);

        // Act
        ResponseEntity<List<ReservationDTO>> response = reservationController.getAllReservations();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllReservationByUserId() {
        // Arrange
        int userId = 1;
        List<ReservationDTO> reservationDTOs = List.of(new ReservationDTO(/* parameters */));
        when(reservationService.getReservationsByUserId(userId)).thenReturn(reservationDTOs);

        // Act
        ResponseEntity<List<ReservationDTO>> response = reservationController.getAllReservationByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getReservationById() {
        // Arrange
        int id = 1;
        Reservation reservation = new Reservation(/* parameters */);
        when(reservationService.getReservationById(id)).thenReturn(Optional.of(reservation));

        // Act
        ResponseEntity<ReservationDTO> response = reservationController.getReservationById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void calculateCost() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);
        int houseId = 1;
        int guestCount = 2;
        when(reservationService.calculateReservationCost(startDate, endDate, houseId, guestCount)).thenReturn(100);

        // Act
        ResponseEntity<?> response = reservationController.calculateCost(startDate, endDate, houseId, guestCount);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody());
    }

    @Test
    void getAllReservationsByLandlord() {
        // Arrange
        int landlordId = 1;
        List<Reservation> reservations = List.of(new Reservation(/* parameters */));
        when(reservationService.getAllReservationsByLandlord(landlordId)).thenReturn(reservations);

        // Act
        ResponseEntity<?> response = reservationController.getAllReservationsByLandlord(landlordId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllReservationsByTenant() {
        // Arrange
        int tenantId = 1;
        List<Reservation> reservations = List.of(new Reservation(/* parameters */));
        when(reservationService.getAllReservationsByTenant(tenantId)).thenReturn(reservations);

        // Act
        ResponseEntity<?> response = reservationController.getAllReservationsByTenant(tenantId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllReservationsByHouseId() {
        // Arrange
        int houseId = 1;
        List<Reservation> reservations = List.of(new Reservation(/* parameters */));
        when(reservationService.getAllReservationsByHouseId(houseId)).thenReturn(reservations);

        // Act
        ResponseEntity<?> response = reservationController.getAllReservationsByHouseId(houseId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}