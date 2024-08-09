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
    private int maxPricePPD;
    private int limit; // used to determine how many results you want per page
    private int offset; // if you go to page 2 and result limit is 10, offset will become 10 (to hide page 1 results)

    public HouseFilter(List<String> provinces, List<String> cities, List<HouseType> houseTypes, User houseOwner,
                        int amountOfGuests, int desiredRoomCount,int minPricePPPD, int maxPricePPD, int limit, int offset) {
        this.provinces = provinces;
        this.cities = cities;
        this.houseTypes = houseTypes;
        this.houseOwner = houseOwner;
        this.amountOfGuests = amountOfGuests;
        this.desiredRoomCount = desiredRoomCount;
        this.minPricePPPD = minPricePPPD;
        this.maxPricePPD = maxPricePPD;
        this.limit = limit;
        this.offset = offset;
    }

    public static class Builder {

        private List<String> provinces;
        private List<String> cities;
        private List<HouseType> houseTypes;
        private User houseOwner;
        private int amountOfGuests;
        private int desiredRoomCount;
        private int minPricePPPD;
        private int maxPricePPD;
        private int limit;
        private int offset;

        public Builder containsProvinces(List<String> provinces) {
            this.provinces = provinces;
            return this;
        }

        public Builder containsCities(List<String> cities) {
            this.cities = cities;
            return this;
        }

        public Builder containsHouseTypes(List<HouseType> houseTypes) {
            this.houseTypes = houseTypes;
            return this;
        }

        public Builder containsHouseOwner(User houseOwner) {
            this.houseOwner = houseOwner;
            return this;
        }

        public Builder containsAmountOfGuests(int amountOfGuests) {
            this.amountOfGuests = amountOfGuests;
            return this;
        }

        public Builder containsDesiredRoomCount(int desiredRoomCount) {
            this.desiredRoomCount = desiredRoomCount;
            return this;
        }

        public Builder containsMinPricePPPD(int minPricePPPD) {
            this.minPricePPPD = minPricePPPD;
            return this;
        }

        public Builder containsMaxPricePPPD(int maxPricePPPD) {
            this.maxPricePPD = maxPricePPPD;
            return this;
        }

        public Builder containsLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder containsOffset(int offset) {
            this.offset = offset;
            return this;
        }

    } // builder class

} // housefilter class
