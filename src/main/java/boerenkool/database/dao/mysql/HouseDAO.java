package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 10:41
 */
public interface HouseDAO extends GenericDAO<House> {

    @Override
    List<House> getAll();

    List<House> getAllHousesByOwner(int ownerId);

    List<House> getLimitedList(int limit, int offset);

    List<House> getHousesWithFilter(HouseFilter filter);

    @Override
    Optional<House> getOneById(int id);

    @Override
    void storeOne(House house);

    @Override
    boolean updateOne(House house);

    @Override
    boolean removeOneById(int id);

}
