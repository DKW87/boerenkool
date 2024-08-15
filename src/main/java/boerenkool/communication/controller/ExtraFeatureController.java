package boerenkool.communication.controller;

import boerenkool.business.model.ExtraFeature;
import boerenkool.business.service.ExtraFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/extraFeatures")
public class ExtraFeatureController {

    private final Logger logger = LoggerFactory.getLogger(ExtraFeatureController.class);
    private final ExtraFeatureService extraFeatureService;

    @Autowired
    public ExtraFeatureController(ExtraFeatureService extraFeatureService) {
        this.extraFeatureService = extraFeatureService;
        logger.info("Nieuwe ExtraFeatureController aangemaakt");
    }

    @GetMapping
    public List<ExtraFeature> getAllExtraFeatures() {
        logger.info("Alle extra features ophalen");
        return extraFeatureService.getAllExtraFeatures();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getExtraFeatureById(@PathVariable int id) {
        logger.info("Extra feature ophalen met ID: {}", id);
        Optional<ExtraFeature> extraFeature = extraFeatureService.getExtraFeatureById(id);
        if (extraFeature.isPresent()) {
            return new ResponseEntity<>(extraFeature.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ExtraFeature niet gevonden", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<String> createExtraFeature(@RequestBody ExtraFeature extraFeature) {
        try {
            logger.info("Nieuwe extra feature aanmaken: {}", extraFeature.getExtraFeatureName());
            ExtraFeature savedExtraFeature = extraFeatureService.saveExtraFeature(extraFeature);
            return new ResponseEntity<>("ExtraFeature succesvol aangemaakt met ID: " + savedExtraFeature.getExtraFeatureId(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Fout bij het aanmaken van extra feature: {}", e.getMessage());
            return new ResponseEntity<>("Fout bij het aanmaken van ExtraFeature: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateExtraFeature(@PathVariable int id, @RequestBody ExtraFeature extraFeature) {
        logger.info("Extra feature bijwerken met ID: {}", id);
        extraFeature.setExtraFeatureId(id);
        boolean updated = extraFeatureService.updateExtraFeature(extraFeature);
        if (updated) {
            return new ResponseEntity<>("ExtraFeature succesvol bijgewerkt", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Mislukt om ExtraFeature bij te werken", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteExtraFeature(@PathVariable int id) {
        logger.info("Extra feature verwijderen met ID: {}", id);
        boolean deleted = extraFeatureService.deleteExtraFeatureById(id);
        if (deleted) {
            return new ResponseEntity<>("ExtraFeature succesvol verwijderd", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Mislukt om ExtraFeature te verwijderen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<?> findExtraFeatureByName(@PathVariable String name) {
        logger.info("Extra feature ophalen met naam: {}", name);
        Optional<ExtraFeature> extraFeature = extraFeatureService.findExtraFeatureByName(name);
        if (extraFeature.isPresent()) {
            return new ResponseEntity<>(extraFeature.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ExtraFeature niet gevonden", HttpStatus.NOT_FOUND);
        }
    }
}
