package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
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
        logger.info("New HouseDAO");
    }

    @Override
    public List<House> getAll() {
        String sql = "SELECT * FROM House";
        List<House> allHouses = jdbcTemplate.query(sql, new HouseMapper(userDAO, houseTypeDAO));
        return allHouses;
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
        jdbcTemplate.update(connection -> updateHouseStatement(house, connection));
        return true;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "DELETE FROM House WHERE houseId = ?";
        jdbcTemplate.update(sql, id);
        return true;
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
