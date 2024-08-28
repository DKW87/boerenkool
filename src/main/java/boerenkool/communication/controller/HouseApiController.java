package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import boerenkool.business.service.HouseService;
import boerenkool.communication.dto.HouseListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
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

    @GetMapping("/{houseId}")
    public RedirectView redirectToHouseName(@PathVariable int houseId) {
        if (houseId <= 0) {
            return new RedirectView("/e/huis-id-incorrect");
        }

        House house = houseService.getOneById(houseId);
        if (house == null) {
            return new RedirectView("/e/huis-bestaat-niet");
        }

        String houseName = house.getHouseName();
        String seoFriendlyName = houseName.toLowerCase().replace(" ", "-")
                .replaceAll("[^a-z0-9\\-]", "");

        return new RedirectView("/api/huizen/" + houseId + "/" + seoFriendlyName);
    }


    @GetMapping(value = "/{houseId}/{houseName}")
    public ResponseEntity<?> getOneHouseByIdAndName(
            @PathVariable int houseId,
            @PathVariable String houseName) {

        if (houseId <= 0) {
            return new ResponseEntity<>("House ID is invalid and cannot be 0 or negative", HttpStatus.BAD_REQUEST);
        }
        House house = houseService.getOneById(houseId);
        if (house == null) {
            return new ResponseEntity<>("House was not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(house, HttpStatus.OK);
    }

    @GetMapping("/l/{id}")
    public RedirectView getListOfHousesByOwnerId(@PathVariable int id) {
        if (id <= 0) {
            return new RedirectView("/e/huis-eigenaar-id-incorrect");
        }

        List<House> listOfHousesByOwner = houseService.getListOfHousesByOwnerId(id);

        String ownerName = houseService.getHouseOwnerName(id);
        String seoFriendlyName = ownerName.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9\\-]", "");

        URI location = URI.create(String.format("/api/huizen/%d/%s", id, seoFriendlyName));
        return new RedirectView("/api/huizen/l/" + id + "/" + seoFriendlyName);
    }

    @GetMapping("/l/{id}/{houseOwnerName}")
    public ResponseEntity<?> getListOfHousesByOwnerIdAndName(
            @PathVariable int id,
            @PathVariable String houseOwnerName) {

        if (id <= 0) {
            return new ResponseEntity<>("Owner ID cannot be 0 or negative", HttpStatus.BAD_REQUEST);
        }

        List<House> listOfHousesByOwner = houseService.getListOfHousesByOwnerId(id);
        if (listOfHousesByOwner.isEmpty()) {
            return new ResponseEntity<>("No houses belong to this owner", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(listOfHousesByOwner, HttpStatus.OK);
    }

    @GetMapping("/steden")
    public ResponseEntity<?> getUniquesCities() {
        List<String> uniqueCities = houseService.getUniqueCities();
        if (uniqueCities.isEmpty()) {
            return new ResponseEntity<>("No cities found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(uniqueCities, HttpStatus.OK);
    }

    @GetMapping("/typen")
    public ResponseEntity<?> getHouseTypes() {
        List<HouseType> houseTypes = houseService.getAllHouseTypes();
        if (houseTypes.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(houseTypes, HttpStatus.OK);
    }

    @GetMapping(value = "/filter")
    public ResponseEntity<?> getListOfHousesByFilter(
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

    @PutMapping(value = "/{houseId}")
    public ResponseEntity<?> updateHouse(@PathVariable int houseId,
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

    @DeleteMapping(value = "/{houseId}")
    public ResponseEntity<?> deleteHouse(@PathVariable int houseId,
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
