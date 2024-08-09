package boerenkool.database.dao.mysql;

import boerenkool.business.model.Picture;
import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPictureDAO implements PictureDAO {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPictureDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcMemberDao.");
    }

    /**
    Logger class to track the flow throughout the application
     */
    private final Logger logger = LoggerFactory.getLogger(JdbcPictureDAO.class);

    /**
     * PictureRowMapper makes KeyMap from picture object for more efficient searching
     */
    private static class PictureRowMapper implements RowMapper<Picture> {

        @Override
        public Picture mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            int pictureId = resultSet.getInt("pictureId");
            House house = house.getOneById(resultSet.getInt("houseId")); //todo bestaat nog niet
            byte[] pictureData = resultSet.getBytes("picture");
            String pictureDescription = resultSet.getString("pictureDescription");
            Picture picture = new Picture(pictureId, house, pictureData, pictureDescription);
            picture.setPictureId(pictureId);
            return picture;
        } //todo house opvragen bij sql als int ? en dan getHouseById opslaan als house ? dan meegeven aan picture constructor ?
    }



    @Override
    public List<Picture> getAll() {
        List<Picture> allPictures = jdbcTemplate.query("SELECT * FROM Picture;", new JdbcPictureDAO.PictureRowMapper());
        return allPictures;
    }
    //todo is de new JdbcPictureDAO overbodig ?

    public List<Picture> getAllByHouseId(int houseId) {
        List<Picture> allPicturesFromHouseId = jdbcTemplate.query("SELECT * FROM Picture WHERE houseId = ?;", new JdbcPictureDAO.PictureRowMapper(), houseId);
        return allPicturesFromHouseId;
    }
    //todo is de new JdbcPictureDAO overbodig ?

    @Override
    public Optional<Picture> getOneById(int id) {
        String sql = "SELECT * FROM Picture WHERE pictureId = ?;";
        List<Picture> resultList =
                jdbcTemplate.query(sql, new PictureRowMapper(), id);
        if (resultList.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void storeOne(Picture picture) {

    }

    @Override
    public boolean updateOne(Picture picture) {
        return false;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql  = "DELETE FROM user WHERE userId = ?";
        return jdbcTemplate.update(sql, id) != 0;
    }

    private void setCommonParameters(PreparedStatement ps, Picture picture) throws SQLException {
        ps.setString(1, picture.getPictureId());
        ps.setString(2, );
        ps.setString(3, picture.getPicture());
        ps.setString(4, picture.getDescription());
    }

    private PreparedStatement InsertPictureStatement(Picture picture, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                "insert into picture_table (pictureId, houseId, picture, pictureDescription) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, picture.getPictureId());
        ps.setHouse(2, picture.getHouse);
        ps.setBytes(3, picture.getPicture());
        ps.setString(4, picture.getDescription());
        return ps;
    }



} // einde klasse
