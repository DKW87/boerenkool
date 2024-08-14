package boerenkool.database.dao.mysql;
import boerenkool.business.model.Picture;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;
/**
 * @author Timothy Houweling
 * @project Boerenkool
 */
public interface PictureDAO extends GenericDAO<Picture> {
    List<Picture> getAll();

    List<Picture> getAllByHouseId(int houseId);

    Optional<Picture> getFirstPictureByHouseId(int houseId);

    Optional<Picture> getOneById(int pictureId);

    boolean storeOne(Picture picture);

    boolean updateOne(Picture picture);

    boolean removeOneById(int pictureId);








}
