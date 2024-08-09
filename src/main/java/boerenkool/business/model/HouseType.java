package boerenkool.business.model;

/**
 * @author Emine Sernur YILDIRIM
 */

public class HouseType {

    // attributes
    private int houseTypeId;
    private String houseTypeName;

    // constructors
    public HouseType(int houseTypeId, String houseTypeName) {
        this.houseTypeId = houseTypeId;
        this.houseTypeName = houseTypeName;
    }

    public HouseType(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

    // getters and setters
    public int getHouseTypeId() {
        return houseTypeId;
    }

    public void setHouseTypeId(int houseTypeId) {
        this.houseTypeId = houseTypeId;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

    @Override
    public String toString() {
        return this.houseTypeName;
    }
}
