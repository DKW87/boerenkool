package boerenkool.business.model;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 07/08/2024 - 14:35
 */
public class House {

    // attributes
    private int houseId;
    private String houseName; // add to DB + UML/DomainModel?
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
        this.province = province;
        this.city = city;
        this.streetAndNumber = streetAndNumber;
        this.zipcode = zipcode;
        this.maxGuest = maxGuest;
        this.roomCount = roomCount;
        this.pricePPPD = pricePPPD;
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


    // getters and setters
    // TODO implement as needed

}
