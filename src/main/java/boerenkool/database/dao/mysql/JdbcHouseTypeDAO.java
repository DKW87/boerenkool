package boerenkool.database.dao.mysql;

import boerenkool.business.model.HouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcHouseTypeDAO implements HouseTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(JdbcHouseTypeDAO.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcHouseTypeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("JdbcHouseTypeDAO instantiated");
    }

    @Override
    public List<HouseType> getAll() {
        String sql = "SELECT * FROM HouseType";
        return jdbcTemplate.query(sql, new HouseTypeRowMapper());
    }

    @Override
    public Optional<HouseType> getOneById(int id) {
        String sql = "SELECT * FROM HouseType WHERE houseTypeId = ?";
        return jdbcTemplate.query(sql, new HouseTypeRowMapper(), id)
                .stream()
                .findFirst();
    }

    @Override
    public boolean storeOne(HouseType houseType) {
        if (houseType.getHouseTypeId() == 0) {
            insert(houseType);
        } else {
            updateOne(houseType);
        }
        return false;
    }

    @Override
    public boolean updateOne(HouseType houseType) {
        String sql = "UPDATE HouseType SET houseTypeName = ? WHERE houseTypeId = ?";
        int rowsAffected = jdbcTemplate.update(sql, houseType.getHouseTypeName(), houseType.getHouseTypeId());
        return rowsAffected > 0;
    }

    @Override
    public Optional<HouseType> findByName(String houseTypeName) {
        return Optional.empty();
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM HouseType WHERE houseTypeId = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    private void insert(HouseType houseType) {
        String sql = "INSERT INTO HouseType (houseTypeName) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"houseTypeId"});
            ps.setString(1, houseType.getHouseTypeName());
            return ps;
        }, keyHolder);
        houseType.setHouseTypeId(keyHolder.getKey().intValue());
    }

    private static class HouseTypeRowMapper implements RowMapper<HouseType> {
        @Override
        public HouseType mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            int houseTypeId = resultSet.getInt("houseTypeId");
            String houseTypeName = resultSet.getString("houseTypeName");
            return new HouseType(houseTypeId, houseTypeName);
        }
    }
}
