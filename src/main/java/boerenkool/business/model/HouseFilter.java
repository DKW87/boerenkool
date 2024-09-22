package boerenkool.business.model;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Danny KWANT
 * @project Boerenkool
 * @created 09/08/2024 - 15:36
 */

/**
 * This HouseFilter class was made to avoid having filter methods in DAO which requires too many param.
 * Instead, param are consolidated in own class and are build using the builder of this class.
 *
 * Example use of HouseFilter:
 * HouseFilter filter = new HouseFilter.Builder()
 * .setProvinces(Arrays.asList("Noord-Holland"))
 * .setCities(Arrays.asList("Amsterdam", "Alkmaar", "Hilversum"))
 * .setAmountOfGuests(4)
 * .setDesiredRoomCount(4)
 * .setMinPricePPPD(25)
 * .setSortBy("houseId")
 * .setSortOrder("DESC")
 * .setLimit(10)
 * .build();
 *
 */
public class HouseFilter {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> provinces;
    private List<String> cities;
    private List<Integer> houseTypeIds;
    private int houseOwnerId;
    private int amountOfGuests;
    private int desiredRoomCount;
    private int minPricePPPD;
    private int maxPricePPPD;
    private String sortBy; // use the attribute from the DB to sort on, e.g. "houseId"
    private String sortOrder; // ASC or DESC
    private int limit; // used to determine how many results you want per page
    private int offset; // if you go to page 2 and result limit is 10, offset will become 10 (to hide page 1 results)
    private boolean count; // returns the amount of records found

    private HouseFilter(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.provinces = builder.provinces;
        this.cities = builder.cities;
        this.houseTypeIds = builder.houseTypeIds;
        this.houseOwnerId = builder.houseOwnerId;
        this.amountOfGuests = builder.amountOfGuests;
        this.desiredRoomCount = builder.desiredRoomCount;
        this.minPricePPPD = builder.minPricePPPD;
        this.maxPricePPPD = builder.maxPricePPPD;
        this.sortBy = builder.sortBy;
        this.sortOrder = builder.sortOrder;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.count = builder.count;
    }

    // getters

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<String> getProvinces() {
        return provinces;
    }

    public List<String> getCities() {
        return cities;
    }

    public List<Integer> getHouseTypeIds() {
        return houseTypeIds;
    }

    public int getHouseOwnerId() {
        return houseOwnerId;
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

    public boolean getCount() {
        return count;
    }

    public static class Builder {

        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> provinces;
        private List<String> cities;
        private List<Integer> houseTypeIds;
        private int houseOwnerId;
        private int amountOfGuests;
        private int desiredRoomCount;
        private int minPricePPPD;
        private int maxPricePPPD;
        private String sortBy;
        private String sortOrder;
        private int limit;
        private int offset;
        private boolean count;

        public Builder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setProvinces(List<String> provinces) {
            this.provinces = provinces;
            return this;
        }

        public Builder setCities(List<String> cities) {
            this.cities = cities;
            return this;
        }

        public Builder setHouseTypeIds(List<Integer> houseTypeIds) {
            this.houseTypeIds = houseTypeIds;
            return this;
        }

        public Builder setHouseOwner(int houseOwnerId) {
            this.houseOwnerId = houseOwnerId;
            return this;
        }

        public Builder setAmountOfGuests(int amountOfGuests) {
            if (amountOfGuests > 0) {
                this.amountOfGuests = amountOfGuests;
            }
            return this;
        }

        public Builder setDesiredRoomCount(int desiredRoomCount) {
            if (desiredRoomCount > 0) {
                this.desiredRoomCount = desiredRoomCount;
            }
            return this;
        }

        public Builder setMinPricePPPD(int minPricePPPD) {
            if (minPricePPPD > 0) {
                this.minPricePPPD = minPricePPPD;
            }
            return this;
        }

        public Builder setMaxPricePPPD(int maxPricePPPD) {
            if (maxPricePPPD > 0) {
                this.maxPricePPPD = maxPricePPPD;
            }
            return this;
        }

        public Builder setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder setSortOrder(String sortOrder) {
            if (sortOrder.equals("ASC") || sortOrder.equals("DESC")) {
                this.sortOrder = sortOrder;
            }
            return this;
        }

        public Builder setLimit(int limit) {
            if (limit > 0) {
                this.limit = limit;
            }
            return this;
        }

        public Builder setOffset(int offset) {
            if (offset > 0) {
                this.offset = offset;
            }
            return this;
        }

        public Builder setCount(boolean count) {
            this.count = count;
            return this;
        }

        public HouseFilter build() {
            return new HouseFilter(this);
        }

    } // builder class

} // housefilter class
