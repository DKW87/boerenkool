package boerenkool.database.repository;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.dao.mysql.HouseExtraFeatureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HouseExtraFeatureRepository {

    private final Logger logger = LoggerFactory.getLogger(HouseExtraFeatureRepository.class);

    private final HouseExtraFeatureDAO houseExtraFeatureDAO;

    private final JdbcTemplate jdbcTemplate;


    public HouseExtraFeatureRepository(HouseExtraFeatureDAO houseExtraFeatureDAO, JdbcTemplate jdbcTemplate) {
        this.houseExtraFeatureDAO = houseExtraFeatureDAO;
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New HouseExtraFeatureRepository");
    }

    public void storeOne(HouseExtraFeature houseExtraFeature) {
        String sql = "INSERT INTO HouseExtraFeature (houseId, featureId) VALUES (?, ?)";
        jdbcTemplate.update(sql, houseExtraFeature.getHouseId(), houseExtraFeature.getFeatureId());
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

    public List<HouseExtraFeature> getAllFeaturesByHouseIdWithNames(int houseId) {
        return houseExtraFeatureDAO.getAllFeaturesByHouseIdWithNames(houseId);
    }

    public void removeAllByHouseId(int houseId) {
        houseExtraFeatureDAO.removeAllByHouseId(houseId);
    }

    public void storeAll(List<HouseExtraFeature> houseExtraFeatures) {
        String sql = "INSERT INTO HouseExtraFeature (houseId, featureId) VALUES (?, ?)";

        houseExtraFeatures.forEach(feature -> {
            jdbcTemplate.update(sql, feature.getHouseId(), feature.getFeatureId());
        });
    }




}
