package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.ReservationService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.ReservationDTO;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HouseService houseService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    public ReservationController(ReservationService reservationService, HouseService houseService, UserService userService) {
        this.reservationService = reservationService;
        this.houseService = houseService;
        this.userService = userService;
        logger.info("Reservation Controller created");
    }

    @GetMapping("/reservation")
    public String getReservationPage() {
        return "reservation"; 
    }

    // 1. GET /api/reservations - Get all reservations
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            logger.warn("No reservations found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(reservationService::convertToDto)
                .toList();
        logger.info("Fetched all reservations.");
        return new ResponseEntity<>(reservationDTOs, HttpStatus.OK);
    }
    @GetMapping("/reservations-by-userId/{userId}")
    public ResponseEntity<List<ReservationDTO>> getAllReservationByUserId(@PathVariable int userId) {
        List<Reservation> reservations = reservationService.getAllReservationsByUserId(userId);
        if (reservations.isEmpty()) {
            logger.warn("No reservations found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(reservationService::convertToDto)
                .toList();
        logger.info("Fetched all reservations.");
        return new ResponseEntity<>(reservationDTOs, HttpStatus.OK);
    }

    // 2. GET /api/reservations/{id} - Get a specific reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            logger.info("Fetched reservation with ID: {}", id);
            ReservationDTO reservationDTO = reservationService.convertToDto(reservation.get());
            return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
        } else {
            logger.warn("No reservation found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 3. PUT /api/reservations/{id} - Update a reservation
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable int id, @RequestBody ReservationDTO updatedReservationDTO) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);

        if (existingReservation.isPresent()) {
            House house = houseService.getOneById(updatedReservationDTO.getHouseId());
            User user = userService.getOneById(updatedReservationDTO.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            Reservation updatedReservation = reservationService.convertToEntity(updatedReservationDTO, house, user);
            updatedReservation.setReservationId(id);

            Reservation savedReservation = reservationService.saveReservation(updatedReservation);

            ReservationDTO savedReservationDTO = reservationService.convertToDto(savedReservation);
            return new ResponseEntity<>(savedReservationDTO, HttpStatus.OK);
        } else {
            logger.warn("No reservation found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 4. DELETE /api/reservations/{id} - Cancel a reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable int id) {
        try {
            boolean isDeleted = reservationService.deleteReservationById(id);
            if (isDeleted) {
                logger.info("Deleted reservation with ID: {}", id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.warn("No reservation found with ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting reservation", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. DELETE /api/user/{userId}/reservations?reservationId={reservationId} - Tenant or Landlord cancels reservation
    @DeleteMapping("/users/{userId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable int userId, @RequestParam int reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);

        User user = userService.getOneById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        boolean isTenant = user.getTypeOfUser().equals("Huurder");

        if (reservation.isPresent()) {
            if (isTenant) {
                if (reservation.get().getReservedByUser().getUserId() == userId) {
                    boolean isDeleted = reservationService.deleteReservationById(reservationId);
                    if (isDeleted) {
                        logger.info("User with ID: {} canceled reservation with ID: {}", userId, reservationId);
                        return new ResponseEntity<>("Reservation canceled by tenant successfully.", HttpStatus.OK);
                    } else {
                        logger.warn("Failed to cancel reservation with ID: {}", reservationId);
                        return new ResponseEntity<>("Failed to cancel reservation.", HttpStatus.NOT_FOUND);
                    }
                } else {
                    logger.warn("User with ID: {} does not have permission to cancel reservation with ID: {}", userId, reservationId);
                    return new ResponseEntity<>("User not authorized to cancel this reservation.", HttpStatus.FORBIDDEN);
                }
            }
            else {
                House house = houseService.getOneById(reservation.get().getHouse().getHouseId());
                if (house.getHouseOwner().getUserId() == userId) {
                    boolean isDeleted = reservationService.deleteReservationById(reservationId);
                    if (isDeleted) {
                        logger.info("Landlord canceled reservation with ID: {}", reservationId);
                        return new ResponseEntity<>("Reservation cancelled successfully by landlord.", HttpStatus.OK);
                    } else {
                        logger.warn("Failed to cancel reservation with ID: {}", reservationId);
                        return new ResponseEntity<>("Failed to cancel reservation.", HttpStatus.NOT_FOUND);
                    }
                } else {
                    logger.warn("Landlord cancel reservation with ID: {}",  reservationId);
                    return new ResponseEntity<>("Landlord is not authorized to cancel this reservation.", HttpStatus.FORBIDDEN);
                }
            }
        } else {
            logger.warn("Reservation with ID: {} not found.", reservationId);
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }
    }

    // 7. CREATE
    @PostMapping
    public ResponseEntity<?> saveReservation(@RequestBody ReservationDTO reservationDTO) {
        try {
            House house = houseService.getOneById(reservationDTO.getHouseId());
            User user = userService.getOneById(reservationDTO.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            Reservation reservation = reservationService.convertToEntity(reservationDTO, house, user);

            Reservation savedReservation = reservationService.saveReservation(reservation);

            ReservationDTO savedReservationDTO = reservationService.convertToDto(savedReservation);
            logger.info("Created new reservation with ID: {}", savedReservation.getReservationId());
            return new ResponseEntity<>(savedReservationDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating reservation", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get reservations by landlord
    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<?> getAllReservationsByLandlord(@PathVariable int landlordId) {
        List<Reservation> reservations = reservationService.getAllReservationsByLandlord(landlordId);
        if (reservations.isEmpty()) {
            logger.warn("No reservations found for landlord with ID: {}", landlordId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(reservationService::convertToDto)
                .toList();
        return new ResponseEntity<>(reservationDTOs, HttpStatus.OK);
    }

    // Get reservations by tenant ID
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<?> getAllReservationsByTenant(@PathVariable int tenantId) {
        List<Reservation> reservations = reservationService.getAllReservationsByTenant(tenantId);
        if (reservations.isEmpty()) {
            logger.warn("No reservations found for tenant with ID: {}", tenantId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(reservationService::convertToDto)
                .toList();
        return new ResponseEntity<>(reservationDTOs, HttpStatus.OK);
    }

    // Get reservations by house ID
    @GetMapping("/house/{houseId}")
    public ResponseEntity<?> getAllReservationsByHouseId(@PathVariable int houseId) {
        List<Reservation> reservations = reservationService.getAllReservationsByHouseId(houseId);
        if (reservations.isEmpty()) {
            logger.warn("No reservations found for house with ID: {}", houseId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(reservationService::convertToDto)
                .toList();
        return new ResponseEntity<>(reservationDTOs, HttpStatus.OK);
    }
}

