package boerenkool.business.model;

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


    // constructors

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
    }

    // isNotAvailable optional constructor
    public House(String houseName, HouseType houseType, User houseOwner, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD, String description) {
        this(houseName, houseType, houseOwner, province, city, streetAndNumber, zipcode, maxGuest, roomCount, pricePPPD, description, false);
    }

    // description optional constructor
    public House(String houseName, HouseType houseType, User houseOwner, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD, boolean isNotAvailable) {
        this(houseName, houseType, houseOwner, province, city, streetAndNumber, zipcode, maxGuest, roomCount, pricePPPD, "", isNotAvailable);
    }

    // description and isNotAvailable optional constructor
    public House(String houseName, HouseType houseType, User houseOwner, String province, String city, String streetAndNumber,
                 String zipcode, int maxGuest, int roomCount, int pricePPPD) {
        this(houseName, houseType, houseOwner, province, city, streetAndNumber, zipcode, maxGuest, roomCount, pricePPPD, "", false);
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
    // TODO implement as needed

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

    public boolean isNotAvailable() {
        return isNotAvailable;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public void setProvince(String province) {
        if (province != null && province.matches("^[^0-9]*$")) { // Geen cijfers toegestaan
            this.province = province;
        } else {
            this.province = "Onbekend";
        }
    }

    public void setCity(String city) {
        if (city != null && city.matches("^[^0-9]*$")) { // Geen cijfers toegestaan
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
        if (zipcode != null && zipcode.matches("\\d{4}[A-Za-z]{2}")) { // zipcode moet bestaan uit 4 cijfers 2 letters
            this.zipcode = zipcode;
        } else {
            this.zipcode = "0000AA";
        }
    }

} // class
