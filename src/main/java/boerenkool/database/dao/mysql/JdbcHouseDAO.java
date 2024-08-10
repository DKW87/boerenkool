package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 07/08/2024 - 15:06
 */
public class JdbcHouseDAO implements HouseDAO {

    private final Logger logger = LoggerFactory.getLogger(HouseDAO.class);

    private JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final HouseTypeDAO houseTypeDAO;

    @Autowired
    public JdbcHouseDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO, HouseTypeDAO houseTypeDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
        this.houseTypeDAO = houseTypeDAO;
        logger.info("New JdbcHouseDAO");
    }

    @Override
    public List<House> getAll() {
        String sql = "SELECT * FROM House";
        List<House> allHouses = jdbcTemplate.query(sql, new HouseMapper(userDAO, houseTypeDAO));
        return allHouses;
    }

    @Override
    public List<House> getAllHousesByOwner(int ownerId) {
        String sql = "SELECT * FROM House WHERE houseOwnerId = ?";
        return jdbcTemplate.query(sql, new HouseMapper(userDAO, houseTypeDAO), ownerId);
    }

    @Override
    public List<House> getLimitedList(int limit, int offset) {
        String sql = "SELECT * FROM House LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new HouseMapper(userDAO, houseTypeDAO), limit, offset);
    }

    // TODO refactor with helper methods to make method smaller
    @Override
    public List<House> getHousesWithFilter(HouseFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM House WHERE 1=1");
        List<Object> params = new ArrayList<>();


        if (filter.getProvinces() != null && !filter.getProvinces().isEmpty()) {
            sql.append(" AND province IN (")
                    .append(String.join(", ", Collections.nCopies(filter.getProvinces().size(), "?")))
                    .append(")");
            params.addAll(filter.getProvinces());
        }

        if (filter.getCities() != null && !filter.getCities().isEmpty()) {
            sql.append(" AND city IN (")
                    .append(String.join(", ", Collections.nCopies(filter.getCities().size(), "?")))
                    .append(")");
            params.addAll(filter.getCities());
        }

        if (filter.getHouseTypes() != null && !filter.getHouseTypes().isEmpty()) {
            sql.append(" AND houseTypeId IN (")
                    .append(String.join(", ", Collections.nCopies(filter.getHouseTypes().size(), "?")))
                    .append(")");
            for (HouseType type : filter.getHouseTypes()) {
                params.add(type.getHouseTypeId());
            }
        }

        if (filter.getHouseOwner() != null) {
            sql.append(" AND houseOwnerId = ?");
            params.add(filter.getHouseOwner().getUserId());
        }

        if (filter.getAmountOfGuests() > 0) {
            sql.append(" AND maxGuest >= ?");
            params.add(filter.getAmountOfGuests());
        }

        if (filter.getDesiredRoomCount() > 0) {
            sql.append(" AND roomCount >= ?");
            params.add(filter.getDesiredRoomCount());
        }

        if (filter.getMinPricePPPD() > 0) {
            sql.append(" AND pricePPPD >= ?");
            params.add(filter.getMinPricePPPD());
        }

        if (filter.getMaxPricePPPD() > 0) {
            sql.append(" AND pricePPPD <= ?");
            params.add(filter.getMaxPricePPPD());
        }

        if (filter.getLimit() > 0) {
            sql.append(" LIMIT ?");
            params.add(filter.getLimit());
        }

        if (filter.getOffset() > 0) {
            sql.append(" OFFSET ?");
            params.add(filter.getOffset());
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new HouseMapper(userDAO, houseTypeDAO));
    }

    @Override
    public Optional<House> getOneById(int id) {
        String sql = "SELECT * FROM House WHERE houseId = ?";
        House house = jdbcTemplate.queryForObject(sql, new HouseMapper(userDAO, houseTypeDAO), id);
        return house == null ? Optional.empty() : Optional.of(house);
    }

    @Override
    public void storeOne(House house) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertHouseStatement(house, connection), keyHolder);
        int pKey = keyHolder.getKey().intValue();
        house.setHouseId(pKey);
    }

    @Override
    public boolean updateOne(House house) {
        int rowsUpdated = jdbcTemplate.update(connection -> updateHouseStatement(house, connection));
        return rowsUpdated == 1;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM House WHERE houseId = ?";
        jdbcTemplate.update(sql, id);
        return getOneById(id).isEmpty();
    }

    private PreparedStatement insertHouseStatement(House house, Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "INSERT INTO House (houseName, houseTypeId, houseOwnerId, province, city, streetAndNumber, zipcode, "
                + "maxGuest, roomCount, pricePPPD, description, isNotAvailable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, house.getHouseName());
        preparedStatement.setInt(2, house.getHouseType().getHouseTypeId());
        preparedStatement.setInt(3, house.getHouseOwner().getUserId());
        preparedStatement.setString(4, house.getProvince());
        preparedStatement.setString(5, house.getCity());
        preparedStatement.setString(6, house.getStreetAndNumber());
        preparedStatement.setString(7, house.getZipcode());
        preparedStatement.setInt(8, house.getMaxGuest());
        preparedStatement.setInt(9, house.getRoomCount());
        preparedStatement.setInt(10, house.getPricePPPD());
        preparedStatement.setString(11, house.getDescription());
        preparedStatement.setBoolean(12, house.isNotAvailable());

        return preparedStatement;
    }

    private PreparedStatement updateHouseStatement(House house, Connection connection) throws SQLException {
        String sql = "UPDATE House SET houseName=?, houseTypeId=?, houseOwnerId=?, province=?, city=?, streetAndNumber=?, " +
                "zipcode=?, maxGuest=?, roomCount=?, pricePPPD=?, description=?, isNotAvailable=? WHERE houseId = ?";
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, house.getHouseName());
        preparedStatement.setInt(2, house.getHouseType().getHouseTypeId());
        preparedStatement.setInt(3, house.getHouseOwner().getUserId());
        preparedStatement.setString(4, house.getProvince());
        preparedStatement.setString(5, house.getCity());
        preparedStatement.setString(6, house.getStreetAndNumber());
        preparedStatement.setString(7, house.getZipcode());
        preparedStatement.setInt(8, house.getMaxGuest());
        preparedStatement.setInt(9, house.getRoomCount());
        preparedStatement.setInt(10, house.getPricePPPD());
        preparedStatement.setString(11, house.getDescription());
        preparedStatement.setBoolean(12, house.isNotAvailable());
        preparedStatement.setInt(13, house.getHouseId());

        return preparedStatement;
    }

    private static class HouseMapper implements RowMapper<House> {

        private final UserDAO userDAO;
        private final HouseTypeDAO houseTypeDAO;

        public HouseMapper(UserDAO userDAO, HouseTypeDAO houseTypeDAO) {
            this.userDAO = userDAO;
            this.houseTypeDAO = houseTypeDAO;
        }

        @Override
        public House mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            int houseId = resultSet.getInt("houseId");
            String houseName = resultSet.getString("houseName");
            int houseTypeId = resultSet.getInt("houseTypeId");
            int houseOwnerId = resultSet.getInt("houseOwnerId");
            String province = resultSet.getString("province");
            String city = resultSet.getString("city");
            String streetAndNumber = resultSet.getString("streetAndNumber");
            int zipcode = resultSet.getInt("zipcode");
            int maxGuest = resultSet.getInt("maxGuest");
            int roomCount = resultSet.getInt("roomCount");
            int pricePPPD = resultSet.getInt("pricePPPD");
            String description = resultSet.getString("description");
            boolean isNotAvailable = resultSet.getBoolean("isNotAvailable");
            return new House(houseId, houseName, houseTypeDAO.getOneById(houseTypeId), userDAO.getOneById(houseOwnerId),
                    province, city, streetAndNumber, zipcode, maxGuest, roomCount, pricePPPD, description, isNotAvailable);
        }
    }

} // class
