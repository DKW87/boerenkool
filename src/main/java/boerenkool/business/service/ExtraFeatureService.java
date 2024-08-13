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
        validateExtraFeature(extraFeature);
        extraFeatureRepository.storeOne(extraFeature);
        return extraFeature;
    }

    public boolean deleteExtraFeatureById(int id) {
        extraFeatureRepository.removeOneById(id);
        return true;
    }

    public boolean updateExtraFeature(ExtraFeature extraFeature) {
        validateExtraFeature(extraFeature);
        return extraFeatureRepository.updateOne(extraFeature);
    }

    public Optional<ExtraFeature> findExtraFeatureByName(String name) {
        return extraFeatureRepository.findByName(name);
    }

    private void validateExtraFeature(ExtraFeature extraFeature) {
        if (extraFeature.getExtraFeatureName() == null || extraFeature.getExtraFeatureName().isEmpty()) {
            throw new IllegalArgumentException("Feature name cannot be null or empty.");
        }
    }
}
