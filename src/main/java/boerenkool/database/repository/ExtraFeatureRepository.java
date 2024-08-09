package boerenkool.database.repository;

import boerenkool.business.model.ExtraFeature;
import boerenkool.database.dao.mysql.ExtraFeatureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExtraFeatureRepository {

    private final Logger logger = LoggerFactory.getLogger(ExtraFeatureRepository.class);

    private final ExtraFeatureDAO extraFeatureDAO;

    public ExtraFeatureRepository(ExtraFeatureDAO extraFeatureDAO) {
        this.extraFeatureDAO = extraFeatureDAO;
        logger.info("New ExtraFeatureRepository");
    }

    public void storeOne(ExtraFeature extraFeature) {
        extraFeatureDAO.storeOne(extraFeature);
    }

    public void removeOneById(int id) {
        extraFeatureDAO.removeOneById(id);
    }

    public List<ExtraFeature> getAll() {
        return extraFeatureDAO.getAll();
    }

    public Optional<ExtraFeature> getOneById(int id) {
        return extraFeatureDAO.getOneById(id);
    }

    public boolean updateOne(ExtraFeature extraFeature) {
        return extraFeatureDAO.updateOne(extraFeature);
    }

    public Optional<ExtraFeature> findByName(String extraFeatureName) {
        return extraFeatureDAO.findByName(extraFeatureName);
    }
}
