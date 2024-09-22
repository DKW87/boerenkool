package boerenkool.communication.dto;

import boerenkool.business.model.ExtraFeature;
import java.util.List;

public class HouseExtraFeatureDTO {
    private int houseId;
    private int extraFeatureId;

    public HouseExtraFeatureDTO() {}

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public int getExtraFeatureId() {
        return extraFeatureId;
    }

    public void setExtraFeatureId(int extraFeatureId) {
        this.extraFeatureId = extraFeatureId;
    }
}

