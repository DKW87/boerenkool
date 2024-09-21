package boerenkool.database.repository;

import boerenkool.business.model.*;
import boerenkool.database.dao.mysql.*;
import boerenkool.database.dao.mysql.PictureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 11:54
 */
@Repository
public class HouseRepository {

    private final Logger logger = LoggerFactory.getLogger(HouseRepository.class);
    private final HouseDAO houseDAO;
    private final PictureDAO pictureDAO;
    private final ExtraFeatureDAO extraFeatureDAO;
    private final UserDAO userDAO;
    private final HouseTypeDAO houseTypeDAO;

    @Autowired
    public HouseRepository(HouseDAO houseDAO, PictureDAO pictureDAO, ExtraFeatureDAO extraFeatureDAO,
                           UserDAO userDAO, HouseTypeDAO houseTypeDAO) {
        logger.info("New HouseRepository");
        this.houseDAO = houseDAO;
        this.pictureDAO = pictureDAO;
        this.extraFeatureDAO = extraFeatureDAO;
        this.userDAO = userDAO;
        this.houseTypeDAO = houseTypeDAO;
    }

    public List<House> getListOfHousesByOwner(int ownerId) {
        List<House> allHouses = houseDAO.getHousesByOwner(ownerId);
        setListDependencies(allHouses);
        return allHouses;
    }

    public List<House> getHousesWithFilter(HouseFilter filter) {
        List<House> allHouses = houseDAO.getHousesByFilter(filter);
        setListDependencies(allHouses);
        return allHouses;
    }

    public int countHousesWithFilter(HouseFilter filter) {
        return houseDAO.countHousesByFilter(filter);
    }

    public List<String> getUniqueCities() {
        return houseDAO.getUniqueCities();
    }

    public List<HouseType> getAllHouseTypes() {
        return houseTypeDAO.getAll();
    }

    public Optional<House> getHouseById(int houseId) {
        Optional<House> optionalHouse = houseDAO.getOneById(houseId);
        setOneDependencies(optionalHouse);
        return optionalHouse;
    }

    public boolean saveHouse(House house) {
        return house.getHouseId() == 0 ? storeNewHouse(house) : updateHouse(house);
    }

    private boolean storeNewHouse(House house) {
        return houseDAO.storeOne(house);
    }

    private boolean updateHouse(House house) {
        return houseDAO.updateOne(house);
    }

    public boolean deleteHouse(int houseId) {
        return houseDAO.removeOneById(houseId);
    }

    private void setListDependencies(List<House> allHouses) {
        for (House house : allHouses) {
            house.setHouseOwner(userDAO.getOneById(house.accessOtherEntityIds().getHouseOwnerId())
                    .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            house.setHouseType(houseTypeDAO.getOneById(house.accessOtherEntityIds().getHouseTypeId())
                    .orElseThrow(() -> new NoSuchElementException("houseType not found")));
            house.setExtraFeatures(extraFeatureDAO.getExtraFeaturesByHouseId(house.getHouseId()));
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));
        }
    }

    private void setOneDependencies(Optional<House> optionalHouse) {
        if (optionalHouse.isPresent()) {
            optionalHouse.get()
                    .setHouseType(houseTypeDAO.getOneById(optionalHouse.get().accessOtherEntityIds().getHouseTypeId())
                            .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            optionalHouse.get()
                    .setHouseOwner(userDAO.getOneById(optionalHouse.get().accessOtherEntityIds().getHouseOwnerId())
                            .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            optionalHouse.get()
                    .setExtraFeatures(extraFeatureDAO.getExtraFeaturesByHouseId(optionalHouse.get().getHouseId()));
            optionalHouse.get()
                    .setPictures(pictureDAO.getAllByHouseId(optionalHouse.get().getHouseId()));
        }
    }

} // class
