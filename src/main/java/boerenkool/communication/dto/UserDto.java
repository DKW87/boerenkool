package boerenkool.communication.dto;

import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDto {

    private String typeOfUser;
    private String username;
    private String password;  // Plain text password for registration/login
    private String email;
    private String phone;
    private String firstName;
    private String infix;
    private String lastName;
    private int coinBalance;
    private final static int DEFAULT_COINBALANCE = 500;  // Default coin balance set to 500

    private final Logger logger = LoggerFactory.getLogger(UserDto.class);

    // No-arg constructor for JSON deserialization
    public UserDto() {
        super();
        this.coinBalance = DEFAULT_COINBALANCE;
        logger.info("New UserDto using no-arg constructor");
    }

    // Constructor that initializes UserDto from User entity
    public UserDto(User user) {
        this(user, false);
    }

    // Constructor that initializes UserDto from User entity, with optional password inclusion
    public UserDto(User user, boolean includePassword) {
        super();
        this.typeOfUser = user.getTypeOfUser();
        this.username = user.getUsername();
        // If includePassword is true, set the password field to the actual hashed password from the User entity.
// Otherwise, set it to a masked placeholder ("**********") to hide the password.
        this.password = includePassword ? user.getHashedPassword() : "**********";
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.infix = user.getInfix();
        this.lastName = user.getLastName();
        this.coinBalance = user.getCoinBalance();
        logger.info("New UserDto using all-arg constructor.");
        logger.info("include password == " + includePassword);
    }


    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInfix() {
        return infix;
    }

    public void setInfix(String infix) {
        this.infix = infix;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(int coinBalance) {
        this.coinBalance = coinBalance;
    }

}
