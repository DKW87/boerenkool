package boerenkool.database.repository;
import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import boerenkool.database.dao.mysql.HouseDAO;
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
    public void savePicture(Picture picture) {
        if (picture.getPictureId() == 0) {
            pictureDAO.storeOne(picture);
        }
        else {
            pictureDAO.updateOne(picture);
        }
    }

    public boolean deletePicture(int pictureId) {
        return pictureDAO.removeOneById(pictureId);
    }

    public void storeOne(Picture picture) {
        pictureDAO.storeOne(picture);
    }

    // geef ik hier het object mee of een id ?
    public void removeOneById(int pictureId) {
        pictureDAO.removeOneById(pictureId);
    }

    public List<Picture> getAllByHouseId(int houseId) {
        return pictureDAO.getAllByHouseId(houseId);
    }

    Optional getOneById(int pictureId) {
        return pictureDAO.getOneById(pictureId);
    }

    boolean updateOne(Picture picture) {
        return pictureDAO.updateOne(picture);
    }


}
