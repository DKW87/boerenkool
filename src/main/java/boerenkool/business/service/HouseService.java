package boerenkool.business.service;

import boerenkool.business.model.House;
import boerenkool.business.model.HouseFilter;
import boerenkool.business.model.HouseType;
import boerenkool.business.model.Picture;
import boerenkool.communication.dto.HouseDetailsDTO;
import boerenkool.communication.dto.HouseListDTO;
import boerenkool.communication.dto.PictureDTO;
import boerenkool.database.repository.HouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:23
 */
@Service
public class HouseService {

    private CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(HouseService.class);
    private final HouseRepository houseRepository;
    private final UserService userService;
    private final PictureService pictureService;

    @Autowired
    public HouseService(HouseRepository houseRepository, CacheManager cacheManager, UserService userService,
                        PictureService pictureService) {
        this.houseRepository = houseRepository;
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.pictureService = pictureService;
        logger.info("New HouseService");
    }

    @Cacheable(value = "houses", key = "#houseId")
    public House getOneById(int houseId) {
        return houseRepository.getHouseById(houseId).orElse(null);
    }

    public HouseDetailsDTO getOneByIdAndConvertToDTO(int houseId) {
        House house = getOneById(houseId);
        return house == null
                ? null
                : convertHouseToHouseDetailsDTO(house);
    }

    private HouseDetailsDTO convertHouseToHouseDetailsDTO(House house) {
        HouseDetailsDTO houseDetailsDTO = new HouseDetailsDTO();
        houseDetailsDTO.setHouseId(house.getHouseId());
        houseDetailsDTO.setHouseName(house.getHouseName());
        houseDetailsDTO.setHouseType(house.getHouseType());
        houseDetailsDTO.setHouseOwnerId(house.getHouseOwner().getUserId());
        houseDetailsDTO.setHouseOwnerUsername(house.getHouseOwner().getUsername());
        houseDetailsDTO.setProvince(house.getProvince());
        houseDetailsDTO.setCity(house.getCity());
        houseDetailsDTO.setStreetAndNumber(house.getStreetAndNumber());
        houseDetailsDTO.setZipcode(house.getZipcode());
        houseDetailsDTO.setMaxGuest(house.getMaxGuest());
        houseDetailsDTO.setRoomCount(house.getRoomCount());
        houseDetailsDTO.setPricePPPD(house.getPricePPPD());
        houseDetailsDTO.setDescription(house.getDescription());
        houseDetailsDTO.setIsNotAvailable(house.getIsNotAvailable());
        if (house.getPictures() != null) {
            houseDetailsDTO.setPictures(convertListOfPicturesToDTO(house.getPictures()));
        }
        houseDetailsDTO.setExtraFeatures(house.getExtraFeatures());
        return houseDetailsDTO;
    }

    private List<PictureDTO> convertListOfPicturesToDTO(List<Picture> pictures) {
        List<PictureDTO> listPictureDTO = new ArrayList<>();
        for (Picture picture : pictures) {
            PictureDTO pictureDTO = pictureService.convertToDTO(picture);
            listPictureDTO.add(pictureDTO);
        }
        return listPictureDTO;
    }

    public List<House> getAllHouses() {
        return houseRepository.getListOfAllHouses();
    }

    public List<House> getListOfHousesByOwnerId(int houseOwnerId) {
        return houseRepository.getListOfAllHousesByOwner(houseOwnerId);
    }

    public String getHouseOwnerName(int houseOwnerId) {
        return userService.getOneById(houseOwnerId).get().getUsername();
    }

    public List<HouseListDTO> getFilteredListOfHouses(HouseFilter filter) {
        List<House> filteredHouses = houseRepository.getHousesWithFilter(filter);
        return filteredHouses.isEmpty()
                ? null
                : convertListToHouseListDTO(filteredHouses);
    }

    private List<HouseListDTO> convertListToHouseListDTO(List<House> houses) {
        List<HouseListDTO> strippedFilteredHouses = new ArrayList<>();
        for (House fullHouse : houses) {
            HouseListDTO strippedHouse = new HouseListDTO();
            strippedHouse.setHouseId(fullHouse.getHouseId());
            if (!fullHouse.getPictures().isEmpty()) {
                PictureDTO pictureDTO = pictureService.convertToDTO(fullHouse.getPictures().getFirst());
                strippedHouse.setPicture(pictureDTO);
            }
            strippedHouse.setHouseName(fullHouse.getHouseName());
            strippedHouse.setHouseType(fullHouse.getHouseType().getHouseTypeName());
            strippedHouse.setProvince(fullHouse.getProvince());
            strippedHouse.setCity(fullHouse.getCity());
            strippedHouse.setPrice(fullHouse.getPricePPPD());
            strippedFilteredHouses.add(strippedHouse);
        }
        return strippedFilteredHouses;
    }

    public List<String> getUniqueCities() {
        return houseRepository.getUniqueCities();
    }

    public List<HouseType> getAllHouseTypes() {
        return houseRepository.getAllHouseTypes();
    }

    public boolean saveHouse(House house) {
        boolean result = houseRepository.saveHouse(house);
        if (result) {
            updateCache(house);
        }
        return result;
    }

    private void updateCache(House house) {
        Cache cache = cacheManager.getCache("houses");
        if (cache != null) {
            cache.put(house.getHouseId(), house);
        }
    }

    @CacheEvict(value = "houses", key = "#houseId")
    public boolean deleteHouse(int houseId) {
        return houseRepository.deleteHouse(houseId);
    }

}
