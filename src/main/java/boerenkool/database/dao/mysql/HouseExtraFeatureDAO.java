package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;


public interface HouseExtraFeatureDAO extends GenericDAO<HouseExtraFeature> {

    @Override
    List<HouseExtraFeature> getAll();


    Optional<HouseExtraFeature> getOneById(int houseId, int featureId);

    @Override
    boolean storeOne(HouseExtraFeature houseExtraFeature);


    boolean removeOneByIds(int houseId, int featureId);

    @Override
    boolean updateOne(HouseExtraFeature houseExtraFeature);


    List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId);
}
