package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.communication.dto.ReservationDTO;
import boerenkool.database.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HouseService houseService;
    private final UserService userService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, HouseService houseService, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.houseService = houseService;
        this.userService = userService;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getAllReservations();
    }

    public Optional<Reservation> getReservationById(int id) {
        return reservationRepository.getReservationById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        validateReservation(reservation);
        return reservationRepository.saveReservation(reservation);
    }

    public boolean deleteReservationById(int id) {
        if (reservationRepository.getReservationById(id).isEmpty()) {
            throw new IllegalArgumentException("Reservation not found");
        }
        return reservationRepository.deleteReservationById(id);
    }

    public List<Reservation> getAllReservationsByLandlord(int landlordId) {
        return reservationRepository.getAllReservationsByLandlord(landlordId);
    }

    public List<Reservation> getAllReservationsByTenant(int tenantId) {
        return reservationRepository.getAllReservationsByTenant(tenantId);
    }

    public List<Reservation> getAllReservationsByHouseId(int houseId) {
        return reservationRepository.getAllReservationsByHouseId(houseId);
    }

    public ReservationDTO convertToDto(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setReservationId(reservation.getReservationId());
        reservationDTO.setStartDate(reservation.getStartDate());
        reservationDTO.setEndDate(reservation.getEndDate());
        reservationDTO.setGuestCount(reservation.getGuestCount());
        reservationDTO.setHouseId(reservation.getHouse().getHouseId());
        reservationDTO.setHouseName(reservation.getHouse().getHouseName());
        reservationDTO.setUserId(reservation.getReservedByUser().getUserId());
        return reservationDTO;
    }

    public Reservation convertToEntity(ReservationDTO reservationDTO, House house, User user) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationDTO.getReservationId());
        reservation.setStartDate(reservationDTO.getStartDate());
        reservation.setEndDate(reservationDTO.getEndDate());
        reservation.setGuestCount(reservationDTO.getGuestCount());
        reservation.setHouse(house);
        reservation.setReservedByUser(user);
        return reservation;
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getGuestCount() < 0) {
            throw new IllegalArgumentException("Guest count cannot be negative.");
        }

        LocalDate startDate = reservation.getStartDate();
        LocalDate endDate = reservation.getEndDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
    }

    public List<Reservation> getAllReservationsByUserId(int userId) {
        return reservationRepository.getAllReservationsByUserId(userId);
    }
}

