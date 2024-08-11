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
        setStartDate(startDate);
        setEndDate(endDate);
        setGuestCount(guestCount);
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

    public void setGuestCount(int guestCount) {
        if (guestCount < 0) {
            throw new IllegalArgumentException("Guest count cannot be negative.");
        }
        this.guestCount = guestCount;
    }

    public LocalDate getEndDate() {return endDate;}

    public void setEndDate(LocalDate endDate) {
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        this.startDate = startDate;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return reservationId == that.reservationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
}
