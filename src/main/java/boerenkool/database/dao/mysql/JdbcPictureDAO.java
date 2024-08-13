package boerenkool.database.dao.mysql;
import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Timothy Houweling
 * @project Boerenkool
 */

@Repository
public class JdbcPictureDAO implements PictureDAO {
    /**
     Logger class to track the flow throughout the application
     */
    private final Logger logger = LoggerFactory.getLogger(JdbcPictureDAO.class);
    JdbcTemplate jdbcTemplate;


    @Autowired
    public JdbcPictureDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcPictureDao.");
    }

    @Override
    public List<Picture> getAll() {
        List<Picture> allPictures = jdbcTemplate.query("SELECT * FROM Picture;", new JdbcPictureDAO.PictureRowMapper());
        return allPictures;
    }

    @Override
    public List<Picture> getAllByHouseId(int houseId) {
        List<Picture> allPicturesFromHouseId = jdbcTemplate.query("SELECT * FROM Picture WHERE houseId = ?;", new JdbcPictureDAO.PictureRowMapper(), houseId);
        return allPicturesFromHouseId;
    }


    @Override
    public Optional<Picture> getOneById(int pictureId) {
        String sql = "SELECT * FROM Picture WHERE pictureId = ?;";
        List<Picture> resultList =
                jdbcTemplate.query(sql, new PictureRowMapper(), pictureId);
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void storeOne(Picture picture) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertPictureStatement(picture, connection),keyHolder);
        int pKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
        picture.setPictureId(pKey);
    }

    @Override
    public boolean updateOne(Picture picture) {
        return jdbcTemplate.update(connection -> updatePictureStatement(picture, connection)) != 0;
    }

    @Override
    public boolean removeOneById(int pictureId) {
        String sql  = "DELETE FROM Picture WHERE pictureId = ?";
        return jdbcTemplate.update(sql, pictureId) != 0;
    }


    private void setCommonParameters(PreparedStatement ps, Picture picture) throws SQLException {
        ps.setInt(1, picture.getHouse().getHouseId());
        ps.setBytes(2, picture.getPicture());
        ps.setString(3, picture.getDescription());
    }

    private PreparedStatement insertPictureStatement(Picture picture, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                "INSERT INTO Picture (houseId, picture, pictureDescription) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        setCommonParameters(ps, picture);
        return ps;
    }


    private PreparedStatement updatePictureStatement(Picture picture, Connection connection) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement(
                """
         UPDATE Picture 
         SET 
            houseId=?,
            picture=?,
            pictureDescription=?
         WHERE id=?
      """
        );
        setCommonParameters(ps, picture);
        ps.setInt(4, picture.getPictureId());
        return ps;
    }

    /**
     * PictureRowMapper makes KeyMap from picture object for more efficient searching
     * Creates new picture with a house value of null. This will be implemented later in the Repo class
     */
    private static class PictureRowMapper implements RowMapper<Picture> {
        @Override
        public Picture mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            int pictureId = resultSet.getInt("pictureId");
            byte[] pictureData = resultSet.getBytes("picture");
            String pictureDescription = resultSet.getString("pictureDescription");
            Picture picture = new Picture
                    (null, pictureData,pictureDescription);
            picture.setPictureId(pictureId);
            return picture;
        }
    }



} // einde klasse
