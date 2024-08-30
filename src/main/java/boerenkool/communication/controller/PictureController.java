package boerenkool.communication.controller;

import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import boerenkool.business.model.User;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.PictureService;
import boerenkool.communication.dto.PictureDTO;
import boerenkool.database.dao.mysql.JdbcPictureDAO;
import boerenkool.utilities.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pictures")
public class PictureController {

    private final JdbcPictureDAO jdbcPictureDAO;
    private final PictureService pictureService;
    private final HouseService houseService;

    public PictureController(JdbcPictureDAO jdbcPictureDAO, PictureService pictureService, HouseService houseService) {
        this.jdbcPictureDAO = jdbcPictureDAO;
        this.pictureService = pictureService;
        this.houseService = houseService;
    }

    @GetMapping
    public String showPicturesMessage() {
        return "Hello user, if you see this everything is OK.";
    }

    //todo onderstaande methode nog formatten
    //todo array of strings
    @GetMapping("/houses/{houseId}")
    public ResponseEntity<?> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
        Optional<List<Picture>> optionalPictures = Optional.ofNullable(pictureService.getAllByHouseId(houseId));

        //todo deze methode moet 2 strings ( de mime + de byte[] samenvoegen als een DTO en meegeven als een object )
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


    //todo werkt nu
    @GetMapping("/houses/first/{houseId}")
    public ResponseEntity<?> getFirstPictureOfHouse(@PathVariable("houseId") int houseId) {
        Optional<Picture> picture = pictureService.getFirstPictureByHouseId(houseId);
        if (picture.isPresent()) {
            return pictureService.buildImageResponse(picture.get().getPicture());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //todo werkt nu
    @GetMapping("/{pictureId}")
    public ResponseEntity<PictureDTO> getPictureById(@PathVariable("pictureId") int pictureId) {
        Optional<Picture> pictureOptional = pictureService.getOneById(pictureId);
        if (pictureOptional.isPresent()) {
            PictureDTO pictureDTO = pictureService.convertToDTO(pictureOptional.get());
            return ResponseEntity.ok(pictureDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/update/{pictureId}")
    public ResponseEntity<?> updatePictureDescription(@PathVariable("pictureId") int pictureId,
                                                      @RequestParam("description") String description) {
        try {
            // Fetch the picture by ID
            Optional<Picture> optionalPicture = pictureService.getOneById(pictureId);

            if (optionalPicture.isPresent()) {
                Picture picture = optionalPicture.get();

                // Update the description
                picture.setDescription(description);

                // Save the updated picture
                boolean isUpdated = pictureService.savePicture(picture);

                if (isUpdated) {
                    return new ResponseEntity<>("Picture description updated successfully", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Failed to update picture description", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Picture not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //todo werkt nu.
    @PostMapping("/save")
    public ResponseEntity<?> savePicture(@RequestParam("houseId") Integer houseId,
                                         @RequestParam("picture") MultipartFile pictureFile,
                                         @RequestParam("description") String description) {
        try {
            byte[] pictureData = pictureFile.getBytes();
            House house = houseService.getOneById(houseId);
            Picture picture = new Picture(house, pictureData, description);
            boolean isSaved = jdbcPictureDAO.storeOne(picture); // todo deze nog aanpassen naar service
            if (isSaved) {
                return new ResponseEntity<>("Picture saved successfully with ID: " + picture.getPictureId(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to save picture", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save picture: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //todo methode werkt
    @DeleteMapping("/delete/{pictureId}")
    public ResponseEntity<?> deletePictureById(@PathVariable("pictureId") int pictureId) {
        boolean isDeleted = pictureService.removeOneById(pictureId);
        if (isDeleted) {
            return new ResponseEntity<>("Picture deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Picture not found or deletion failed", HttpStatus.NOT_FOUND);
        }
    }







}
