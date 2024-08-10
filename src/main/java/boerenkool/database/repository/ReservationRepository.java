package boerenkool.database.repository;

import boerenkool.business.model.Reservation;
import boerenkool.database.dao.mysql.ReservationDAO;
import boerenkool.database.dao.mysql.UserDAO;
import boerenkool.database.dao.mysql.HouseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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
        return reservationDAO.getAll();
    }

    public Optional<Reservation> getReservationById(int id) {
        Optional<Reservation> reservation = reservationDAO.getOneById(id);
        reservation.ifPresent(this::loadRelatedEntities);
        return reservation;
    }

    public Reservation saveReservation(Reservation reservation) {
        if (reservation.getReservationId() == 0) {
            reservationDAO.storeOne(reservation);
        } else {
            reservationDAO.updateOne(reservation);
        }
        return reservation;
    }

    public boolean deleteReservationById(int id) {
        return reservationDAO.removeOneById(id);
    }

    private void loadRelatedEntities(Reservation reservation) {
        // Load related entities if needed
        // Example: reservation.getHouse() or reservation.getReservedByUser()
        reservation.setReservedByUser(userDAO.getOneById(reservation.getReservedByUser().getUserId())
                .orElse(null));
        reservation.setHouse(houseDAO.getOneById(reservation.getHouse().getHouseId())
                .orElse(null));
    }
}
