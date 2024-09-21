package boerenkool.business.service;

import boerenkool.business.model.HouseExtraFeature;
import boerenkool.database.repository.HouseExtraFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseExtraFeatureService {

    private final HouseExtraFeatureRepository houseExtraFeatureRepository;

    @Autowired
    public HouseExtraFeatureService(HouseExtraFeatureRepository houseExtraFeatureRepository) {
        this.houseExtraFeatureRepository = houseExtraFeatureRepository;
    }


    public List<HouseExtraFeature> getAllHouseExtraFeatures() {
        return houseExtraFeatureRepository.getAll();
    }


    public Optional<HouseExtraFeature> getHouseExtraFeatureByIds(int houseId, int featureId) {
        return houseExtraFeatureRepository.getOneByIds(houseId, featureId);
    }

    public List<HouseExtraFeature> getAllFeaturesByHouseIdWithNames(int houseId) {
        return houseExtraFeatureRepository.getAllFeaturesByHouseIdWithNames(houseId);
    }


    public void saveAllHouseExtraFeatures(List<HouseExtraFeature> houseExtraFeatures) {
        houseExtraFeatures.forEach(this::saveHouseExtraFeature);
    }

    public HouseExtraFeature saveHouseExtraFeature(HouseExtraFeature houseExtraFeature) {
        houseExtraFeatureRepository.storeOne(houseExtraFeature);
        return houseExtraFeature;
    }

    public void updateHouseExtraFeatures(int houseId, List<HouseExtraFeature> newFeatures) {

        houseExtraFeatureRepository.removeAllByHouseId(houseId);


        houseExtraFeatureRepository.storeAll(newFeatures);
    }



    public boolean deleteHouseExtraFeatureByIds(int houseId, int featureId) {
        if (!houseExtraFeatureRepository.getOneByIds(houseId, featureId).isPresent()) {
            throw new IllegalArgumentException("Een HouseExtraFeature met houseId " + houseId + " en featureId " + featureId + " bestaat niet.");
        }
        houseExtraFeatureRepository.removeOneByIds(houseId, featureId);
        return true;
    }

    public List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId) {
        return houseExtraFeatureRepository.getAllFeaturesByHouseId(houseId);
    }

    private void validateHouseExtraFeature(HouseExtraFeature houseExtraFeature) {
        if (houseExtraFeature.getHouseId() <= 0 || houseExtraFeature.getFeatureId() <= 0) {
            throw new IllegalArgumentException("Zowel houseId als featureId moeten geldig zijn (groter dan 0).");
        }
    }

    public void removeAllExtraFeaturesFromHouse(int houseId) {
        List<HouseExtraFeature> features = houseExtraFeatureRepository.getAllFeaturesByHouseId(houseId);
        for (HouseExtraFeature feature : features) {
            houseExtraFeatureRepository.removeOneByIds(houseId, feature.getFeatureId());
        }
    }
}
