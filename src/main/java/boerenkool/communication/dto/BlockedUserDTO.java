package boerenkool.communication.dto;

public class BlockedUserDTO {
    private String username;

    public BlockedUserDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
