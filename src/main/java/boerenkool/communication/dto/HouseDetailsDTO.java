package boerenkool.communication.dto;

import boerenkool.business.model.ExtraFeature;
import boerenkool.business.model.HouseType;

import java.util.List;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 29/08/2024 - 07:50
 */
public class HouseDetailsDTO {

    // attributes
    int houseId;
    String houseName;
    HouseType houseType;
    int houseOwnerId;
    String houseOwnerUsername;
    String province;
    String city;
    String streetAndNumber;
    String zipcode;
    int maxGuest;
    int roomCount;
    int pricePPPD;
    String description;
    boolean isNotAvailable;
    List<PictureDTO> pictures;
    List<ExtraFeature> extraFeatures;

    public HouseDetailsDTO() { /* empty constructor for jackson, use setters */ }

    // setters
    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public void setHouseOwnerId(int houseOwnerId) {
        this.houseOwnerId = houseOwnerId;
    }

    public void setHouseOwnerUsername(String houseOwnerUsername) {
        this.houseOwnerUsername = houseOwnerUsername;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setMaxGuest(int maxGuest) {
        this.maxGuest = maxGuest;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public void setPricePPPD(int pricePPPD) {
        this.pricePPPD = pricePPPD;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsNotAvailable(boolean notAvailable) {
        isNotAvailable = notAvailable;
    }

    public void setPictures(List<PictureDTO> pictures) {
        this.pictures = pictures;
    }

    public void setExtraFeatures(List<ExtraFeature> extraFeatures) {
        this.extraFeatures = extraFeatures;
    }

    // getters
    public int getHouseId() {
        return houseId;
    }

    public String getHouseName() {
        return houseName;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public int getHouseOwnerId() {
        return houseOwnerId;
    }

    public String getHouseOwnerUsername() {
        return houseOwnerUsername;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public int getMaxGuest() {
        return maxGuest;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public int getPricePPPD() {
        return pricePPPD;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNotAvailable() {
        return isNotAvailable;
    }

    public List<PictureDTO> getPictures() {
        return pictures;
    }

    public List<ExtraFeature> getExtraFeatures() {
        return extraFeatures;
    }

} // class
