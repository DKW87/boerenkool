package boerenkool.communication.dto;

public class PictureDTO {
    private int id;
    private int houseId;
    private String format;
    private String base64Image;

    public PictureDTO(int id, int houseId, String format, String base64Image) {
        this.id = id;
        this.houseId = houseId;
        this.format = format;
        this.base64Image = base64Image;
    }

    // Getters and setters
    public int getpictureId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHouseId() { return houseId; }
    public void setHouseId(int houseId) { this.houseId = houseId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }

}