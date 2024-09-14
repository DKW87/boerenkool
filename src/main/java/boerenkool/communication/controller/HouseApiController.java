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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:30
 */
@RestController
@RequestMapping(value = "/api/houses")
public class HouseApiController {

    private final Logger logger = LoggerFactory.getLogger(HouseApiController.class);
    private final HouseService houseService;
    private final AuthorizationService authorizationService;
    private final ReservationService reservationService;

    @Autowired
    public HouseApiController(HouseService houseService, AuthorizationService authorizationService,
                              ReservationService reservationService) {
        this.houseService = houseService;
        this.authorizationService = authorizationService;
        this.reservationService = reservationService;
        logger.info("New HouseApiController");
    }

    @GetMapping
    public ResponseEntity<?> getAllHouses() {
        List<House> allHouses = houseService.getAllHouses();
        return allHouses.isEmpty()
                ? new ResponseEntity<>("No houses in Database", HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(allHouses, HttpStatus.OK);
    }

    @GetMapping(value = "/{houseId}")
    public ResponseEntity<?> getOneHouseById(@PathVariable int houseId) {

        if (houseId <= 0) {
            return new ResponseEntity<>("House ID is invalid and cannot be 0 or negative", HttpStatus.BAD_REQUEST);
        }

        HouseDetailsDTO houseDetailsDTO = houseService.getOneByIdAndConvertToDTO(houseId);

        if (houseDetailsDTO == null) {
            return new ResponseEntity<>("House was not found", HttpStatus.NOT_FOUND);
        }
        System.out.println(houseDetailsDTO.getExtraFeatures());
        return new ResponseEntity<>(houseDetailsDTO, HttpStatus.OK);
    }

    @GetMapping("/l/{id}")
    public ResponseEntity<?> getListOfHousesByOwnerId(@PathVariable int id) {

        if (id <= 0) {
            return new ResponseEntity<>("Owner ID cannot be 0 or negative", HttpStatus.BAD_REQUEST);
        }

        List<HouseListDTO> listOfHousesByOwner = houseService.getListOfHousesByOwnerId(id);

        if (listOfHousesByOwner == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(listOfHousesByOwner, HttpStatus.OK);
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getUniquesCities() {
        List<String> uniqueCities = houseService.getUniqueCities();
        if (uniqueCities.isEmpty()) {
            return new ResponseEntity<>("No cities found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(uniqueCities, HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getHouseTypes() {
        List<HouseType> houseTypes = houseService.getAllHouseTypes();
        if (houseTypes.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(houseTypes, HttpStatus.OK);
    }

    @GetMapping(value = "/l/filter")
    public ResponseEntity<?> getListOfHousesByFilter(
            @RequestParam(name = "aankomst", required = false, defaultValue = "") LocalDate startDate,
            @RequestParam(name = "vertrek", required = false, defaultValue = "") LocalDate endDate,
            @RequestParam(name = "provincies", required = false, defaultValue = "") List<String> provinces,
            @RequestParam(name = "steden", required = false, defaultValue = "") List<String> cities,
            @RequestParam(name = "huis-typen", required = false, defaultValue = "") List<Integer> houseTypeIds,
            @RequestParam(name = "huis-eigenaar", required = false, defaultValue = "0") int houseOwnerId,
            @RequestParam(name = "aantal-gasten", required = false, defaultValue = "0") int amountOfGuests,
            @RequestParam(name = "aantal-kamers", required = false, defaultValue = "0") int desiredRoomCount,
            @RequestParam(name = "minimum-prijs-per-persoon-per-nacht", required = false, defaultValue = "0") int minPricePPPD,
            @RequestParam(name = "maximum-prijs-per-persoon-per-nacht", required = false, defaultValue = "0") int maxPricePPPD,
            @RequestParam(name = "sorteer-op", required = false, defaultValue = "houseId") String sortBy,
            @RequestParam(name = "sorteer-orde", required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "12") int limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {

        HouseFilter filter = new HouseFilter.Builder()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setProvinces(provinces)
                .setCities(cities)
                .setHouseTypeIds(houseTypeIds)
                .setHouseOwner(houseOwnerId)
                .setAmountOfGuests(amountOfGuests)
                .setDesiredRoomCount(desiredRoomCount)
                .setMinPricePPPD(minPricePPPD)
                .setMaxPricePPPD(maxPricePPPD)
                .setSortBy(sortBy)
                .setSortOrder(sortOrder)
                .setLimit(limit)
                .setOffset(offset)
                .build();

        List<HouseListDTO> filteredHouses = houseService.getFilteredListOfHouses(filter);

        return filteredHouses.isEmpty()
                ? new ResponseEntity<>(filteredHouses, HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(filteredHouses, HttpStatus.OK);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<?> saveNewHouse(@RequestHeader("Authorization") String token, @RequestBody HouseDetailsDTO house) {
        Optional<User> optionalUser = authorizationService.validate(UUID.fromString(token));
        if (optionalUser.isPresent()) {
            if (house == null) {
                return new ResponseEntity<>("House cannot be null", HttpStatus.BAD_REQUEST);
            } else if (house.getHouseType() == null || house.getHouseOwnerId() == 0) {
                return new ResponseEntity<>("No user/housetype attached, incomplete House", HttpStatus.BAD_REQUEST);
            } else {
                return houseService.saveHouse(house)
                        ? new ResponseEntity<>(house.getHouseId(), HttpStatus.CREATED)
                        : new ResponseEntity<>("Unable to store new house", HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping(value = "/{houseId}")
    public ResponseEntity<?> updateHouse(@RequestHeader("Authorization") String token, @RequestBody HouseDetailsDTO house) {
        Optional<User> optionalUser = authorizationService.validate(UUID.fromString(token));
        if (optionalUser.isPresent()) {
            int houseId = house.getHouseId();
            int houseOwnerId = optionalUser.get().getUserId();

            if (houseId <= 0 || houseOwnerId <= 0) {
                return new ResponseEntity<>("ID's cannot not be 0 or negative", HttpStatus.BAD_REQUEST);
            } else if (house == null) {
                return new ResponseEntity<>("House cannot be null", HttpStatus.BAD_REQUEST);
            } else if (house.getHouseType() == null || house.getHouseOwnerId() == 0) {
                return new ResponseEntity<>("No user/housetype attached, incomplete House", HttpStatus.BAD_REQUEST);
            } else {
                if (houseOwnerId == house.getHouseOwnerId()) {
                    return houseService.saveHouse(house)
                            ? new ResponseEntity<>("House successfully modified", HttpStatus.OK)
                            : new ResponseEntity<>("Unable to update this house", HttpStatus.CONFLICT);
                }
                return new ResponseEntity<>("Unauthorized to update this house", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping(value = "/{houseId}")
    public ResponseEntity<?> deleteHouse(@RequestHeader("Authorization") String token, @PathVariable int houseId) {
        Optional<User> optionalUser = authorizationService.validate(UUID.fromString(token));
        if (optionalUser.isPresent()) {
            int houseOwnerId = optionalUser.get().getUserId();
            if (houseId <= 0 || houseOwnerId <= 0) {
                return new ResponseEntity<>("ID's cannot not be 0 or negative", HttpStatus.BAD_REQUEST);
            }
            House house = houseService.getOneById(houseId);
            if (house == null) {
                return new ResponseEntity<>("House was not found", HttpStatus.NOT_FOUND);
            } else if (houseOwnerId != house.getHouseOwner().getUserId()) {
                return new ResponseEntity<>("Not authorized to delete this house", HttpStatus.FORBIDDEN);
            } else if (!reservationService.getAllReservationsByHouseId(houseId).isEmpty()) {
              return new ResponseEntity<>("House is reserved and may not be deleted", HttpStatus.CONFLICT);
            } else {
                return houseService.deleteHouse(houseId)
                        ? new ResponseEntity<>("Successfully deleted house", HttpStatus.OK)
                        : new ResponseEntity<>("Unable to delete house", HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    }

} // class
