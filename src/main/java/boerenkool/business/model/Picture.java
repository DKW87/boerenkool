package boerenkool.business.model;

import java.sql.SQLOutput;

/*
Student: Timothy Houweling
 */
public class Picture {

    /*
    Attributen
     */

    private static final int MAX_PICTURE_SIZE = 5 * 1024 * 1024; // 5 MB
    private int pictureId;
    private int houseId;
    private byte[] picture;
    private String description;

    /*
    Constructors
     */

    public Picture(int houseId, byte[] picture, String description) {
        this.houseId = houseId;
        this.picture = picture;
        this.description = description;
    }

    public Picture(int houseId, byte[] picture) {
        this(0, picture, "");
    }

    /*
    Methodes
     */

    @Override
    public String toString() {
        return "Picture id" + getPictureId();
    }

    /*
    Getters & Setters
     */

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(House house) {
        this.houseId = houseId;
    }

    public byte[] getPicture() {
        return picture;
    }

    /**
     * Setter dictates Picture size limit.
     * @param picture
     * @throws IllegalArgumentException
     */
    public void setPicture(byte[] picture) throws IllegalArgumentException {
        if (picture.length > MAX_PICTURE_SIZE) {
            throw new IllegalArgumentException("Foto overschrijd het maximum grootte van 5MB.");
        }
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

} // einde klasse