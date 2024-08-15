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
@RequestMapping(value = "/api/houses")
public class HouseApiController {

    private final Logger logger = LoggerFactory.getLogger(HouseApiController.class);
    private final HouseService houseService;

    @Autowired
    public HouseApiController(HouseService houseService) {
        this.houseService = houseService;
        logger.info("New HouseApiController");
    }

    @GetMapping
    public List<House> getAllHouses() {
        return houseService.getAllHouses();
    }

    @GetMapping(value = "/owner")
    public List<House> getListOfHousesByHouseOwnerId(@RequestParam int id) {
        return houseService.getListOfHousesByOwnerId(id);
    }

    @GetMapping(value = "/filter")
    public List<House> getListOfHousesByFilter(
            @RequestParam(required = false, defaultValue = "") List<String> provinces,
            @RequestParam(required = false, defaultValue = "") List<String> cities,
            @RequestParam(required = false, defaultValue = "") List<Integer> houseTypeIds,
            @RequestParam(required = false, defaultValue = "0") int houseOwnerId,
            @RequestParam(required = false, defaultValue = "0") int amountOfGuests,
            @RequestParam(required = false, defaultValue = "0") int desiredRoomCount,
            @RequestParam(required = false, defaultValue = "0") int minPricePPPD,
            @RequestParam(required = false, defaultValue = "0") int maxPricePPPD,
            @RequestParam(required = false, defaultValue = "houseId") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") int limit,
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

        return houseService.getFilteredListOfHouses(filter);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<?> saveNewHouse(@RequestBody House house) {
        if (house == null) {
            return new ResponseEntity<>("No house found", HttpStatus.NOT_FOUND);
        }
        else {
            return houseService.saveHouse(house)
                    ? new ResponseEntity<>("House successfully created: " + house, HttpStatus.CREATED)
                    : new ResponseEntity<>("Unable to save house: " + house, HttpStatus.CONFLICT);
        }
    }

}
