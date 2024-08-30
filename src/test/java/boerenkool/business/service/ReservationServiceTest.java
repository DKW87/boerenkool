/*
package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.database.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReservation_Success() {

        House house = new House();
        User user = new User();
        Reservation reservation = new Reservation(1, house, user, LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 25), 4);

        when(reservationRepository.saveReservation(any(Reservation.class))).thenReturn(reservation);


        Reservation savedReservation = reservationService.saveReservation(reservation);
        assertNotNull(savedReservation);
        assertEquals(reservation.getReservationId(), savedReservation.getReservationId());


        verify(reservationRepository, times(1)).saveReservation(any(Reservation.class));
    }

    @Test
    void testSaveReservation_InvalidGuestCount() {

        House house = new House();
        User user = new User();
        Reservation reservation = new Reservation(1, house, user, LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 25), -1);


        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.saveReservation(reservation);
        });


        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }

    @Test
    void testSaveReservation_EndDateBeforeStartDate() {

        House house = new House();
        User user = new User();
        Reservation reservation = new Reservation(1, house, user, LocalDate.of(2024, 8, 25), LocalDate.of(2024, 8, 20), 4);


        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.saveReservation(reservation);
        });


        verify(reservationRepository, never()).saveReservation(any(Reservation.class));
    }
}
*/
