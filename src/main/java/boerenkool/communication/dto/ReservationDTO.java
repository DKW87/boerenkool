package boerenkool.communication.dto;

import java.time.LocalDate;

/**
 * @author Adnan Kilic
 * @project Boerenkool
 */

public class ReservationDTO {

    private int reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int guestCount;
    private int houseId;
    private String houseName;
    private int userId;

    public ReservationDTO() {
    }

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

    public String getHouseName() {return houseName;}

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

