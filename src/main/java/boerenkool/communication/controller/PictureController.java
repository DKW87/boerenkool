package boerenkool.communication.controller;
import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.PictureService;
import boerenkool.communication.dto.PictureDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pictures")
public class PictureController {

    private final PictureService pictureService;
    private final HouseService houseService;

    public PictureController(PictureService pictureService, HouseService houseService) {
        this.pictureService = pictureService;
        this.houseService = houseService;
    }

    // Test mapping
    @GetMapping
    public String showPicturesMessage() {
        return "Hello user, if you see this everything is OK.";
    }



    @GetMapping("/houses/{houseId}")
    public ResponseEntity<?> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
        List<Picture> pictureList = pictureService.getAllByHouseId(houseId);
        if (pictureList != null && !pictureList.isEmpty()) {
            List<PictureDTO> pictureDTOList = pictureList.stream().map(pictureService::convertToDTO).collect(Collectors.toList());
            System.out.println("getPicturesByHouseId activated");

            return new ResponseEntity<>(pictureDTOList, HttpStatus.OK);

        } else {
            return new ResponseEntity<>("No images found for this house ID", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/houses/first/{houseId}")
    public ResponseEntity<?> getFirstPictureOfHouse(@PathVariable("houseId") int houseId) {
        Optional<Picture> picture = pictureService.getFirstPictureByHouseId(houseId);
        if (picture.isPresent()) {
            PictureDTO pictureDTO = pictureService.convertToDTO(picture.get());
            System.out.println("getFirstPictureByHouseId activated");
            return ResponseEntity.ok(pictureDTO);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{pictureId}")
    public ResponseEntity<PictureDTO> getPictureById(@PathVariable("pictureId") int pictureId) {
        Optional<Picture> pictureOptional = pictureService.getOneById(pictureId);
        if (pictureOptional.isPresent()) {
            PictureDTO pictureDTO = pictureService.convertToDTO(pictureOptional.get());
            System.out.println("getPictureById activated");
            return ResponseEntity.ok(pictureDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/update/{pictureId}")
    public ResponseEntity<?> updatePictureDescription(@PathVariable("pictureId") int pictureId,
                                                      @RequestParam("description") String description) {
        try {
            Optional<Picture> optionalPicture = pictureService.getOneById(pictureId);
            if (optionalPicture.isPresent()) {
                Picture picture = optionalPicture.get();
                picture.setDescription(description);
                boolean isUpdated = pictureService.savePicture(picture);

                if (isUpdated) {
                    System.out.println("updatePictureDescription activated");
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


    @PostMapping("/save")
    public ResponseEntity<?> savePicture(@RequestParam("houseId") Integer houseId,
                                         @RequestParam("picture") MultipartFile pictureFile,
                                         @RequestParam("description") String description) {
        try {
            byte[] pictureData = pictureFile.getBytes();
            House house = houseService.getOneById(houseId);
            Picture picture = new Picture(house, pictureData, description);
            boolean isSaved = pictureService.savePicture(picture);
            if (isSaved) {
                System.out.println("savePicture activated");
                return new ResponseEntity<>("Picture saved successfully with ID: " + picture.getPictureId(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to save picture", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save picture: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{pictureId}")
    public ResponseEntity<?> deletePictureById(@PathVariable("pictureId") int pictureId) {
        boolean isDeleted = pictureService.removeOneById(pictureId);
        if (isDeleted) {
            System.out.println("deletePictureById activated");
            return new ResponseEntity<>("Picture deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Picture not found or deletion failed", HttpStatus.NOT_FOUND);
        }
    }


}
