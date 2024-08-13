package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.service.HouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:30
 */
@RestController
public class HouseApiController {

    private final Logger logger = LoggerFactory.getLogger(HouseApiController.class);
    private final HouseService houseService;

    @Autowired
    public HouseApiController(HouseService houseService) {
        this.houseService = houseService;
        logger.info("New HouseApiController");
    }

    @GetMapping("hoi")
    public String hoi() {
        System.out.println("Kijken of dit naar de console geprint wordt...");
        return "Welkom bij Huisje, Boompje, Boerenkool. Dé geur van thuis!";
    }

    @GetMapping("all_houses")
    public List<House> getAllHouses() {
        return houseService.getAllHouses();
    }

}
