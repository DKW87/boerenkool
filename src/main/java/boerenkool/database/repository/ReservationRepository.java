package boerenkool.database.repository;

import boerenkool.business.model.Reservation;
import boerenkool.database.dao.mysql.ReservationDAO;
import boerenkool.database.dao.mysql.UserDAO;
import boerenkool.database.dao.mysql.HouseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Adnan Kilic
 * @project Boerenkool
 */


@Repository
public class ReservationRepository {

    private final Logger logger = LoggerFactory.getLogger(ReservationRepository.class);
    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;
    private final HouseDAO houseDAO;

    @Autowired
    public ReservationRepository(ReservationDAO reservationDAO, UserDAO userDAO, HouseDAO houseDAO) {
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.houseDAO = houseDAO;
        logger.info("New ReservationRepository instance created.");
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationDAO.getAll();
        reservations.forEach(this::loadRelatedEntities);
        logger.debug("Fetched all reservations, count: {}", reservations.size());
        return reservations;
    }

    public Optional<Reservation> getReservationById(int id) {
        Optional<Reservation> reservationOpt = reservationDAO.getOneById(id);
        reservationOpt.ifPresent(this::loadRelatedEntities);
        if (reservationOpt.isPresent()) {
            logger.debug("Fetched reservation with ID: {}", id);
        } else {
            logger.warn("No reservation found with ID: {}", id);
        }
        return reservationOpt;
    }

    public Reservation saveReservation(Reservation reservation) {
        if (reservation.getReservationId() == 0) {
            reservationDAO.storeOne(reservation);
            logger.info("Stored new reservation with ID: {}", reservation.getReservationId());
        } else {
            reservationDAO.updateOne(reservation);
            logger.info("Updated reservation with ID: {}", reservation.getReservationId());
        }
        loadRelatedEntities(reservation);
        return reservation;
    }

    public boolean deleteReservationById(int id) {
        boolean isDeleted = reservationDAO.removeOneById(id);
        if (isDeleted) {
            logger.info("Deleted reservation with ID: {}", id);
        } else {
            logger.warn("Failed to delete reservation with ID: {}", id);
        }
        return isDeleted;
    }

    public List<Reservation> getAllReservationsByLandlord(int landlordId) {
        List<Reservation> reservations = reservationDAO.getAllReservationsByUserId(landlordId);
        reservations.forEach(this::loadRelatedEntities);
        logger.debug("Fetched reservations for landlord with ID: {}", landlordId);
        return reservations;
    }

    public List<Reservation> getAllReservationsByTenant(int tenantId) {
        List<Reservation> reservations = reservationDAO.getAllReservationsByTenant(tenantId);
        reservations.forEach(this::loadRelatedEntities);
        logger.debug("Fetched reservations for tenant with ID: {}", tenantId);
        return reservations;
    }

    public List<Reservation> getAllReservationsByHouseId(int houseId) {
        List<Reservation> reservations = reservationDAO.getAllReservationsByHouseId(houseId);
        reservations.forEach(this::loadRelatedEntities);
        logger.debug("Fetched reservations for house with ID: {}", houseId);
        return reservations;
    }

    public boolean checkDateOverlap(int houseId, LocalDate startDate, LocalDate endDate) {
        return reservationDAO.existsByHouseIdAndDatesOverlap(houseId, startDate, endDate);
    }

    private void loadRelatedEntities(Reservation reservation) {
        int userId = reservation.getReservedByUser().getUserId();
        int houseId = reservation.getHouse().getHouseId();

        userDAO.getOneById(userId).ifPresent(reservation::setReservedByUser);
        houseDAO.getOneById(houseId).ifPresent(reservation::setHouse);

        logger.debug("Loaded related entities for reservation ID: {}", reservation.getReservationId());
    }
}
