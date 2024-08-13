package boerenkool.database.repository;
import boerenkool.business.model.Picture;
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

    PictureDAO pictureDAO;

    private final Logger logger = LoggerFactory.getLogger(PictureRepository.class);

    @Autowired
    public PictureRepository(PictureDAO pictureDAO) {
        this.pictureDAO = pictureDAO;
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

    //todo aparte methode x3 gebruikt voor alle gets.

    public boolean deletePicture(int pictureId) {
        return pictureDAO.removeOneById(pictureId);
    }

    public void storeOne(Picture picture) {
        pictureDAO.storeOne(picture);
    }


    public void removeOneById(int pictureId) {
        pictureDAO.removeOneById(pictureId);
    }

    List<Picture> getAll() {
        return pictureDAO.getAll();
    }

    List<Picture> getAllByHouseId() {
        return pictureDAO.getAll();
    }

    Optional getOneById(int pictureId) {
        return pictureDAO.getOneById(pictureId);
    }

    boolean updateOne(Picture picture) {
        return pictureDAO.updateOne(picture);
    }


}
