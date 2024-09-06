package boerenkool.database.dao.mysql;

import boerenkool.business.model.ExtraFeature;
import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.GenericDAO;


import java.util.List;
import java.util.Optional;


public interface ExtraFeatureDAO extends GenericDAO<ExtraFeature> {


    @Override
    List<ExtraFeature> getAll();

    @Override
    Optional<ExtraFeature> getOneById(int id);

    @Override
    boolean storeOne(ExtraFeature extraFeature);

    @Override
    boolean removeOneById(int id);

    @Override
    boolean updateOne(ExtraFeature extraFeature);


    Optional<ExtraFeature> findByName(String extraFeatureName);

    List<HouseExtraFeature> getAllFeaturesByHouseIdWithNames(int houseId);
}
