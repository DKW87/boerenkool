package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
@Repository
public class JdbcHouseDAO implements HouseDAO {

    private final Logger logger = LoggerFactory.getLogger(HouseDAO.class);
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public JdbcHouseDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcHouseDAO");
    }

    @Override
    public List<House> getAll() {
        String sql = "SELECT * FROM House";
        return jdbcTemplate.query(sql, new HouseMapper());
    }

    @Override
    public List<House> getAllHousesByOwner(int ownerId) {
        String sql = "SELECT * FROM House WHERE houseOwnerId = ?";
        return jdbcTemplate.query(sql, new HouseMapper(), ownerId);
    }

    @Override
    public List<House> getLimitedList(int limit, int offset) {
        String sql = "SELECT * FROM House LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new HouseMapper(), limit, offset);
    }

    @Override
    public List<House> getHousesWithFilter(HouseFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT house.* FROM House AS house WHERE 1=1 AND house.isNotAvailable = 0");
        List<Object> params = new ArrayList<>();

        addDateFilter(sql, params, filter);
        addProvinceFilter(sql, params, filter);
        addCityFilter(sql, params, filter);
        addHouseTypeFilter(sql, params, filter);
        addHouseOwnerFilter(sql, params, filter);
        addGuestFilter(sql, params, filter);
        addRoomCountFilter(sql, params, filter);
        addPriceFilter(sql, params, filter);
        addOrderByClause(sql, filter);
        addLimitOffset(sql, params, filter);

        return jdbcTemplate.query(sql.toString(), new HouseMapper(), params.toArray());
    }

    @Override
    public int countHousesWithFilter(HouseFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM House AS house WHERE 1=1" +
                " AND house.isNotAvailable = 0");
        List<Object> params = new ArrayList<>();

        addDateFilter(sql, params, filter);
        addProvinceFilter(sql, params, filter);
        addCityFilter(sql, params, filter);
        addHouseTypeFilter(sql, params, filter);
        addHouseOwnerFilter(sql, params, filter);
        addGuestFilter(sql, params, filter);
        addRoomCountFilter(sql, params, filter);
        addPriceFilter(sql, params, filter);

        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    @Override
    public Optional<House> getOneById(int id) {
        String sql = "SELECT * FROM House WHERE houseId = ?";
        try {
            House house = jdbcTemplate.queryForObject(sql, new HouseMapper(), id);
            System.out.println("HouseDAO heeft eenmalig gegevens uit DB gehaald");
            return Optional.ofNullable(house);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> getUniqueCities() {
        String sql = "SELECT DISTINCT city FROM House ORDER BY city ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("city"));
    }

    @Override
    public boolean storeOne(House house) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int recordInserted = jdbcTemplate.update(connection -> insertHouseStatement(house, connection), keyHolder);
        int pKey = keyHolder.getKey().intValue();
        house.setHouseId(pKey);
        addExtraFeaturesToHouse(house);
        return recordInserted == 1;
    }

    @Override
    public boolean updateOne(House house) {
        int recordsUpdated = jdbcTemplate.update(connection -> updateHouseStatement(house, connection));
        return recordsUpdated == 1;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM House WHERE houseId = ?";
        int recordsUpdated = jdbcTemplate.update(sql, id);
        return recordsUpdated == 1;
    }

    @Override
    public void addExtraFeaturesToHouse(House house) {
        String sql = "insert into HouseExtraFeature (houseId,featureId) values (?,?)";
        var extraFeatures = house.getExtraFeatures();
        extraFeatures.forEach(feature -> {
            jdbcTemplate.update(sql,house.getHouseId(),feature.getExtraFeatureId());
        });
    }

    private void addDateFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            sql.append(" AND NOT EXISTS (SELECT 1 FROM Reservation AS reservation " +
                    "WHERE reservation.houseId = house.houseId AND reservation.startDate < ? AND reservation.endDate > ?)");
            params.add(filter.getEndDate());
            params.add(filter.getStartDate());
        }
    }

    private void addProvinceFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getProvinces() != null && !filter.getProvinces().isEmpty()) {
            List<String> provinces = filter.getProvinces();

            if (provinces.size() == 1) {
                sql.append(" AND house.province = ?");
                params.add(provinces.get(0));
            } else {
                sql.append(" AND house.province IN (")
                        .append(String.join(", ", Collections.nCopies(provinces.size(), "?")))
                        .append(")");
                params.addAll(provinces);
            }
        }
    }

    private void addCityFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getCities() != null && !filter.getCities().isEmpty()) {
            List<String> cities = filter.getCities();

            if (cities.size() == 1) {
                sql.append(" AND house.city = ?");
                params.add(cities.get(0));
            } else {
                sql.append(" AND house.city IN (")
                        .append(String.join(", ", Collections.nCopies(cities.size(), "?")))
                        .append(")");
                params.addAll(cities);
            }
        }
    }

    private void addHouseTypeFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getHouseTypeIds() != null && !filter.getHouseTypeIds().isEmpty()) {
            List<Integer> houseTypes = filter.getHouseTypeIds();

            if (houseTypes.size() == 1) {
                sql.append(" AND house.houseTypeId = ?");
                params.add(houseTypes.get(0));
            } else {
                sql.append(" AND house.houseTypeId IN (")
                        .append(String.join(", ", Collections.nCopies(houseTypes.size(), "?")))
                        .append(")");
                for (Integer type : houseTypes) {
                    params.add(type);
                }
            }
        }
    }

    private void addHouseOwnerFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getHouseOwnerId() > 0) {
            sql.append(" AND house.houseOwnerId = ?");
            params.add(filter.getHouseOwnerId());
        }
    }

    private void addGuestFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getAmountOfGuests() > 0) {
            sql.append(" AND house.maxGuest >= ?");
            params.add(filter.getAmountOfGuests());
        }
    }

    private void addRoomCountFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getDesiredRoomCount() > 0) {
            sql.append(" AND house.roomCount >= ?");
            params.add(filter.getDesiredRoomCount());
        }
    }

    private void addPriceFilter(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getMinPricePPPD() > 0 && filter.getMaxPricePPPD() > 0) {
            if (filter.getMaxPricePPPD() == filter.getMinPricePPPD()) {
                sql.append(" AND house.pricePPPD = ?");
                params.add(filter.getMaxPricePPPD());
            } else {
                sql.append(" AND house.pricePPPD BETWEEN ? AND ?");
                params.add(filter.getMinPricePPPD());
                params.add(filter.getMaxPricePPPD());
            }
        } else if (filter.getMinPricePPPD() > 0) {
            sql.append(" AND house.pricePPPD >= ?");
            params.add(filter.getMinPricePPPD());
        } else if (filter.getMaxPricePPPD() > 0) {
            sql.append(" AND house.pricePPPD <= ?");
            params.add(filter.getMaxPricePPPD());
        }
    }

    private void addOrderByClause(StringBuilder sql, HouseFilter filter) {
        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
            String sortBy = filter.getSortBy();
            String sortOrder = filter.getSortOrder() != null ? filter.getSortOrder() : "ASC"; // Default to ASC

            // Zorg ervoor dat de sortOrder geldig is
            if (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder)) {
                sortOrder = "ASC"; // Default to ASC if invalid value
            }

            sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
        }
    }

    private void addLimitOffset(StringBuilder sql, List<Object> params, HouseFilter filter) {
        if (filter.getLimit() > 0) {
            sql.append(" LIMIT ?");
            params.add(filter.getLimit());
        }
        if (filter.getOffset() > 0) {
            sql.append(" OFFSET ?");
            params.add(filter.getOffset());
        }
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
        preparedStatement.setBoolean(12, house.getIsNotAvailable());

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
        preparedStatement.setBoolean(12, house.getIsNotAvailable());
        preparedStatement.setInt(13, house.getHouseId());

        return preparedStatement;
    }

    private static class HouseMapper implements RowMapper<House> {

        @Override
        public House mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            int houseId = resultSet.getInt("houseId");
            int houseTypeId = resultSet.getInt("houseTypeId");
            int houseOwnerId = resultSet.getInt("houseOwnerId");
            String houseName = resultSet.getString("houseName");
            String province = resultSet.getString("province");
            String city = resultSet.getString("city");
            String streetAndNumber = resultSet.getString("streetAndNumber");
            String zipcode = resultSet.getString("zipcode");
            int maxGuest = resultSet.getInt("maxGuest");
            int roomCount = resultSet.getInt("roomCount");
            int pricePPPD = resultSet.getInt("pricePPPD");
            String description = resultSet.getString("description");
            boolean isNotAvailable = resultSet.getBoolean("isNotAvailable");

            House house = new House(houseName, province, city, streetAndNumber, zipcode,
                    maxGuest, roomCount, pricePPPD, description, isNotAvailable);
            house.setHouseId(houseId);
            house.accessOtherEntityIds().setHouseTypeId(houseTypeId);
            house.accessOtherEntityIds().setHouseOwnerId(houseOwnerId);

            return house;
        }

    } // HouseMapper class

} // JdbcHouseDAO class
