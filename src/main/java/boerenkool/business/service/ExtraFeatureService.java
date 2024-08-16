package boerenkool.business.service;

import boerenkool.business.model.ExtraFeature;
import boerenkool.database.repository.ExtraFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExtraFeatureService {

    private final ExtraFeatureRepository extraFeatureRepository;

    @Autowired
    public ExtraFeatureService(ExtraFeatureRepository extraFeatureRepository) {
        this.extraFeatureRepository = extraFeatureRepository;
    }

    public List<ExtraFeature> getAllExtraFeatures() {
        return extraFeatureRepository.getAll();
    }

    public Optional<ExtraFeature> getExtraFeatureById(int id) {
        return extraFeatureRepository.getOneById(id);
    }

    public ExtraFeature saveExtraFeature(ExtraFeature extraFeature) {
        if (extraFeatureRepository.findByName(extraFeature.getExtraFeatureName()).isPresent()) {
            throw new IllegalArgumentException("Een Extra Feature met deze naam bestaat al.");
        }
        validateExtraFeature(extraFeature);
        extraFeatureRepository.storeOne(extraFeature);
        return extraFeature;
    }

    public boolean deleteExtraFeatureById(int id) {
        // Controleer of de te verwijderen ExtraFeature bestaat
        if (!extraFeatureRepository.getOneById(id).isPresent()) {
            throw new IllegalArgumentException("Een ExtraFeature met ID " + id + " bestaat niet.");
        }
        extraFeatureRepository.removeOneById(id);
        return true;
    }

    public boolean updateExtraFeature(ExtraFeature extraFeature) {
        if (!extraFeatureRepository.getOneById(extraFeature.getExtraFeatureId()).isPresent()) {
            throw new IllegalArgumentException("Een ExtraFeature met ID " + extraFeature.getExtraFeatureId() + " bestaat niet.");
        }
        validateExtraFeature(extraFeature);
        return extraFeatureRepository.updateOne(extraFeature);
    }

    public Optional<ExtraFeature> findExtraFeatureByName(String name) {
        return extraFeatureRepository.findByName(name);
    }

    private void validateExtraFeature(ExtraFeature extraFeature) {
        if (extraFeature.getExtraFeatureName() == null || extraFeature.getExtraFeatureName().isEmpty()) {
            throw new IllegalArgumentException("Feature naam mag niet leeg zijn.");
        }
    }
}
