package boerenkool.business.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 07/08/2024 - 14:35
 */
public class House implements Comparable<House> {

    // attributes
    private int houseId;
    private String houseName;
    private HouseType houseType;
    private User houseOwner;
    private String province;
    private String city;
    private String streetAndNumber;
    private String zipcode;
    private int maxGuest;
    private int roomCount;
    private int pricePPPD;
    private String description;
    private boolean isNotAvailable;
    private List<Picture> pictures;
    private List<ExtraFeature> extraFeatures;
    private otherEntityIds otherEntityIds;

    // constructors

    // all args
    public House(String houseName, HouseType houseType, User houseOwner, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD, String description, boolean isNotAvailable) {
        this.houseName = houseName;
        this.houseType = houseType;
        this.houseOwner = houseOwner;
        this.setProvince(province);
        this.setCity(city);
        this.streetAndNumber = streetAndNumber;
        this.setZipcode(zipcode);
        this.setMaxGuest(maxGuest);
        this.setRoomCount(roomCount);
        this.setPricePPPD(pricePPPD);
        this.description = description;
        this.isNotAvailable = isNotAvailable;
        this.otherEntityIds = new otherEntityIds();
    }

    // housetype + user optional constructor
    public House(String houseName, String province, String city, String streetAndNumber, String zipcode,
                 int maxGuest, int roomCount, int pricePPPD, String description, boolean isNotAvailable) {
        this(houseName, null, null, province, city, streetAndNumber, zipcode, maxGuest,
                roomCount, pricePPPD, description, isNotAvailable);
    }


    // isNotAvailable optional constructor
    public House(String houseName, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD, String description) {
        this(houseName, province, city, streetAndNumber, zipcode, maxGuest, roomCount,
                pricePPPD, description, false);
    }

    // description optional constructor
    public House(String houseName, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD, boolean isNotAvailable) {
        this(houseName, province, city, streetAndNumber, zipcode, maxGuest,
                roomCount, pricePPPD, "", isNotAvailable);
    }

    public House() {
        // Standaardconstructor, nodig voor Jackson (de library die JSON naar Java-objecten omzet heeft dit schijnbaar nodig)
    }

    // methods

    @Override
    public String toString() {
        return this.houseName;
    }

    @Override
    public int compareTo(House other) {
        return Integer.compare(this.houseId, other.houseId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return houseId == house.houseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseId);
    }

    // getters and setters

    public int getHouseId() {
        return houseId;
    }

    public String getHouseName() {
        return houseName;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public User getHouseOwner() {
        return houseOwner;
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

    public boolean getIsNotAvailable() {
        return isNotAvailable;
    }

    public List<ExtraFeature> getExtraFeatures() {
        return extraFeatures;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public otherEntityIds accessOtherEntityIds() {
        return otherEntityIds;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public void setHouseOwner(User houseOwner) {
        this.houseOwner = houseOwner;
    }

    public void setProvince(String province) {
        if (province != null && province.matches("^[^0-9]*$")) { // no numbers allowed
            this.province = province;
        } else {
            this.province = "Onbekend";
        }
    }

    public void setCity(String city) {
        if (city != null && city.matches("^[^0-9]*$")) { // no numbers allowed
            this.city = city;
        } else {
            this.city = "Onbekend";
        }
    }

    public void setPricePPPD(int pricePPPD) {
        if (pricePPPD > 0) {
            this.pricePPPD = pricePPPD;
        }
        else {
            this.pricePPPD = 0;
        }
    }

    public void setRoomCount(int roomCount) {
        if (roomCount > 0) {
            this.roomCount = roomCount;
        }
        else {
            this.roomCount = 0;
        }
    }

    public void setMaxGuest(int maxGuest) {
        if (maxGuest > 0) {
            this.maxGuest = maxGuest;
        }
        else {
            this.maxGuest = 0;
        }
    }

    public void setZipcode(String zipcode) {
        if (zipcode != null && zipcode.matches("\\d{4}[A-Za-z]{2}")) { // zipcode consists of 4 numbers 2 letters
            this.zipcode = zipcode;
        } else {
            this.zipcode = "0000AA";
        }
    }

    public void setExtraFeatures(List<ExtraFeature> extraFeatures) {
        this.extraFeatures = extraFeatures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsNotAvailable(boolean isNotAvailable) {
        this.isNotAvailable = isNotAvailable;
    }

    public static class otherEntityIds {

        int houseOwnerId;
        int houseTypeId;

        public int getHouseOwnerId() {
            return houseOwnerId;
        }

        public int getHouseTypeId() {
            return houseTypeId;
        }

        public void setHouseOwnerId(int houseOwnerId) {
            this.houseOwnerId = houseOwnerId;
        }

        public void setHouseTypeId(int houseTypeId) {
            this.houseTypeId = houseTypeId;
        }

    }

} // class
