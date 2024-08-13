package boerenkool.database.repository;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.mysql.HouseExtraFeatureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HouseExtraFeatureRepository {

    private final Logger logger = LoggerFactory.getLogger(HouseExtraFeatureRepository.class);

    private final HouseExtraFeatureDAO houseExtraFeatureDAO;

    public HouseExtraFeatureRepository(HouseExtraFeatureDAO houseExtraFeatureDAO) {
        this.houseExtraFeatureDAO = houseExtraFeatureDAO;
        logger.info("New HouseExtraFeatureRepository");
    }

    public void storeOne(HouseExtraFeature houseExtraFeature) {
        houseExtraFeatureDAO.storeOne(houseExtraFeature);
    }

    public void removeOneByIds(int houseId, int featureId) {
        houseExtraFeatureDAO.removeOneByIds(houseId, featureId);
    }

    public List<HouseExtraFeature> getAll() {
        return houseExtraFeatureDAO.getAll();
    }

    public Optional<HouseExtraFeature> getOneByIds(int houseId, int featureId) {
        return houseExtraFeatureDAO.getOneById(houseId, featureId);
    }

    public boolean updateOne(HouseExtraFeature houseExtraFeature) {
        return houseExtraFeatureDAO.updateOne(houseExtraFeature);
    }

    public List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId) {
        return houseExtraFeatureDAO.getAllFeaturesByHouseId(houseId);
    }
}
