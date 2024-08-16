package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.service.HouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:30
 */
@RestController
@RequestMapping(value = "/api/huizen")
public class HouseApiController {

    private final Logger logger = LoggerFactory.getLogger(HouseApiController.class);
    private final HouseService houseService;

    @Autowired
    public HouseApiController(HouseService houseService) {
        this.houseService = houseService;
        logger.info("New HouseApiController");
    }

    @GetMapping
    public ResponseEntity<?> getAllHouses() {
        List<House> allHouses = houseService.getAllHouses();
        return allHouses.isEmpty()
                ? new ResponseEntity<>("No houses in Database", HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(allHouses, HttpStatus.OK);
    }

    @GetMapping(value = "/vind-een-op")
    public ResponseEntity<?> getOneHouseById(@RequestParam(name = "huisId") int houseId) {
        if (houseId <= 0) {
            return new ResponseEntity<>("House ID is invalid and cannot be 0 or negative", HttpStatus.BAD_REQUEST);
        }
        House house = houseService.getOneById(houseId);
        if (house == null) {
            return new ResponseEntity<>("House was not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(house, HttpStatus.OK);
    }

    @GetMapping(value = "/vind-lijst-op")
    public ResponseEntity<?> getListOfHousesByHouseOwnerId(@RequestParam(name = "eigenaarId") int id) {
        if (id <= 0) {
            return new ResponseEntity<>("Owner ID cannot not be 0 or negative", HttpStatus.BAD_REQUEST);
        }
        List<House> listOfHousesByOwner = houseService.getListOfHousesByOwnerId(id);
        return listOfHousesByOwner.isEmpty()
                ? new ResponseEntity<>("No houses belong to this owner", HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(listOfHousesByOwner, HttpStatus.OK);
    }

    @GetMapping(value = "/filter")
    public ResponseEntity<?> getListOfHousesByFilter(
            @RequestParam(name = "provincies", required = false, defaultValue = "") List<String> provinces,
            @RequestParam(name = "steden", required = false, defaultValue = "") List<String> cities,
            @RequestParam(name = "typen", required = false, defaultValue = "") List<Integer> houseTypeIds,
            @RequestParam(name = "eigenaar", required = false, defaultValue = "0") int houseOwnerId,
            @RequestParam(name = "aantal-gasten", required = false, defaultValue = "0") int amountOfGuests,
            @RequestParam(name = "aantal-kamers", required = false, defaultValue = "0") int desiredRoomCount,
            @RequestParam(name = "minimum-prijs-per-persoon-per-dag", required = false, defaultValue = "0") int minPricePPPD,
            @RequestParam(name = "maximum-prijs-per-persoon-per-dag", required = false, defaultValue = "0") int maxPricePPPD,
            @RequestParam(name = "sorteer-op", required = false, defaultValue = "") String sortBy,
            @RequestParam(name = "sorteer-orde", required = false, defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {

        HouseFilter filter = new HouseFilter.Builder()
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

        List<House> filteredHouses = houseService.getFilteredListOfHouses(filter);
        return filteredHouses.isEmpty()
                ? new ResponseEntity<>("No houses match your criteria", HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(filteredHouses, HttpStatus.OK);
    }

    @PostMapping(value = "/nieuw")
    public ResponseEntity<?> saveNewHouse(@RequestBody House house) {
        if (house == null) {
            return new ResponseEntity<>("House cannot be null", HttpStatus.BAD_REQUEST);
        } else if (house.getHouseType() == null || house.getHouseOwner() == null) {
            return new ResponseEntity<>("No user/housetype attached, incomplete House", HttpStatus.BAD_REQUEST);
        } else {
            return houseService.saveHouse(house)
                    ? new ResponseEntity<>("House successfully created", HttpStatus.CREATED)
                    : new ResponseEntity<>("Unable to store new house", HttpStatus.CONFLICT);
        }
    }

    @PutMapping(value = "/bewerk-waar")
    public ResponseEntity<?> updateHouse(@RequestParam(name = "huisId") int houseId,
                                         @RequestParam(name = "huis-eigenaarId") int houseOwnerId,
                                         @RequestBody House house) {
        if (houseId <= 0 || houseOwnerId <= 0) {
            return new ResponseEntity<>("ID's cannot not be 0 or negative", HttpStatus.BAD_REQUEST);
        } else if (house == null) {
            return new ResponseEntity<>("House cannot be null", HttpStatus.BAD_REQUEST);
        } else if (house.getHouseType() == null || house.getHouseOwner() == null) {
            return new ResponseEntity<>("No user/housetype attached, incomplete House", HttpStatus.BAD_REQUEST);
        } else {
            if (houseOwnerId == house.getHouseOwner().getUserId()) {
                return houseService.saveHouse(house)
                        ? new ResponseEntity<>("House successfully modified", HttpStatus.OK)
                        : new ResponseEntity<>("Unable to update this house", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Unauthorized to update this house", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "/verwijder-waar")
    public ResponseEntity<?> deleteHouse(@RequestParam(name = "huisId") int houseId,
                                         @RequestParam(name = "huis-eigenaarId") int houseOwnerId) {
        if (houseId <= 0 || houseOwnerId <= 0) {
            return new ResponseEntity<>("ID's cannot not be 0 or negative", HttpStatus.BAD_REQUEST);
        }
        House house = houseService.getOneById(houseId);
        if (house == null) {
            return new ResponseEntity<>("House was not found", HttpStatus.NOT_FOUND);
        } else if (houseOwnerId != house.getHouseOwner().getUserId()) {
            return new ResponseEntity<>("Not authorized to delete this house", HttpStatus.FORBIDDEN);
        } else {
            // TODO check of huis gereserveerd is
                return houseService.deleteHouse(houseId)
                        ? new ResponseEntity<>("Successfully deleted house", HttpStatus.OK)
                        : new ResponseEntity<>("Unable to delete house", HttpStatus.CONFLICT);
        }
    }

} // class
