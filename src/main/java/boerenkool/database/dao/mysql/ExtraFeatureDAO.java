package boerenkool.database.dao.mysql;

import boerenkool.business.model.ExtraFeature;
import boerenkool.database.dao.GenericDAO;


import java.util.List;
import java.util.Optional;


public interface ExtraFeatureDAO extends GenericDAO<ExtraFeature> {


    List<ExtraFeature> getAll();


    Optional<ExtraFeature> getOneById(int id);


    void storeOne(ExtraFeature extraFeature);


    boolean removeOneById(int id);


    boolean updateOne(ExtraFeature extraFeature);


    Optional<ExtraFeature> findByName(String extraFeatureName);
}
