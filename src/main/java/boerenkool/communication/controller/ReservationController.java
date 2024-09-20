package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.ReservationService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.ReservationDTO;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Adnan Kilic
 * @project Boerenkool
 */

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HouseService houseService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final AuthorizationService authorizationService;

    @Autowired
    public ReservationController(ReservationService reservationService, AuthorizationService authorizationService, HouseService houseService, UserService userService) {
        this.reservationService = reservationService;
        this.houseService = houseService;
        this.userService = userService;
        this.authorizationService = authorizationService;
        logger.info("Reservation Controller created");
    }

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
        try {
            List<ReservationDTO> reservationDTOs = reservationService.getReservationsByUserId(userId);
            if (reservationDTOs.isEmpty()) {
                logger.warn("No reservations found for user ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
            logger.info("Fetched reservations for user ID: {}", userId);
            return ResponseEntity.ok(reservationDTOs);
        } catch (RuntimeException e) {
            logger.error("Error fetching reservations for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            logger.info("Fetched reservation with ID: {}", id);
            ReservationDTO reservationDTO = reservationService.convertToDto(reservation.get());
            return new ResponseEntity<>(reservationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/calculate-cost")
    public ResponseEntity<?> calculateCost(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam int houseId,
            @RequestParam int guestCount) {
        try {
            int totalCost = reservationService.calculateReservationCost(startDate, endDate, houseId, guestCount);
            return new ResponseEntity<>(totalCost, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable int id, @RequestBody ReservationDTO updatedReservationDTO) {
        return reservationService.getReservationById(id)
                .map(existingReservation -> updateAndSaveReservation(id, updatedReservationDTO))
                .orElseGet(() -> {
                    logger.warn("No reservation found with ID: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    private ResponseEntity<ReservationDTO> updateAndSaveReservation(int id, ReservationDTO updatedReservationDTO) {
        House house = houseService.getOneById(updatedReservationDTO.getHouseId());
        User user = userService.getOneById(updatedReservationDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Reservation updatedReservation = reservationService.convertToEntity(updatedReservationDTO, house, user);
        updatedReservation.setReservationId(id);

        Reservation savedReservation = reservationService.saveReservation(updatedReservation);
        ReservationDTO savedReservationDTO = reservationService.convertToDto(savedReservation);

        return new ResponseEntity<>(savedReservationDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable int userId, @PathVariable int reservationId) {
        try {
            Reservation reservation = findReservationById(reservationId);
            User user = findUserById(userId);
            if (!isUserAuthorizedToDeleteReservation(user, reservation)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return deleteReservation(reservationId, userId);
        } catch (IllegalArgumentException e) {
            logger.warn("Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error deleting reservation", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Reservation findReservationById(int reservationId) {
        return reservationService.getReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation with ID: " + reservationId + " not found."));
    }

    private User findUserById(int userId) {
        return userService.getOneById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isUserAuthorizedToDeleteReservation(User user, Reservation reservation) {
        boolean isAuthorized = reservationService.isUserAuthorizedToDeleteReservation(user, reservation);
        if (!isAuthorized) {
            logger.warn("User with ID: {} is not authorized to delete reservation with ID: {}", user.getUserId(), reservation.getReservationId());
        }
        return isAuthorized;
    }

    private ResponseEntity<Void> deleteReservation(int reservationId, int userId) {
        if (reservationService.deleteReservationById(reservationId)) {
            logger.info("User with ID: {} deleted reservation with ID: {}", userId, reservationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            logger.warn("Failed to delete reservation with ID: {}", reservationId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveReservation(@RequestHeader("Authorization") String token, @RequestBody ReservationDTO reservationDTO) {
        try {
            User user = validateUserToken(token);
            validateReservationDetails(reservationDTO);
            House house = houseService.getOneById(reservationDTO.getHouseId());
            Reservation reservation = reservationService.convertToEntity(reservationDTO, house, user);

            return createReservation(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to create reservation: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating reservation", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User validateUserToken(String token) {
        return authorizationService.validate(UUID.fromString(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid token or email"));
    }

    private void validateReservationDetails(ReservationDTO reservationDTO) {
        reservationService.validateReservationDates(reservationDTO.getStartDate(), reservationDTO.getEndDate());
    }

    private ResponseEntity<?> createReservation(Reservation reservation) {
        Reservation savedReservation = reservationService.saveReservation(reservation);
        ReservationDTO savedReservationDTO = reservationService.convertToDto(savedReservation);

        logger.info("Created new reservation with ID: {}", savedReservation.getReservationId());
        return new ResponseEntity<>(savedReservationDTO, HttpStatus.CREATED);
    }

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

