
package boerenkool.business.model;

import java.time.LocalDate;
import java.util.Objects;


/**
 * @author Adnan Kilic
 * @project Boerenkool
 * @created 07/08/2024 - 19:49
 */


public class Reservation {

    private int reservationId;
    private House house;
    private User reservedByUser;
    private LocalDate startDate;
    private LocalDate endDate;
    private int guestCount;

    public Reservation(int reservationId, House house, User reservedByUser, LocalDate startDate, LocalDate endDate, int guestCount) {
        this.reservationId = reservationId;
        this.house = house;
        this.reservedByUser = reservedByUser;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guestCount = guestCount;
    }

    public Reservation() {}

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {this.guestCount = guestCount;}

    public LocalDate getEndDate() {return endDate;}

    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public User getReservedByUser() {
        return reservedByUser;
    }

    public void setReservedByUser(User reservedByUser) {
        this.reservedByUser = reservedByUser;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", house=" + house +
                ", reservedByUser=" + reservedByUser +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", guestCount=" + guestCount +
                '}';
    }
}

