package boerenkool.database.repository;
import boerenkool.business.model.Picture;
import boerenkool.database.dao.mysql.HouseDAO;
import boerenkool.database.dao.mysql.JdbcPictureDAO;
import boerenkool.database.dao.mysql.PictureDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * @author Timothy Houweling
 * @project Boerenkool
 */
@Repository
public class PictureRepository {

    private final PictureDAO pictureDAO;
    private final HouseDAO houseDAO;

    private final Logger logger = LoggerFactory.getLogger(PictureRepository.class);

    @Autowired
    public PictureRepository(PictureDAO pictureDAO, HouseDAO houseDAO) {
        this.pictureDAO = pictureDAO;
        this.houseDAO = houseDAO;
        logger.info("New PictureRepository");
    }

    /**
     * Logic to either save or store a picture.
     * @param picture
     */

    public boolean savePicture(Picture picture) {
        if (picture.getPictureId() == 0) {
            return pictureDAO.storeOne(picture);
        } else {
            return pictureDAO.updateOne(picture);
        }
    }

    public boolean storeOne(Picture picture) {
        return pictureDAO.storeOne(picture);
    }

    public boolean updateOne(Picture picture) {
        return pictureDAO.updateOne(picture);
    }

    public boolean removeOneById(int pictureId) {
        return pictureDAO.removeOneById(pictureId);
    }

    public Optional getOneById(int pictureId) {
        return pictureDAO.getOneById(pictureId);
    }

    public List<Picture> getAllByHouseId(int houseId) {
        return pictureDAO.getAllByHouseId(houseId);
    }

    public Picture getFirstPictureByHouseId(int houseId) {
        return pictureDAO.getFirstPictureByHouseId(houseId);
    }






}
