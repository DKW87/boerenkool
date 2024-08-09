package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseType;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

public interface HouseTypeDAO extends GenericDAO<HouseType> {


    List<HouseType> getAll();


    Optional<HouseType> getOneById(int id);


    void storeOne(HouseType houseType);


    boolean removeOneById(int id);


    boolean updateOne(HouseType houseType);


    Optional<HouseType> findByName(String houseTypeName);
}
