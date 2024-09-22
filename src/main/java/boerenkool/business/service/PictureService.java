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

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.*;

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

    public Optional<Picture> getFirstPictureByHouseId(int houseId) {
        return pictureRepository.getFirstPictureByHouseId(houseId);
    }

    public Optional<Picture> getOneById(int pictureId) {
        return pictureRepository.getOneById(pictureId);
    }


    public ResponseEntity<byte[]> buildImageResponse(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        String imageFormat = detectImageFormat(imageBytes);

        switch (imageFormat) {
            case IMAGE_PNG_VALUE:
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            case IMAGE_JPEG_VALUE:
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case IMAGE_GIF_VALUE:
                headers.setContentType(MediaType.IMAGE_GIF);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    public String detectImageFormat(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return "unknown"; //
        }

        // Check PNG signature
        if ((imageBytes[0] & 0xFF) == 0x89 &&
                (imageBytes[1] & 0xFF) == 0x50 &&
                (imageBytes[2] & 0xFF) == 0x4E &&
                (imageBytes[3] & 0xFF) == 0x47) {
            return IMAGE_PNG_VALUE;
        }

        // Check JPEG signature
        if ((imageBytes[0] & 0xFF) == 0xFF &&
                (imageBytes[1] & 0xFF) == 0xD8 &&
                (imageBytes[2] & 0xFF) == 0xFF) {
            return IMAGE_JPEG_VALUE;
        }

        // Check GIF signature
        if ((imageBytes[0] & 0xFF) == 0x47 &&
                (imageBytes[1] & 0xFF) == 0x49 &&
                (imageBytes[2] & 0xFF) == 0x46 &&
                (imageBytes[3] & 0xFF) == 0x38) {
            return IMAGE_GIF_VALUE;
        }
        return "unknown";
    }

    public PictureDTO convertToDTO(Picture picture) {
        byte[] imageBytes = picture.getPicture();
        String base64Picture = Base64.getEncoder().encodeToString(imageBytes);
        String mimeType = detectImageFormat(imageBytes);


        return new PictureDTO(
                picture.getPictureId(),
                picture.getHouseId(),
                base64Picture,
                mimeType,
                picture.getDescription());
    }

}
