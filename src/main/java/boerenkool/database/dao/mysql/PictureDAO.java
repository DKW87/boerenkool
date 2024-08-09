package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.Picture;
import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

public interface PictureDAO extends GenericDAO<Picture> {

    List<Picture> getAll();

    List<Picture> getAllByHouseId();

    Optional<Picture> getOneById(int id);

    void storeOne(Picture picture);

    boolean updateOne(Picture picture);

    boolean removeOneById(int id);








}
