package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.database.repository.HouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:23
 */
@Service
public class HouseService {

    private final Logger logger = LoggerFactory.getLogger(HouseService.class);
    private final HouseRepository houseRepository;

    @Autowired
    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
        logger.info("New HouseService");
    }

    public List<House> getAllHouses() {
        return houseRepository.getListOfAllHouses();
    }

    public List<House> getListOfHousesByOwnerId(int houseOwnerId) {
        return houseRepository.getListOfAllHousesByOwner(houseOwnerId);
    }

    public List<House> getFilteredListOfHouses(HouseFilter filter) {
        return houseRepository.getHousesWithFilter(filter);
    }

    public boolean saveHouse(House house) {
        return houseRepository.saveHouse(house);
    }

}
