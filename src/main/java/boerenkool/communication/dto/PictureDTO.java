package boerenkool.communication.dto;


public class PictureDTO {


    private Integer houseId;
    private String base64Picture;
    private String description;

    public PictureDTO() {}

    public PictureDTO(Integer houseId, String base64Picture, String description) {
        this.houseId = houseId;
        this.base64Picture = base64Picture;
        this.description = description;
    }

    public PictureDTO(String description) {
        this.description = description;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public String getBase64Picture() {
        return base64Picture;
    }

    public void setBase64Picture(String base64Picture) {
        this.base64Picture = base64Picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}