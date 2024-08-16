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

    // Alle HouseTypes ophalen
    public List<HouseType> getAllHouseTypes() {
        return houseTypeRepository.getAll();
    }

    // Een HouseType op basis van ID ophalen
    public Optional<HouseType> getHouseTypeById(int id) {
        return houseTypeRepository.getOneById(id);
    }

    // Een nieuwe HouseType opslaan
    public HouseType saveHouseType(HouseType houseType) {
        // Controleren of er al een HouseType met dezelfde naam bestaat
        if (houseTypeRepository.findByName(houseType.getHouseTypeName()).isPresent()) {
            throw new IllegalArgumentException("Een HouseType met deze naam bestaat al.");
        }

        // Valideer de HouseType en sla deze op
        validateHouseType(houseType);
        houseTypeRepository.storeOne(houseType);
        return houseType;
    }

    // Een HouseType verwijderen op basis van ID
    public boolean deleteHouseTypeById(int id) {
        if (!houseTypeRepository.getOneById(id).isPresent()) {
            throw new IllegalArgumentException("Een HouseType met ID " + id + " bestaat niet.");
        }
        houseTypeRepository.removeOneById(id);
        return true;
    }

    // Een HouseType bijwerken
    public boolean updateHouseType(HouseType houseType) {
        if (!houseTypeRepository.getOneById(houseType.getHouseTypeId()).isPresent()) {
            throw new IllegalArgumentException("Een HouseType met ID " + houseType.getHouseTypeId() + " bestaat niet.");
        }

        // Controleren of een andere HouseType al dezelfde naam heeft
        Optional<HouseType> existingHouseType = houseTypeRepository.findByName(houseType.getHouseTypeName());
        if (existingHouseType.isPresent() && existingHouseType.get().getHouseTypeId() != houseType.getHouseTypeId()) {
            throw new IllegalArgumentException("Een andere HouseType met deze naam bestaat al.");
        }

        validateHouseType(houseType);
        return houseTypeRepository.updateOne(houseType);
    }

    // Een HouseType zoeken op naam
    public Optional<HouseType> findHouseTypeByName(String name) {
        return houseTypeRepository.findByName(name);
    }

    // Valideren van een HouseType
    private void validateHouseType(HouseType houseType) {
        if (houseType.getHouseTypeName() == null || houseType.getHouseTypeName().isEmpty()) {
            throw new IllegalArgumentException("HouseType naam mag niet leeg zijn.");
        }
    }
}
