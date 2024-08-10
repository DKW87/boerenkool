package boerenkool.business.model;

import java.util.List;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 15:36
 */
public class HouseFilter {

    private List<String> provinces;
    private List<String> cities;
    private List<HouseType> houseTypes;
    private User houseOwner;
    private int amountOfGuests;
    private int desiredRoomCount;
    private int minPricePPPD;
    private int maxPricePPPD;
    private String sortBy;
    private String sortOrder;
    private int limit; // used to determine how many results you want per page
    private int offset; // if you go to page 2 and result limit is 10, offset will become 10 (to hide page 1 results)

    private HouseFilter(Builder builder) {
        this.provinces = builder.provinces;
        this.cities = builder.cities;
        this.houseTypes = builder.houseTypes;
        this.houseOwner = builder.houseOwner;
        this.amountOfGuests = builder.amountOfGuests;
        this.desiredRoomCount = builder.desiredRoomCount;
        this.minPricePPPD = builder.minPricePPPD;
        this.maxPricePPPD = builder.maxPricePPPD;
        this.sortBy = builder.sortBy;
        this.sortOrder = builder.sortOrder;
        this.limit = builder.limit;
        this.offset = builder.offset;
    }

    // getters

    public List<String> getProvinces() {
        return provinces;
    }

    public List<String> getCities() {
        return cities;
    }

    public List<HouseType> getHouseTypes() {
        return houseTypes;
    }

    public User getHouseOwner() {
        return houseOwner;
    }

    public int getAmountOfGuests() {
        return amountOfGuests;
    }

    public int getDesiredRoomCount() {
        return desiredRoomCount;
    }

    public int getMinPricePPPD() {
        return minPricePPPD;
    }

    public int getMaxPricePPPD() {
        return maxPricePPPD;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public static class Builder {

        private List<String> provinces;
        private List<String> cities;
        private List<HouseType> houseTypes;
        private User houseOwner;
        private int amountOfGuests;
        private int desiredRoomCount;
        private int minPricePPPD;
        private int maxPricePPPD;
        private String sortBy;
        private String sortOrder;
        private int limit;
        private int offset;

        public Builder setProvinces(List<String> provinces) {
            this.provinces = provinces;
            return this;
        }

        public Builder setCities(List<String> cities) {
            this.cities = cities;
            return this;
        }

        public Builder setHouseTypes(List<HouseType> houseTypes) {
            this.houseTypes = houseTypes;
            return this;
        }

        public Builder setHouseOwner(User houseOwner) {
            this.houseOwner = houseOwner;
            return this;
        }

        public Builder setAmountOfGuests(int amountOfGuests) {
            this.amountOfGuests = amountOfGuests;
            return this;
        }

        public Builder setDesiredRoomCount(int desiredRoomCount) {
            this.desiredRoomCount = desiredRoomCount;
            return this;
        }

        public Builder setMinPricePPPD(int minPricePPPD) {
            this.minPricePPPD = minPricePPPD;
            return this;
        }

        public Builder setMaxPricePPPD(int maxPricePPPD) {
            this.maxPricePPPD = maxPricePPPD;
            return this;
        }

        public Builder setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public HouseFilter build() {
            return new HouseFilter(this);
        }

    } // builder class

} // housefilter class
