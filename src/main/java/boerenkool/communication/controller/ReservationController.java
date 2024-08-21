package boerenkool.communication.controller;

import boerenkool.business.model.Reservation;
import boerenkool.business.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
        logger.info("ReservationController created");
    }

    // 1. GET /api/reservations - Get all reservations
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            logger.warn("No reservations found.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("Fetched all reservations.");
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // 2. GET /api/reservations/{id} - Get a specific reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            logger.info("Fetched reservation with ID: {}", id);
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        } else {
            logger.warn("No reservation found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 3. PUT /api/reservations/{id} - Update a reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable int id, @RequestBody Reservation updatedReservation) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.setStartDate(updatedReservation.getStartDate());
            reservation.setEndDate(updatedReservation.getEndDate());
            reservation.setGuestCount(updatedReservation.getGuestCount());
            reservation.setHouse(updatedReservation.getHouse());
            reservation.setReservedByUser(updatedReservation.getReservedByUser());

            Reservation savedReservation = reservationService.saveReservation(reservation);
            logger.info("Updated reservation with ID: {}", id);
            return new ResponseEntity<>(savedReservation, HttpStatus.OK);
        } else {
            logger.warn("Reservation with ID: {} not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 4. DELETE /api/reservations/{id} - Cancel a reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable int id) {
        boolean isDeleted = reservationService.deleteReservationById(id);
        if (isDeleted) {
            logger.info("Deleted reservation with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            logger.warn("Failed to delete reservation with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 5. DELETE /api/user/{userId}/reservations?reservationId={reservationId} - Tenant cancels reservation
    @DeleteMapping("/user/{userId}/reservations")
    public ResponseEntity<String> cancelReservationByUser(@PathVariable int userId, @RequestParam int reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);

        if (reservation.isPresent()) {
            if (reservation.get().getReservedByUser().getUserId() == userId) {
                boolean isDeleted = reservationService.deleteReservationById(reservationId);
                if (isDeleted) {
                    // Notify landlord if necessary, placeholder for notification
                    logger.info("User with ID: {} canceled reservation with ID: {}", userId, reservationId);
                    return new ResponseEntity<>("Reservation canceled successfully.", HttpStatus.OK);
                } else {
                    logger.warn("Failed to cancel reservation with ID: {}", reservationId);
                    return new ResponseEntity<>("Failed to cancel reservation.", HttpStatus.NOT_FOUND);
                }
            } else {
                logger.warn("User with ID: {} does not have permission to cancel reservation with ID: {}", userId, reservationId);
                return new ResponseEntity<>("User not authorized to cancel this reservation.", HttpStatus.FORBIDDEN);
            }
        } else {
            logger.warn("Reservation with ID: {} not found.", reservationId);
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }
    }

    // 6. DELETE /api/user/{userId}/reservations?reservationId={reservationId} - Landlord cancels reservation
    @DeleteMapping("/api/user/{userId}/reservations")
    public ResponseEntity<String> cancelReservationByLandlord(@PathVariable int userId, @RequestParam int reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);

        if (reservation.isPresent()) {
            boolean isDeleted = reservationService.deleteReservationById(reservationId);
            if (isDeleted) {
                // Notify tenant if necessary, placeholder for notification
                logger.info("Landlord with ID: {} canceled reservation with ID: {}", userId, reservationId);
                return new ResponseEntity<>("Reservation canceled by landlord successfully.", HttpStatus.OK);
            } else {
                logger.warn("Failed to cancel reservation with ID: {}", reservationId);
                return new ResponseEntity<>("Failed to cancel reservation.", HttpStatus.NOT_FOUND);
            }
        } else {
            logger.warn("Reservation with ID: {} not found.", reservationId);
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }
    }

    // 7. CREATE
    @PostMapping
    public ResponseEntity<?> saveReservation(@RequestBody Reservation reservation) {
        try {
            if (reservation.getGuestCount() <= 0) {
                logger.warn("Invalid guest count: {}", reservationService.saveReservation(reservation));
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Reservation savedReservation = reservationService.saveReservation(reservation);
            logger.info("Saved reservation: {}", savedReservation.getReservationId());
            return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
        } catch (DataAccessException dae) {
            logger.error("Database error occurred while saving reservation: {}", dae.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("An error occurred while saving reservation: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get reservations by landlord
    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<List<Reservation>> getAllReservationsByLandlord(@PathVariable int landlordId) {
        List<Reservation> reservations = reservationService.getAllReservationsByLandlord(landlordId);
        if (reservations.isEmpty()) {
            logger.warn("Landlord with ID: {} not found.", landlordId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Found {} reservations.", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Get reservations by tenant ID
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Reservation>> getAllReservationsByTenant(@PathVariable int tenantId) {
        List<Reservation> reservations = reservationService.getAllReservationsByTenant(tenantId);
        if (reservations.isEmpty()) {
            logger.warn("Tenant with ID: {} not found.", tenantId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Found {} reservations.", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    // Get reservations by house ID
    @GetMapping("/house/{houseId}")
    public ResponseEntity<List<Reservation>> getAllReservationsByHouseId(@PathVariable int houseId) {
        List<Reservation> reservations = reservationService.getAllReservationsByHouseId(houseId);
        if (reservations.isEmpty()) {
            logger.warn("House with ID: {} not found.", houseId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Found {} reservations.", reservations.size());
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
}

