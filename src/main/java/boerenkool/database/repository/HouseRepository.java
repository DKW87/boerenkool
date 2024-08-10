package boerenkool.database.repository;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.database.dao.mysql.HouseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 11:54
 */
public class HouseRepository {

    // TODO getListOfHousesWithPictures when PictureDAO is implemented

    private final Logger logger = LoggerFactory.getLogger(HouseRepository.class);
    private final HouseDAO houseDAO;

    public HouseRepository(HouseDAO houseDAO) {
        logger.info("New HouseRepository");
        this.houseDAO = houseDAO;
    }

    public List<House> getListOfAllHouses() {
        return houseDAO.getAll();
    }

    public List<House> getListOfAllHousesByOwner(int ownerId) {
        return houseDAO.getAllHousesByOwner(ownerId);
    }

    public List<House> getLimitedListOfHouses(int limit, int offset) {
        return houseDAO.getLimitedList(limit, offset);
    }

    public List<House> getHousesWithFilter(HouseFilter filter) {
        return houseDAO.getHousesWithFilter(filter);
    }

    public Optional<House> getHouse(int houseId) {
        return houseDAO.getOneById(houseId);
    }

    public void saveHouse(House house) {
        if (house.getHouseId() == 0) {
            houseDAO.storeOne(house);
        }
        else {
            houseDAO.updateOne(house);
        }
    }

    public boolean deleteHouse(int houseId) {
        return houseDAO.removeOneById(houseId);
    }

} // class
