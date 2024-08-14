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

    public HouseExtraFeature saveHouseExtraFeature(HouseExtraFeature houseExtraFeature) {
        houseExtraFeatureRepository.storeOne(houseExtraFeature);
        return houseExtraFeature;
    }

    public boolean deleteHouseExtraFeatureByIds(int houseId, int featureId) {
        houseExtraFeatureRepository.removeOneByIds(houseId, featureId);
        return true;
    }

    public List<HouseExtraFeature> getAllFeaturesByHouseId(int houseId) {
        return houseExtraFeatureRepository.getAllFeaturesByHouseId(houseId);
    }
}

