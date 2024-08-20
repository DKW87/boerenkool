package boerenkool.business.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void testGettersAndSetters() {

        int reservationId = 1;
        House house = new House();
        User user = new User();
        LocalDate startDate = LocalDate.of(2024, 8, 20);
        LocalDate endDate = LocalDate.of(2024, 8, 25);
        int guestCount = 4;


        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setHouse(house);
        reservation.setReservedByUser(user);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setGuestCount(guestCount);


        assertEquals(reservationId, reservation.getReservationId());
        assertEquals(house, reservation.getHouse());
        assertEquals(user, reservation.getReservedByUser());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());
        assertEquals(guestCount, reservation.getGuestCount());
    }

    @Test
    void testToString() {
        House house = new House();
        User user = new User();
        LocalDate startDate = LocalDate.of(2024, 8, 20);
        LocalDate endDate = LocalDate.of(2024, 8, 25);
        int guestCount = 4;

        Reservation reservation = new Reservation(1, house, user, startDate, endDate, guestCount);

        String expected = "Reservation{reservationId=1, house=" + house + ", reservedByUser=" + user +
                ", startDate=" + startDate + ", endDate=" + endDate + ", guestCount=" + guestCount + "}";
        assertEquals(expected, reservation.toString());
    }
}
