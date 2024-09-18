package boerenkool.communication.controller;
import boerenkool.business.model.House;
import boerenkool.business.model.Picture;
import boerenkool.business.model.User;
import boerenkool.business.service.HouseService;
import boerenkool.business.service.PictureService;
import boerenkool.communication.dto.PictureDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import boerenkool.utilities.authorization.AuthorizationService;
import java.util.UUID;
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
    private final AuthorizationService authorizationService;

    @Autowired
    public PictureController(PictureService pictureService, HouseService houseService, AuthorizationService authorizationService) {
        this.pictureService = pictureService;
        this.houseService = houseService;
        this.authorizationService = authorizationService;
    }

    // Test mapping
    @GetMapping
    public String showPicturesMessage() {
        return "Als je dit leest dan is de test succesvol";
    }



    @GetMapping("/houses/{houseId}")
    public ResponseEntity<?> getPicturesByHouseId(@PathVariable("houseId") int houseId) {
        List<Picture> pictureList = pictureService.getAllByHouseId(houseId);
        if (pictureList != null && !pictureList.isEmpty()) {
            List<PictureDTO> pictureDTOList = pictureList.stream().map(pictureService::convertToDTO).collect(Collectors.toList());
            System.out.println("getPicturesByHouseId activated");

            return new ResponseEntity<>(pictureDTOList, HttpStatus.OK);

        } else {
            return new ResponseEntity<>("Geen pictures gevonden bij dit huis: ", HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<>("Picture succesvol opgeslagen met ID: " + picture.getPictureId(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Save picture niet gelukt: ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Save picture niet gelukt: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{pictureId}")
    public ResponseEntity<?> updatePictureDescription(@RequestHeader("Authorization") String token,
                                                      @PathVariable("pictureId") int pictureId,
                                                      @RequestParam("description") String description) {
        try {
            Optional<Picture> optionalPicture = pictureService.getOneById(pictureId);
            if (optionalPicture.isPresent()) {
                Picture picture = optionalPicture.get();
                picture.setDescription(description);
                boolean isUpdated = pictureService.savePicture(picture);

                if (isUpdated) {
                    System.out.println("updatePictureDescription activated");
                    return new ResponseEntity<>("Picture omschrijving succesvol geupdate", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Picture omschrijving update is niet gelukt", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Picture niet gevonden", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Er heeft een error plaatsgevonden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/{houseId}")
    public ResponseEntity<?> uploadPicture(@RequestHeader("Authorization") String token,
                                           @PathVariable("houseId") int houseId,
                                           @RequestParam("picture") MultipartFile pictureFile,
                                           @RequestParam("description") String description) {
        try {
            Optional<User> optionalUser = authorizationService.validate(UUID.fromString(token));
            if (optionalUser.isPresent()) {
                int userId = optionalUser.get().getUserId();
                House house = houseService.getOneById(houseId);
                if (house != null) {
                    int userRealId = house.getHouseOwner().getUserId();
                    if (userId != userRealId) {
                        return new ResponseEntity<>("Niet geauthoriseerd", HttpStatus.FORBIDDEN);
                    }

                    byte[] pictureData = pictureFile.getBytes();
                    Picture picture = new Picture(house, pictureData, description);
                    boolean isSaved = pictureService.savePicture(picture);

                    if (isSaved) {
                        System.out.println("uploadPicture activated");
                        return new ResponseEntity<>("Picture succesvol geupload", HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>("Uploaden van picture mislukt", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>("Huis niet gevonden", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Invalide token", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Picture upload error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/delete/{pictureId}")
    public ResponseEntity<?> deletePictureById(@RequestHeader("Authorization") String token, @PathVariable int pictureId) {
        Optional<User> optionalUser = authorizationService.validate(UUID.fromString(token));
        if (optionalUser.isPresent()) {
            int userId = optionalUser.get().getUserId();
            System.out.println("userId" + userId);

            if (pictureId <= 0) {
                return new ResponseEntity<>("Picture ID kan niet 0 of negatief zijn", HttpStatus.BAD_REQUEST);

            }

            Optional<Picture> optionalPicture = pictureService.getOneById(pictureId);
            if (optionalPicture.isEmpty()) {
                return new ResponseEntity<>("Picture niet gevonden", HttpStatus.NOT_FOUND);
            }

            Picture picture = optionalPicture.get();
            int pictureHouseId = picture.getHouseId();
            House retrievedHouse = houseService.getOneById(pictureHouseId);
            int userRealId = retrievedHouse.getHouseOwner().getUserId();
            if (userId != userRealId) {
                System.out.println("niet geauthoriseerd");
                return new ResponseEntity<>("Niet geauthoriseerd", HttpStatus.FORBIDDEN);
            }

            boolean isDeleted = pictureService.removeOneById(pictureId);
            if (isDeleted) {
                System.out.println("Succesvol verwijderd");
                return new ResponseEntity<>("Picture succesvol verwijderd", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Niet mogelijk om picture te verwijderen", HttpStatus.CONFLICT);
            }
        }

        return new ResponseEntity<>("Invalide token", HttpStatus.UNAUTHORIZED);
    }


} // end of class
