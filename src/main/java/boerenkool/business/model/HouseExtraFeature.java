package boerenkool.business.model;

/**
 * @author Emine Sernur YILDIRIM

 */
public class HouseExtraFeature {

    // attributes
    private int houseId;
    private int featureId;
    private String extraFeatureName;

    // constructors
    public HouseExtraFeature(int houseId, int featureId) {
        this.houseId = houseId;
        this.featureId = featureId;
    }

    // getters and setters

    public String getExtraFeatureName() {
        return extraFeatureName;
    }

    public void setExtraFeatureName(String extraFeatureName) {
        this.extraFeatureName = extraFeatureName;
    }

    public int getHouseId() {
        return houseId;
    }



    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }
}
