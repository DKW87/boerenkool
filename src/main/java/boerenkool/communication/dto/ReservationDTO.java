package boerenkool.communication.dto;

import java.time.LocalDate;

public class ReservationDTO {

    private int reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int guestCount;
    private int houseId;
    private int userId;

    public ReservationDTO() {
    }

    public ReservationDTO(int reservationId, LocalDate startDate, LocalDate endDate, int guestCount, int houseId, int userId) {
        this.reservationId = reservationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guestCount = guestCount;
        this.houseId = houseId;
        this.userId = userId;
    }

    // Getters and Setters

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

