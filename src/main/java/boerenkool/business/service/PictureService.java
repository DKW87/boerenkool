package boerenkool.business.service;

import boerenkool.business.model.Picture;
import boerenkool.database.repository.PictureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PictureService {

    private final Logger logger = LoggerFactory.getLogger(PictureService.class);
    private final PictureRepository pictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
        logger.info("New PictureService");
    }

    public boolean savePicture(Picture picture) {
        return pictureRepository.savePicture(picture);
    }

    public boolean storeOne(Picture picture) {
        return pictureRepository.storeOne(picture);
    }

    boolean updateOne(Picture picture) {
        return pictureRepository.updateOne(picture);
    }

    public boolean removeOneById(int pictureId) {
        return pictureRepository.removeOneById(pictureId);
    }

    public List<Picture> getAllByHouseId(int houseId) {
        return pictureRepository.getAllByHouseId(houseId);
    }

    public Picture getFirstPictureByHouseId(int houseId) {
        return pictureRepository.getFirstPictureByHouseId(houseId);
    }

    Optional getOneById(int pictureId) {
        return pictureRepository.getOneById(pictureId);
    }







}
