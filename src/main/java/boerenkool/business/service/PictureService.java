package boerenkool.business.service;

import boerenkool.business.model.Picture;
import boerenkool.communication.dto.PictureDTO;
import boerenkool.database.repository.PictureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // save includes store and update
    public boolean savePicture(Picture picture) {
        return pictureRepository.savePicture(picture);
    }


    public boolean removeOneById(int pictureId) {
        return pictureRepository.removeOneById(pictureId);
    }

    public List<Picture> getAllByHouseId(int houseId) {
        return pictureRepository.getAllByHouseId(houseId);
    }

    public Optional getFirstPictureByHouseId(int houseId) {
        return pictureRepository.getFirstPictureByHouseId(houseId);
    }

    public Optional getOneById(int pictureId) {
        return pictureRepository.getOneById(pictureId);
    }


    public ResponseEntity<byte[]> buildImageResponse(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        String imageFormat = detectImageFormat(imageBytes);

        if ("png".equalsIgnoreCase(imageFormat)) {
            headers.setContentType(MediaType.IMAGE_PNG);
        } else if ("jpeg".equalsIgnoreCase(imageFormat) || "jpg".equalsIgnoreCase(imageFormat)) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }


    public String detectImageFormat(byte[] imageBytes) {
        if (imageBytes.length >= 4) {
            // Check PNG signature
            if ((imageBytes[0] & 0xFF) == 0x89 &&
                    (imageBytes[1] & 0xFF) == 0x50 &&
                    (imageBytes[2] & 0xFF) == 0x4E &&
                    (imageBytes[3] & 0xFF) == 0x47) {
                return "png";
            }

            // Check JPEG signature
            if ((imageBytes[0] & 0xFF) == 0xFF &&
                    (imageBytes[1] & 0xFF) == 0xD8 &&
                    (imageBytes[2] & 0xFF) == 0xFF) {
                return "jpeg";
            }
        }
        return null;
    }









}
