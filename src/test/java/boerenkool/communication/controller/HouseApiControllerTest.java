package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import boerenkool.business.model.User;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    void test_getOneHouseById() {
        int houseId = 1;
        HouseDetailsDTO houseDetails = new HouseDetailsDTO();
        when(houseService.getOneByIdAndConvertToDTO(houseId)).thenReturn(houseDetails);

        ResponseEntity<?> response = houseApiController.getOneHouseById(houseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getListOfHousesByOwnerId() {
        int ownerId = 1;
        List<HouseListDTO> houses = Arrays.asList(new HouseListDTO());
        when(houseService.getListOfHousesByOwnerId(ownerId)).thenReturn(houses);

        ResponseEntity<?> response = houseApiController.getListOfHousesByOwnerId(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getUniquesCities() {
        List<String> cities = Arrays.asList("Amsterdam", "Rotterdam");
        when(houseService.getUniqueCities()).thenReturn(cities);

        ResponseEntity<?> response = houseApiController.getUniquesCities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_getHouseTypes() {
        List<HouseType> houseTypes = Arrays.asList(new HouseType());
        when(houseService.getAllHouseTypes()).thenReturn(houseTypes);

        ResponseEntity<?> response = houseApiController.getHouseTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_saveNewHouse() {
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
    void test_updateHouse() {
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
    void test_deleteHouse() {
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

} // HouseApiControllerTest