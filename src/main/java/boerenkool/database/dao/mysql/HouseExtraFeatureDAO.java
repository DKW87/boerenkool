package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;


public interface HouseExtraFeatureDAO extends GenericDAO<HouseExtraFeature> {


    List<HouseExtraFeature> getAll();


    Optional<HouseExtraFeature> getOneByIds(int houseId, int featureId);


    void storeOne(HouseExtraFeature houseExtraFeature);


    boolean removeOneByIds(int houseId, int featureId);


    boolean updateOne(HouseExtraFeature houseExtraFeature);


    List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId);
}
