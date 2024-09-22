package boerenkool.communication.dto;
import boerenkool.business.model.Picture;
import java.util.Base64;

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

    public String getBase64Picture() {
        return base64Picture;
    }

    public void setBase64Picture(String base64Picture) {
        this.base64Picture = base64Picture;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }



}