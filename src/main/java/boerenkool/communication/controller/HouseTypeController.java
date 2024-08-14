package boerenkool.communication.controller;

import boerenkool.business.model.HouseType;
import boerenkool.business.service.HouseTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/house-types")
public class HouseTypeController {

    private final Logger logger = LoggerFactory.getLogger(HouseTypeController.class);
    private final HouseTypeService houseTypeService;

    @Autowired
    public HouseTypeController(HouseTypeService houseTypeService) {
        this.houseTypeService = houseTypeService;
        logger.info("New HouseTypeController created");
    }

    @GetMapping
    public List<HouseType> getAllHouseTypes() {
        return houseTypeService.getAllHouseTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHouseTypeById(@PathVariable int id) {
        Optional<HouseType> houseType = houseTypeService.getHouseTypeById(id);
        if (houseType.isPresent()) {
            return new ResponseEntity<>(houseType.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("HouseType not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createHouseType(@RequestBody HouseType houseType) {
        try {
            HouseType savedHouseType = houseTypeService.saveHouseType(houseType);
            return new ResponseEntity<>(savedHouseType, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHouseType(@PathVariable int id, @RequestBody HouseType houseType) {
        houseType.setHouseTypeId(id);
        boolean updated = houseTypeService.updateHouseType(houseType);
        if (updated) {
            return new ResponseEntity<>("HouseType updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update HouseType", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouseType(@PathVariable int id) {
        boolean deleted = houseTypeService.deleteHouseTypeById(id);
        if (deleted) {
            return new ResponseEntity<>("HouseType deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete HouseType", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findHouseTypeByName(@PathVariable String name) {
        Optional<HouseType> houseType = houseTypeService.findHouseTypeByName(name);
        if (houseType.isPresent()) {
            return new ResponseEntity<>(houseType.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("HouseType not found", HttpStatus.NOT_FOUND);
        }
    }
}
