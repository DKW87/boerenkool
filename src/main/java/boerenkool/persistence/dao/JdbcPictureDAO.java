package boerenkool.persistence.dao;

import boerenkool.business.model.Picture;

import java.util.List;

public class JdbcPictureDAO implements GenericDAO {

    @Override
    public void save(Picture picture) {

    }

    @Override
    public Picture findPictureById(int pictureId) {
        return null;
    }

    @Override
    public List<Picture> findAllPictures() {
        return List.of();
    }

    @Override
    public void update(Picture picture) {

    }

    @Override
    public List<Picture> findPictureByHouse(House house) {
        return List.of();
    }


} // einde klasse
