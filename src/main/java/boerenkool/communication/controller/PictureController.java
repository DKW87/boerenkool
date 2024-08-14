package boerenkool.communication.controller;

import boerenkool.business.model.Picture;
import boerenkool.database.dao.mysql.JdbcPictureDAO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pictures")
public class PictureController {

    private final JdbcPictureDAO jdbcPictureDAO;

    public PictureController(JdbcPictureDAO jdbcPictureDAO) {
        this.jdbcPictureDAO = jdbcPictureDAO;
    }

    @GetMapping
    public String showPicturesMessage() {
        return "Hello user, if you see this everything is OK.";
    }

    //todo Deze klasse werkt nog niet met een service klasse maar met de DAO.
    //todo geeft geen foutmelding bij picture id die niet bestaat.
    //todo ondersteund nog geen JPEG.

    //todo werkt nog niet.
    @GetMapping("/houses/{houseId}")
    public ResponseEntity<List<String>> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
        Optional<List<Picture>> optionalPictures = Optional.ofNullable(jdbcPictureDAO.getAllByHouseId(houseId));

        // Check if the list of pictures is present and not empty
        if (optionalPictures.isPresent() && !optionalPictures.get().isEmpty()) {
            List<String> encodedImages = optionalPictures.get().stream()
                    .map(picture -> {
                        byte[] imageBytes = picture.getPicture();
                        String imageFormat = detectImageFormat(imageBytes);

                        if (imageFormat != null &&
                                ("png".equalsIgnoreCase(imageFormat) ||
                                        "jpeg".equalsIgnoreCase(imageFormat) ||
                                        "jpg".equalsIgnoreCase(imageFormat))) {
                            return Base64.getEncoder().encodeToString(imageBytes);
                        }
                        return null;
                    })
                    .filter(encodedImage -> encodedImage != null)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(encodedImages, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //todo tijdelijke mapping om functie te testen.
    @GetMapping("/houses/first/{houseId}")
    public ResponseEntity<byte[]> getFirstPictureOfHouse(@PathVariable("houseId") int houseId) {
        Optional<Picture> picture = Optional.ofNullable(jdbcPictureDAO.getFirstPictureByHouseId(houseId));
        if (picture.isPresent()) {
            byte[] imageBytes = picture.get().getPicture(); // 1st get() has to stay there.
            HttpHeaders headers = new HttpHeaders();

            // Detect image format.
            String imageFormat = detectImageFormat(imageBytes);
            if ("png".equalsIgnoreCase(imageFormat)) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if ("jpeg".equalsIgnoreCase(imageFormat) || "jpg".equalsIgnoreCase(imageFormat)) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else {
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }






    @GetMapping("/{pictureId}")
    public ResponseEntity<byte[]> getPictureById(@PathVariable("pictureId") int pictureId) {
        Optional<Picture> picture = jdbcPictureDAO.getOneById(pictureId);
        if (picture.isPresent()) {
            byte[] imageBytes = picture.get().getPicture(); // 1st get() has to stay there.
            HttpHeaders headers = new HttpHeaders();

            // Detect image format.
            String imageFormat = detectImageFormat(imageBytes);
            if ("png".equalsIgnoreCase(imageFormat)) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if ("jpeg".equalsIgnoreCase(imageFormat) || "jpg".equalsIgnoreCase(imageFormat)) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else {
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //todo deze methode een eigen plekje geven weg van deze controller.
    // Utility method to detect the image format by inspecting the file signature
    private String detectImageFormat(byte[] imageBytes) {
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
        return null; //
    }

//    @GetMapping("/{pictureId}")
//    public ResponseEntity<byte[]> getPictureById(@PathVariable("pictureId") int pictureId) {
//        Optional<Picture> picture = jdbcPictureDAO.getOneById(pictureId);
//        if (picture.isPresent()) {
//            byte[] imageBytes = picture.get().getPicture(); //getPicture = data of picture
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.IMAGE_PNG);
//            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


////    todo methode per house Id alle pictures op te halen
//    @GetMapping("/houses/{houseId}")
//    public List<Picture> getAllByHouseId(@PathVariable("houseId") int houseId) {
//        Optional<Picture> pictures = jdbcPictureDAO.getAllByHouseId(houseId);
//        return pictures.map(ResponseEntity::ok)
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//
//    }






}
