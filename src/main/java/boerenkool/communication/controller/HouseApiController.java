package boerenkool.communication.controller;

import boerenkool.business.model.House;
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

    @GetMapping(value = "/{houseOwnerId}")
    public List<House> getListOfHousesByHouseOwnerId(@PathVariable int houseOwnerId) {
        return houseService.getListOfHousesByOwnerId(houseOwnerId);
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
