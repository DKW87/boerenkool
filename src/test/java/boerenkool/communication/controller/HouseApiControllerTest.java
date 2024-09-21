package boerenkool.communication.controller;

import boerenkool.business.model.*;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.ReservationService;
import boerenkool.communication.dto.HouseDetailsDTO;
import boerenkool.communication.dto.HouseListDTO;
import boerenkool.utilities.authorization.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 18/09/2024 - 14:38
 */
class HouseApiControllerTest {

    @Mock
    private HouseService houseService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private HouseApiController houseApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_getOneHouseById_statusOK() {
        int houseId = 1;
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        when(houseService.getOneByIdToDTO(houseId)).thenReturn(houseDetails);

        ResponseEntity<?> response = houseApiController.getOneHouseById(houseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getOneHouseById_statusBAD_REQUEST() {
        int houseId = 0;
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        when(houseService.getOneByIdToDTO(houseId)).thenReturn(houseDetails);

        ResponseEntity<?> response = houseApiController.getOneHouseById(houseId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_getOneHouseById_statusNOT_FOUND() {
        int houseId = 1;
        HouseDetailsDTO houseDetails = null;
        when(houseService.getOneByIdToDTO(houseId)).thenReturn(houseDetails);

        ResponseEntity<?> response = houseApiController.getOneHouseById(houseId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByOwnerId_statusOK() {
        int ownerId = 1;
        List<HouseListDTO> houses = Arrays.asList(new HouseListDTO());
        when(houseService.getListByOwnerId(ownerId)).thenReturn(houses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByOwnerId(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByOwnerId_statusBAD_REQUEST() {
        int ownerId = 0;
        List<HouseListDTO> houses = Arrays.asList(new HouseListDTO());
        when(houseService.getListByOwnerId(ownerId)).thenReturn(houses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByOwnerId(ownerId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByOwnerId_statusNO_CONTENT() {
        int ownerId = 1;
        List<HouseListDTO> houses = null;
        when(houseService.getListByOwnerId(ownerId)).thenReturn(houses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByOwnerId(ownerId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_getUniquesCities_statusOK() {
        List<String> cities = Arrays.asList("Amsterdam", "Rotterdam");
        when(houseService.getUniqueCities()).thenReturn(cities);

        ResponseEntity<?> response = houseApiController.getUniquesCities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getUniquesCities_statusNO_CONTENT() {
        List<String> cities = new ArrayList<>();
        when(houseService.getUniqueCities()).thenReturn(cities);

        ResponseEntity<?> response = houseApiController.getUniquesCities();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_getHouseTypes_statusOK() {
        List<HouseType> houseTypes = Arrays.asList(new HouseType());
        when(houseService.getAllHouseTypes()).thenReturn(houseTypes);

        ResponseEntity<?> response = houseApiController.getHouseTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getHouseTypes_statusNO_CONTENT() {
        List<HouseType> houseTypes = new ArrayList<>();
        when(houseService.getAllHouseTypes()).thenReturn(houseTypes);

        ResponseEntity<?> response = houseApiController.getHouseTypes();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByFilter_statusOK() {
        List<HouseListDTO> filteredHouses = Arrays.asList(new HouseListDTO());
        when(houseService.getFilteredList(any(HouseFilter.class))).thenReturn(filteredHouses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByFilter(
                "", "",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                0,
                0,
                0,
                0,
                0,
                "houseId",
                "DESC",
                12,
                0,
                false
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByFilter_statusNO_CONTENT() {
        List<HouseListDTO> filteredHouses = new ArrayList<>();
        when(houseService.getFilteredList(any(HouseFilter.class))).thenReturn(filteredHouses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByFilter(
                "", "",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                0,
                0,
                0,
                0,
                0,
                "houseId",
                "DESC",
                12,
                0,
                false
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByFilter_statusOK_withCount() {
        int houseCount = 5;
        when(houseService.countFilterResult(any(HouseFilter.class))).thenReturn(houseCount);

        ResponseEntity<?> response = houseApiController.getListOfHousesByFilter(
                "", "",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                0,
                0,
                0,
                0,
                0,
                "houseId",
                "DESC",
                12,
                0,
                true
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void test_saveNewHouse_statusCREATED() {
        String token = UUID.randomUUID().toString();

        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);

        HouseType houseType = new HouseType();
        houseDetails.setHouseType(houseType);

        User user = new User();
        user.setUserId(1);

        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.saveHouse(houseDetails)).thenReturn(true);

        ResponseEntity<?> response = houseApiController.saveNewHouse(token, houseDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_saveNewHouse_statusUNAUTHORIZED() {
        String token = UUID.randomUUID().toString();

        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);

        HouseType houseType = new HouseType();
        houseDetails.setHouseType(houseType);

        Optional<User> optionalUser = Optional.empty();

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.saveNewHouse(token, houseDetails);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void test_saveNewHouse_statusBAD_REQUEST() {
        String token = UUID.randomUUID().toString();

        HouseDetailsDTO houseDetails = null;

        User user = new User();
        user.setUserId(1);

        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.saveNewHouse(token, houseDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_saveNewHouse_statusBAD_REQUEST2() {
        String token = UUID.randomUUID().toString();

        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseType(null);

        User user = new User();
        user.setUserId(1);

        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.saveNewHouse(token, houseDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_updateHouse_statusOK() {
        String token = UUID.randomUUID().toString();

        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);
        houseDetails.setHouseId(1);

        HouseType houseType = new HouseType();
        houseDetails.setHouseType(houseType);

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.saveHouse(houseDetails)).thenReturn(true);

        ResponseEntity<?> response = houseApiController.updateHouse(token, houseDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_updateHouse_statusBAD_REQUEST() {
        String token = UUID.randomUUID().toString();
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(-1);
        houseDetails.setHouseId(-1);

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.updateHouse(token, houseDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_updateHouse_statusBAD_REQUEST2() {
        String token = UUID.randomUUID().toString();
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);
        houseDetails.setHouseId(1);

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.updateHouse(token, houseDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_updateHouse_statusUNAUTHORIZED() {
        String token = UUID.randomUUID().toString();
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);
        houseDetails.setHouseId(1);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(Optional.empty());

        ResponseEntity<?> response = houseApiController.updateHouse(token, houseDetails);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void test_updateHouse_statusCONFLICT() {
        String token = UUID.randomUUID().toString();
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        houseDetails.setHouseOwnerId(1);
        houseDetails.setHouseId(1);

        HouseType houseType = new HouseType();
        houseDetails.setHouseType(houseType);

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.saveHouse(houseDetails)).thenReturn(false);

        ResponseEntity<?> response = houseApiController.updateHouse(token, houseDetails);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusOK() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;
        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);
        House house = new House();
        HouseType houseType = new HouseType();
        house.setHouseOwner(optionalUser.get());
        house.setHouseType(houseType);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.getOneById(houseId)).thenReturn(house);
        when(reservationService.getAllReservationsByHouseId(houseId)).thenReturn(Collections.emptyList());
        when(houseService.deleteHouse(houseId)).thenReturn(true);

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusBAD_REQUEST() {
        String token = UUID.randomUUID().toString();
        int houseId = -1;

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusUNAUTHORIZED() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(Optional.empty());

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusNOT_FOUND() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.getOneById(houseId)).thenReturn(null);

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusFORBIDDEN() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;

        User user = new User();
        user.setUserId(2);
        Optional<User> optionalUser = Optional.of(user);

        House house = new House();
        User owner = new User();
        owner.setUserId(1);
        house.setHouseOwner(owner);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.getOneById(houseId)).thenReturn(house);

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusCONFLICT() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        House house = new House();
        house.setHouseOwner(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.getOneById(houseId)).thenReturn(house);
        when(reservationService.getAllReservationsByHouseId(houseId)).thenReturn(Arrays.asList(new Reservation()));

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void test_deleteHouse_statusCONFLICT2() {
        String token = UUID.randomUUID().toString();
        int houseId = 1;

        User user = new User();
        user.setUserId(1);
        Optional<User> optionalUser = Optional.of(user);

        House house = new House();
        house.setHouseOwner(user);

        when(authorizationService.validate(UUID.fromString(token))).thenReturn(optionalUser);
        when(houseService.getOneById(houseId)).thenReturn(house);
        when(reservationService.getAllReservationsByHouseId(houseId)).thenReturn(Collections.emptyList());
        when(houseService.deleteHouse(houseId)).thenReturn(false);

        ResponseEntity<?> response = houseApiController.deleteHouse(token, houseId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


} // HouseApiControllerTest