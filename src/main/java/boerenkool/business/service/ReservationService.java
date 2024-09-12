package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.communication.dto.ReservationDTO;
import boerenkool.database.repository.ReservationRepository;
import boerenkool.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HouseService houseService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, HouseService houseService, UserService userService, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.houseService = houseService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getAllReservations();
    }

    public Optional<Reservation> getReservationById(int id) {
        return reservationRepository.getReservationById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        checkGuestCount(reservation.getHouse(), reservation.getGuestCount());
        checkDateOverlap(reservation.getHouse().getHouseId(), reservation.getStartDate(), reservation.getEndDate());

        User user = reservation.getReservedByUser();

        int totalCost = calculateReservationCost(
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getHouse().getHouseId(),
                reservation.getGuestCount()
        );

        validateUserBudget(totalCost, user);

        Reservation savedReservation = reservationRepository.saveReservation(reservation);

        int newBalance = user.getCoinBalance() - totalCost;
        boolean updateSuccess = userRepository.updateBoerenkoolcoins(user.getUserId(), newBalance);

        if (!updateSuccess) {
            throw new RuntimeException("Failed to update user balance");
        }

        return savedReservation;
    }

    public int calculateReservationCost(LocalDate startDate, LocalDate endDate, int houseId, int guestCount) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Startdatum kan niet na einddatum zijn!");
        }

        House house = houseService.getOneById(houseId);

        int pppd = house.getPricePPPD();

        int days = (int) ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));

        return pppd * days * guestCount;
    }

    public void validateUserBudget(int totalCost, User user) {
        int userBudget = user.getCoinBalance();
        if (totalCost> userBudget) {
            throw new IllegalArgumentException("De reserveringskosten overschrijden uw budget!");
        }
    }


    public void validateReservationDates(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException("De datums kan niet in het verleden liggen!");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Startdatum kan niet na einddatum zijn!");
        }
    }

    private void checkGuestCount(House house, int guestCount) {
        if (guestCount > house.getMaxGuest()) {
            throw new IllegalArgumentException("Het aantal gasten overschrijdt het maximaal toegestane aantal ("
                    + house.getMaxGuest() + ") voor dit huis!");
        }
    }

    private void checkDateOverlap(int houseId, LocalDate startDate, LocalDate endDate) {
        boolean hasOverlap = reservationRepository.checkDateOverlap(houseId, startDate, endDate);
        if (hasOverlap) {
            throw new IllegalStateException("Dit huis is niet beschikbaar op deze data!");
        }
    }


    public boolean deleteReservationById(int id) {

        Reservation reservation = reservationRepository.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        User user = reservation.getReservedByUser();
        int totalCost = calculateReservationCost(
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getHouse().getHouseId(),
                reservation.getGuestCount()
        );

        int newBalance = user.getCoinBalance() + totalCost;
        boolean updateSuccess = userRepository.updateBoerenkoolcoins(user.getUserId(), newBalance);

        if (!updateSuccess) {
            throw new RuntimeException("Failed to update user balance");
        }

        boolean cancellationSuccess = reservationRepository.deleteReservationById(id);

        if (!cancellationSuccess) {
            throw new RuntimeException("Failed to cancel reservation");
        }

        return true;
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

    public List<ReservationDTO> getReservationsByUserId(int userId) {

        User user = userService.getOneById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reservation> reservations = fetchReservationsByUserType(user);

        return reservations.stream()
                .map(this::convertToDto)
                .toList();
    }

    private List<Reservation> fetchReservationsByUserType(User user) {
        String userType = user.getTypeOfUser();

        if ("Huurder".equalsIgnoreCase(userType)) {
            return getAllReservationsByTenant(user.getUserId());
        } else if ("Verhuurder".equalsIgnoreCase(userType)) {
            return getAllReservationsByLandlord(user.getUserId());
        } else {
            return Collections.emptyList();
        }
    }

    public boolean isUserAuthorizedToDeleteReservation(User user, Reservation reservation) {
        boolean isTenant = "Huurder".equals(user.getTypeOfUser());

        if (isTenant) {
            return reservation.getReservedByUser().getUserId() == user.getUserId();
        } else {
            House house = houseService.getOneById(reservation.getHouse().getHouseId());
            return house.getHouseOwner().getUserId() == user.getUserId();
        }
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

    public List<Reservation> getAllReservationsByUserId(int userId) {
        return reservationRepository.getAllReservationsByUserId(userId);
    }
}

