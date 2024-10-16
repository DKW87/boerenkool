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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 13/08/2024 - 12:23
 */
@Service
public class HouseService {

    public static final int NEW_HOUSE_ID = 0;
    private final Logger logger = LoggerFactory.getLogger(HouseService.class);
    private final HouseRepository houseRepository;
    private final UserService userService;
    private final PictureService pictureService;


    @Autowired
    public HouseService(HouseRepository houseRepository, UserService userService, PictureService pictureService) {
        this.houseRepository = houseRepository;
        this.userService = userService;
        this.pictureService = pictureService;
        logger.info("New HouseService");
    }


    public House getOneById(int houseId) {
        return houseRepository.getHouseById(houseId).orElse(null);
    }

    public HouseDetailsDTO getOneByIdToDTO(int houseId) {
        House house = getOneById(houseId);
        return house == null
                ? null
                : toHouseDetailsDTO(house);
    }

    public List<HouseListDTO> getListByOwnerId(int houseOwnerId) {
        List<House> houses = houseRepository.getListOfHousesByOwner(houseOwnerId);

        return houses.isEmpty()
                ? new ArrayList<>()
                : toHouseListDTO(houses);
    }

    public List<HouseListDTO> getFilteredList(HouseFilter filter) {
        List<House> filteredHouses = houseRepository.getHousesWithFilter(filter);
        return filteredHouses.isEmpty()
                ? new ArrayList<>()
                : toHouseListDTO(filteredHouses);
    }

    public int countFilterResult(HouseFilter filter) {
        return houseRepository.countHousesWithFilter(filter);
    }

    public List<String> getUniqueCities() {
        return houseRepository.getUniqueCities();
    }

    public List<HouseType> getAllHouseTypes() {
        return houseRepository.getAllHouseTypes();
    }

    public boolean saveHouse(HouseDetailsDTO house) {
        House fullHouse = toHouse(house);
        boolean result = houseRepository.saveHouse(fullHouse);
        if (house.getHouseId() == NEW_HOUSE_ID) {
            house.setHouseId(fullHouse.getHouseId()); 
        }
        return result;
    }

    private List<HouseListDTO> toHouseListDTO(List<House> houses) {
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

    private HouseDetailsDTO toHouseDetailsDTO(House house) {
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
            houseDetailsDTO.setPictures(picturesToDTOs(house.getPictures()));
        }
        houseDetailsDTO.setExtraFeatures(house.getExtraFeatures());
        return houseDetailsDTO;
    }

    private List<PictureDTO> picturesToDTOs(List<Picture> pictures) {
        List<PictureDTO> listPictureDTO = new ArrayList<>();
        for (Picture picture : pictures) {
            PictureDTO pictureDTO = pictureService.convertToDTO(picture);
            listPictureDTO.add(pictureDTO);
        }
        return listPictureDTO;
    }

    private House toHouse(HouseDetailsDTO houseDetailsDTO) {
        House house = new House();
        if (houseDetailsDTO.getHouseId() != NEW_HOUSE_ID) {
            house.setHouseId(houseDetailsDTO.getHouseId());
        }
        house.setHouseId(houseDetailsDTO.getHouseId());
        house.setHouseName(houseDetailsDTO.getHouseName());
        house.setHouseType(houseDetailsDTO.getHouseType());
        house.setHouseOwner(userService.getOneById(houseDetailsDTO.getHouseOwnerId())
                .orElseThrow(() -> new NoSuchElementException("user not found")));
        house.setProvince(houseDetailsDTO.getProvince());
        house.setCity(houseDetailsDTO.getCity());
        house.setStreetAndNumber(houseDetailsDTO.getStreetAndNumber());
        house.setZipcode(houseDetailsDTO.getZipcode());
        house.setMaxGuest(houseDetailsDTO.getMaxGuest());
        house.setRoomCount(houseDetailsDTO.getRoomCount());
        house.setPricePPPD(houseDetailsDTO.getPricePPPD());
        house.setDescription(houseDetailsDTO.getDescription());
        house.setIsNotAvailable(houseDetailsDTO.isNotAvailable());
        return house;
    }

    public boolean deleteHouse(int houseId) {
        return houseRepository.deleteHouse(houseId);
    }

}
