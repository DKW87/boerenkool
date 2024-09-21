package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import boerenkool.database.repository.HouseRepository;
import boerenkool.database.repository.ReservationRepository;
import boerenkool.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseRepository houseRepository;

    private House testHouse;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup a test house
        testHouse = new House();
        testHouse.setHouseId(1);
        testHouse.setPricePPPD(100); // Price per person per day
        testHouse.setMaxGuest(4);
        testHouse.setHouseName("Test House");
        houseRepository.saveHouse(testHouse);

        // Setup a test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setCoinBalance(1000); // Example balance
        testUser.setTypeOfUser("Huurder");
        userRepository.storeOne(testUser);
    }

    @Test
    void getAllReservations() {
        Reservation reservation1 = createTestReservation();
        reservationRepository.saveReservation(reservation1);

        Reservation reservation2 = createTestReservation();
        reservationRepository.saveReservation(reservation2);

        assertEquals(2, reservationService.getAllReservations().size());
    }

    @Test
    void getReservationById() {
        Reservation reservation = createTestReservation();
        reservation = reservationRepository.saveReservation(reservation);

        Optional<Reservation> retrievedReservation = reservationService.getReservationById(reservation.getReservationId());
        assertTrue(retrievedReservation.isPresent());
        assertEquals(reservation.getReservationId(), retrievedReservation.get().getReservationId());
    }

    @Test
    void saveReservation() {
        Reservation reservation = createTestReservation();
        Reservation savedReservation = reservationService.saveReservation(reservation);

        assertNotNull(savedReservation);
        assertEquals(reservation.getStartDate(), savedReservation.getStartDate());
        assertEquals(reservation.getEndDate(), savedReservation.getEndDate());
        assertEquals(reservation.getGuestCount(), savedReservation.getGuestCount());
    }

    @Test
    void calculateReservationCost() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        int guestCount = 2;
        int cost = reservationService.calculateReservationCost(startDate, endDate, testHouse.getHouseId(), guestCount);
        assertEquals(600, cost); // 100 * 2 days * 3 guests
    }

    @Test
    void validateUserBudget() {
        int totalCost = 200;
        assertDoesNotThrow(() -> reservationService.validateUserBudget(totalCost, testUser));

        testUser.setCoinBalance(100); // Set balance to less than cost
        userRepository.storeOne(testUser);

        assertThrows(IllegalArgumentException.class, () -> reservationService.validateUserBudget(totalCost, testUser));
    }

    @Test
    void validateReservationDates() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        assertDoesNotThrow(() -> reservationService.validateReservationDates(startDate, endDate));

        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> reservationService.validateReservationDates(pastDate, endDate));
    }

    @Test
    void deleteReservationById() {
        Reservation reservation = createTestReservation();
        reservation = reservationRepository.saveReservation(reservation);

        assertTrue(reservationService.deleteReservationById(reservation.getReservationId()));
        assertFalse(reservationRepository.getReservationById(reservation.getReservationId()).isPresent());
    }


    @Test
    void getAllReservationsByHouseId() {
        Reservation reservation = createTestReservation();
        reservationRepository.saveReservation(reservation);

        assertEquals(1, reservationService.getAllReservationsByHouseId(testHouse.getHouseId()).size());
    }

    @Test
    void getReservationsByUserId() {
        Reservation reservation = createTestReservation();
        reservationRepository.saveReservation(reservation);

        assertEquals(1, reservationService.getReservationsByUserId(testUser.getUserId()).size());
    }

    @Test
    void isUserAuthorizedToDeleteReservation() {
        Reservation reservation = createTestReservation();
        reservation = reservationRepository.saveReservation(reservation);

        assertTrue(reservationService.isUserAuthorizedToDeleteReservation(testUser, reservation));
    }

    private Reservation createTestReservation() {
        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.now().plusDays(1));
        reservation.setEndDate(LocalDate.now().plusDays(3));
        reservation.setGuestCount(2);
        reservation.setHouse(testHouse);
        reservation.setReservedByUser(testUser);
        return reservation;
    }
}
