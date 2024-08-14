package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseType;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

public interface HouseTypeDAO extends GenericDAO<HouseType> {

    @Override
    List<HouseType> getAll();

    @Override
    Optional<HouseType> getOneById(int id);

    @Override
    boolean storeOne(HouseType houseType);

    @Override
    boolean removeOneById(int id);

    @Override
    boolean updateOne(HouseType houseType);


    Optional<HouseType> findByName(String houseTypeName);
}
