package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseExtraFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcHouseExtraFeatureDAO implements HouseExtraFeatureDAO {

    private static final Logger logger = LoggerFactory.getLogger(JdbcHouseExtraFeatureDAO.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcHouseExtraFeatureDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("JdbcHouseExtraFeatureDAO instantiated");
    }

    @Override
    public List<HouseExtraFeature> getAll() {
        String sql = "SELECT * FROM HouseExtraFeature";
        return jdbcTemplate.query(sql, new HouseExtraFeatureRowMapper());
    }


    @Override
    public Optional<HouseExtraFeature> getOneById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<HouseExtraFeature> getOneById(int houseId, int featureId) {
        String sql = "SELECT * FROM HouseExtraFeature WHERE houseId = ? AND featureId = ?";
        return jdbcTemplate.query(sql, new HouseExtraFeatureRowMapper(), houseId, featureId)
                .stream()
                .findFirst();
    }

    @Override
    public boolean storeOne(HouseExtraFeature houseExtraFeature) {
        if (exists(houseExtraFeature)) {
            updateOne(houseExtraFeature);
        } else {
            insert(houseExtraFeature);
        }
        return false;
    }

    @Override
    public boolean updateOne(HouseExtraFeature houseExtraFeature) {

        return false; // No actual fields to update
    }

    @Override
    public boolean removeOneById(int id) {
        return false;
    }

    @Override
    public boolean removeOneByIds(int houseId, int featureId) {
        String sql = "DELETE FROM HouseExtraFeature WHERE houseId = ? AND featureId = ?";
        int rowsAffected = jdbcTemplate.update(sql, houseId, featureId);
        return rowsAffected > 0;
    }

    @Override
    public List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId) {
        String sql = "SELECT * FROM HouseExtraFeature WHERE houseId = ?";
        return jdbcTemplate.query(sql, new HouseExtraFeatureRowMapper(), houseId);
    }

    private void insert(HouseExtraFeature houseExtraFeature) {
        String sql = "INSERT INTO HouseExtraFeature (houseId, featureId) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                houseExtraFeature.getHouseId(),
                houseExtraFeature.getFeatureId());
    }

    private boolean exists(HouseExtraFeature houseExtraFeature) {
        return getOneById(houseExtraFeature.getHouseId(), houseExtraFeature.getFeatureId()).isPresent();
    }

    private static class HouseExtraFeatureRowMapper implements RowMapper<HouseExtraFeature> {
        @Override
        public HouseExtraFeature mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            int houseId = resultSet.getInt("houseId");
            int featureId = resultSet.getInt("featureId");
            return new HouseExtraFeature(houseId, featureId);
        }
    }
}
