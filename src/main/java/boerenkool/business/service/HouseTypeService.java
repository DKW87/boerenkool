package boerenkool.business.service;

import boerenkool.business.model.HouseType;
import boerenkool.database.repository.HouseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseTypeService {

    private final HouseTypeRepository houseTypeRepository;

    @Autowired
    public HouseTypeService(HouseTypeRepository houseTypeRepository) {
        this.houseTypeRepository = houseTypeRepository;
    }

    public List<HouseType> getAllHouseTypes() {
        return houseTypeRepository.getAll();
    }

    public Optional<HouseType> getHouseTypeById(int id) {
        return houseTypeRepository.getOneById(id);
    }

    public HouseType saveHouseType(HouseType houseType) {
        validateHouseType(houseType);
        houseTypeRepository.storeOne(houseType);
        return houseType;
    }

    public boolean deleteHouseTypeById(int id) {
        houseTypeRepository.removeOneById(id);
        return true;
    }

    public boolean updateHouseType(HouseType houseType) {
        validateHouseType(houseType);
        return houseTypeRepository.updateOne(houseType);
    }

    public Optional<HouseType> findHouseTypeByName(String name) {
        return houseTypeRepository.findByName(name);
    }

    private void validateHouseType(HouseType houseType) {
        if (houseType.getHouseTypeName() == null || houseType.getHouseTypeName().isEmpty()) {
            throw new IllegalArgumentException("House type name cannot be null or empty.");
        }
    }
}
