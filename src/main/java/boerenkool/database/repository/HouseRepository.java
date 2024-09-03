package boerenkool.database.repository;

import boerenkool.business.model.*;
import boerenkool.database.dao.mysql.*;
import boerenkool.database.dao.mysql.PictureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    // TODO getListOfHousesWithPictures when PictureDAO is implemented

    private final Logger logger = LoggerFactory.getLogger(HouseRepository.class);
    private final HouseDAO houseDAO;
    private final PictureDAO pictureDAO;
    private final ExtraFeatureDAO extraFeatureDAO;
    private final UserDAO userDAO;
    private final HouseTypeDAO houseTypeDAO;
    private final ReservationDAO reservationDAO;

    @Autowired
    public HouseRepository(HouseDAO houseDAO, PictureDAO pictureDAO, ExtraFeatureDAO extraFeatureDAO,
                           UserDAO userDAO, HouseTypeDAO houseTypeDAO, ReservationDAO reservationDAO) {
        logger.info("New HouseRepository");
        this.houseDAO = houseDAO;
        this.pictureDAO = pictureDAO;
        this.extraFeatureDAO = extraFeatureDAO;
        this.userDAO = userDAO;
        this.houseTypeDAO = houseTypeDAO;
        this.reservationDAO = reservationDAO;
    }

    private List<House> setAllHousesEntityDependencies(List<House> allHouses) {
        for (House house : allHouses) {
            house.setHouseOwner(userDAO.getOneById(house.accessOtherEntityIds().getHouseOwnerId())
                    .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            house.setHouseType(houseTypeDAO.getOneById(house.accessOtherEntityIds().getHouseTypeId())
                    .orElseThrow(() -> new NoSuchElementException("houseType not found")));
            // TODO @Emine > T for getAllFeaturesByHouseId = ExtraFeature
            house.setPictures(pictureDAO.getAllByHouseId(house.getHouseId()));

        }
        return allHouses;
    }

    private Optional<House> setSingleHouseEntityDependencies(Optional<House> optionalHouse) {
        if (optionalHouse.isPresent()) {
            optionalHouse.get()
                    .setHouseType(houseTypeDAO.getOneById(optionalHouse.get().accessOtherEntityIds().getHouseTypeId())
                            .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            optionalHouse.get()
                    .setHouseOwner(userDAO.getOneById(optionalHouse.get().accessOtherEntityIds().getHouseOwnerId())
                            .orElseThrow(() -> new NoSuchElementException("houseOwner not found")));
            // TODO @Emine > T for getAllFeaturesByHouseId = ExtraFeature
            optionalHouse.get()
                    .setPictures(pictureDAO.getAllByHouseId(optionalHouse.get().getHouseId()));
        }
        return optionalHouse;
    }

    private List<ExtraFeature> collectHouseExtraFeatures(List<HouseExtraFeature> houseExtraFeatures) {
        List<ExtraFeature> extraFeatures = new ArrayList<>();
        for (HouseExtraFeature houseExtraFeature : houseExtraFeatures) {

        }
        return extraFeatures;
    }

    public List<House> getListOfAllHouses() {
        List<House> allHouses = houseDAO.getAll();
        setAllHousesEntityDependencies(allHouses);
        return allHouses;
    }

    public List<House> getListOfAllHousesWithFirstPicture() {
        List<House> allHouses = getListOfAllHouses();
        // TODO easy solution for first picture only? Maybe need extra PictureDAO method
        return allHouses;
    }

    public List<House> getListOfAllHousesByOwner(int ownerId) {
        List<House> allHouses = houseDAO.getAllHousesByOwner(ownerId);
        // currently gets all pictures
        setAllHousesEntityDependencies(allHouses);
        return allHouses;
    }

    public List<House> getLimitedListOfHouses(int limit, int offset) {
        List<House> allHouses = houseDAO.getLimitedList(limit, offset);
        // currently gets all pictures
        setAllHousesEntityDependencies(allHouses);
        return allHouses;
    }

    public List<House> getHousesWithFilter(HouseFilter filter) {
        List<House> allHouses = houseDAO.getHousesWithFilter(filter);
        // currently gets all pictures
        setAllHousesEntityDependencies(allHouses);
        return allHouses;
    }

    public List<String> getUniqueCities() {
        return houseDAO.getUniqueCities();
    }

    public List<HouseType> getAllHouseTypes() {
        return houseTypeDAO.getAll();
    }

    public Optional<House> getHouseById(int houseId) {
        Optional<House> optionalHouse = houseDAO.getOneById(houseId);
        setSingleHouseEntityDependencies(optionalHouse);
        return optionalHouse;
    }

    public boolean saveHouse(House house) {
        if (house.getHouseId() == 0) {
            return storeNewHouse(house);
        }
        else {
            return updateHouse(house);
        }
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

} // class
