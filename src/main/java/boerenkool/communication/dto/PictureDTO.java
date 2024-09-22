package boerenkool.communication.dto;

/**
 * @author Timothy Houweling
 * @project Boerenkool
 */

public class PictureDTO {

    private Integer pictureId;
    private Integer houseId;
    private String base64Picture;
    private String mimeType;
    private String description;



    public PictureDTO(Integer pictureId, Integer houseId, String base64Picture, String mimeType, String description) {
        this.pictureId = pictureId;
        this.houseId = houseId;
        this.base64Picture = base64Picture;
        this.mimeType = mimeType;
        this.description = description;
    }

    public PictureDTO(Integer houseId, String base64Picture, String mimeType, String description) {
        this.houseId = houseId;
        this.base64Picture = base64Picture;
        this.mimeType = mimeType;
        this.description = description;
    }

    public PictureDTO() {}

    public PictureDTO(String description) {
        this.description = description;
    }

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}