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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value= "/api/houseTypes")
public class HouseTypeController {

    private final Logger logger = LoggerFactory.getLogger(HouseTypeController.class);
    private final HouseTypeService houseTypeService;

    @Autowired
    public HouseTypeController(HouseTypeService houseTypeService) {
        this.houseTypeService = houseTypeService;
        logger.info("Nieuwe HouseTypeController aangemaakt");
    }

    @GetMapping
    public List<HouseType> getAllHouseTypes() {
        return houseTypeService.getAllHouseTypes();
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getHouseTypeById(@PathVariable int id) {
        Optional<HouseType> houseType = houseTypeService.getHouseTypeById(id);
        if (houseType.isPresent()) {
            return new ResponseEntity<>(houseType.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("HouseType niet gevonden", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> createHouseType(@RequestBody Map<String, String> body) {
        String houseTypeName = body.get("name");
        HouseType newHouseType = new HouseType();
        newHouseType.setHouseTypeName(houseTypeName);

        try {
            houseTypeService.saveHouseType(newHouseType);
            return new ResponseEntity<>("HouseType succesvol aangemaakt!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Fout bij het aanmaken van HouseType", HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateHouseType(@PathVariable int id, @RequestBody HouseType houseType) {
        houseType.setHouseTypeId(id);
        boolean updated = houseTypeService.updateHouseType(houseType);
        if (updated) {
            return new ResponseEntity<>("HouseType succesvol bijgewerkt", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Mislukt om HouseType bij te werken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteHouseType(@PathVariable int id) {
        boolean deleted = houseTypeService.deleteHouseTypeById(id);
        if (deleted) {
            return new ResponseEntity<>("HouseType succesvol verwijderd", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Mislukt om HouseType te verwijderen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findHouseTypeByName(@PathVariable String name) {
        Optional<HouseType> houseType = houseTypeService.findHouseTypeByName(name);
        if (houseType.isPresent()) {
            return new ResponseEntity<>(houseType.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("HouseType niet gevonden", HttpStatus.NOT_FOUND);
        }
    }
}
