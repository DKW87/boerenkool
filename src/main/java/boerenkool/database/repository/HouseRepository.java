package boerenkool.database.repository;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.database.dao.mysql.*;
import boerenkool.database.dao.mysql.PictureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 11:54
 */
@Repository
public class HouseRepository {

    // TODO getListOfHousesWithPictures when PictureDAO is implemented

    private final Logger logger = LoggerFactory.getLogger(HouseRepository.class);
    private final HouseDAO houseDAO;
    private final PictureDAO pictureDAO;
    private final HouseExtraFeatureDAO houseExtraFeatureDAO;

    @Autowired
    public HouseRepository(HouseDAO houseDAO, PictureDAO pictureDAO, HouseExtraFeatureDAO houseExtraFeatureDAO) {
        logger.info("New HouseRepository");
        this.houseDAO = houseDAO;
        this.pictureDAO = pictureDAO;
        this.houseExtraFeatureDAO = houseExtraFeatureDAO;
    }

    public List<House> getListOfAllHouses() {
        List<House> allHouses = houseDAO.getAll();
        for (House house : allHouses) {
            // TODO @Emine > T for getAllFeaturesByHouseId = ExtraFeature
//            house.setExtraFeatures(houseExtraFeatureDAO.getAllFeaturesByHouseId(house.getHouseId()));
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));
        }
        return houseDAO.getAll();
    }

    public List<House> getListOfAllHousesWithFirstPicture() {
        List<House> allHouses = getListOfAllHouses();
        for (House house : allHouses) {
//            house.setExtraFeatures(houseExtraFeatureDAO.getAllFeaturesByHouseId(house.getHouseId()));
            // TODO easy solution for first picture only? Maybe need extra DAO method
        }
        return allHouses;
    }

    public List<House> getListOfAllHousesByOwner(int ownerId) {
        List<House> allHouses = houseDAO.getAllHousesByOwner(ownerId);
        for (House house : allHouses) {
            // load all pics/feats right now
//            house.setExtraFeatures(houseExtraFeatureDAO.getAllFeaturesByHouseId(house.getHouseId()));
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));
        }
        return allHouses;
    }

    public List<House> getLimitedListOfHouses(int limit, int offset) {
        List<House> allHouses = houseDAO.getLimitedList(limit, offset);
        for (House house : allHouses) {
            // load all pics/feats right now
//            house.setExtraFeatures(houseExtraFeatureDAO.getAllFeaturesByHouseId(house.getHouseId()));
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));
        }
        return allHouses;
    }

    public List<House> getHousesWithFilter(HouseFilter filter) {
        List<House> allHouses = houseDAO.getHousesWithFilter(filter);
        for (House house : allHouses) {
            // load all pics/feats right now
//            house.setExtraFeatures(houseExtraFeatureDAO.getAllFeaturesByHouseId(house.getHouseId()));
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));
        }
        return allHouses;
    }

    public Optional<House> getHouse(int houseId) {
        return houseDAO.getOneById(houseId);
    }

    public void storeNewHouse(House house) {
        houseDAO.storeOne(house);
    }

    public boolean updateHouse(House house) {
        return houseDAO.updateOne(house);
    }

    public boolean deleteHouse(int houseId) {
        return houseDAO.removeOneById(houseId);
    }

} // class
