package boerenkool.communication.controller;

import boerenkool.business.model.Picture;
import boerenkool.business.model.User;
import boerenkool.business.service.PictureService;
import boerenkool.communication.dto.PictureDTO;
import boerenkool.database.dao.mysql.JdbcPictureDAO;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PictureService pictureService;

    public PictureController(JdbcPictureDAO jdbcPictureDAO, PictureService pictureService) {
        this.jdbcPictureDAO = jdbcPictureDAO;
        this.pictureService = pictureService;
    }

    @GetMapping
    public String showPicturesMessage() {
        return "Hello user, if you see this everything is OK.";
    }

    //todo onderstaande methode nog formatten
    @GetMapping("/houses/{houseId}")
    public ResponseEntity<String> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
        Optional<List<Picture>> optionalPictures = Optional.ofNullable(jdbcPictureDAO.getAllByHouseId(houseId));

        if (optionalPictures.isPresent() && !optionalPictures.get().isEmpty()) {
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<html><body>");

            for (Picture picture : optionalPictures.get()) {
                byte[] imageBytes = picture.getPicture();
                String imageFormat = pictureService.detectImageFormat(imageBytes);

                if (imageFormat != null &&
                        ("png".equalsIgnoreCase(imageFormat) ||
                                "jpeg".equalsIgnoreCase(imageFormat) ||
                                "jpg".equalsIgnoreCase(imageFormat))) {
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    htmlResponse.append("<img src='data:image/")
                            .append(imageFormat)
                            .append(";base64,")
                            .append(base64Image)
                            .append("' alt='House Image'/><br>");
                }
            }
            htmlResponse.append("</body></html>");
            return new ResponseEntity<>(htmlResponse.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No images found for this house ID", HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/houses/{houseId}")
//    public ResponseEntity<List<PictureDTO>> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
//        Optional<List<Picture>> optionalPictures = Optional.ofNullable(jdbcPictureDAO.getAllByHouseId(houseId));
//
//        if (optionalPictures.isPresent() && !optionalPictures.get().isEmpty()) {
//            List<PictureDTO> pictureResponses = optionalPictures.get().stream()
//                    .map(picture -> {
//                        byte[] imageBytes = picture.getPicture();
//                        String imageFormat = pictureService.detectImageFormat(imageBytes);
//
//                        if (imageFormat != null &&
//                                ("png".equalsIgnoreCase(imageFormat) ||
//                                        "jpeg".equalsIgnoreCase(imageFormat) ||
//                                        "jpg".equalsIgnoreCase(imageFormat))) {
//                            return new PictureDTO(picture.getPictureId(), picture.getHouseId(), imageFormat, Base64.getEncoder().encodeToString(imageBytes));
//                        }
//                        return null;
//                    })
//                    .filter(pictureResponse -> pictureResponse != null)
//                    .collect(Collectors.toList());
//            return new ResponseEntity<>(pictureResponses, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    //todo werkt nu
    @GetMapping("/houses/first/{houseId}")
    public ResponseEntity<byte[]> getFirstPictureOfHouse(@PathVariable("houseId") int houseId) {
        Optional<Picture> picture = jdbcPictureDAO.getFirstPictureByHouseId(houseId);
        if (picture.isPresent()) {
            return pictureService.buildImageResponse(picture.get().getPicture());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //todo werkt nu
    @GetMapping("/{pictureId}")
    public ResponseEntity<byte[]> getPictureById(@PathVariable("pictureId") int pictureId) {
        Optional<Picture> picture = jdbcPictureDAO.getOneById(pictureId);
        if (picture.isPresent()) {
            return pictureService.buildImageResponse(picture.get().getPicture());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOne(@RequestBody Picture picture) {
        pictureService.storeOne(picture);
        return new ResponseEntity<>("Picture created successfully", HttpStatus.CREATED);
    }


}
