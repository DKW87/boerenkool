package boerenkool.business.model;

/**
 * @author Emine Sernur YILDIRIM
 */
public class ExtraFeature {

    // attributes
    private int extraFeatureId;
    private String extraFeatureName;

    // constructors
    public ExtraFeature(int extraFeatureId, String extraFeatureName) {
        this.extraFeatureId = extraFeatureId;
        this.extraFeatureName = extraFeatureName;
    }

    public ExtraFeature(String extraFeatureName) {
        this.extraFeatureName = extraFeatureName;
    }

    // getters and setters
    public int getExtraFeatureId() {
        return extraFeatureId;
    }

    public void setExtraFeatureId(int extraFeatureId) {
        this.extraFeatureId = extraFeatureId;
    }

    public String getExtraFeatureName() {
        return extraFeatureName;
    }

    public void setExtraFeatureName(String extraFeatureName) {
        this.extraFeatureName = extraFeatureName;
    }

    @Override
    public String toString() {
        return this.extraFeatureName;
    }
}
