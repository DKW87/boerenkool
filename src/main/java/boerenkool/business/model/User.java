package boerenkool.business.model;

import boerenkool.business.service.RegistrationService;
import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private int userId;
    private String typeOfUser;
    private String username;
    private String hashedPassword;
    private String salt;
    private String email;
    private String phone;
    private String firstName;
    private String infix;
    private String lastName;
    private int coinBalance;
    private List<User> blockedUser;

    private final static int DEFAULT_COIN_BALANCE = 500;
    private final static int DEFAULT_USER_ID = 0;
    private final static String DEFAULT_USER = "Huurder";

    // Basisconstructor met alle parameters
    public User(int userId, String typeOfUser, String username, String hashedPassword, String salt, String email, String phone,
                String firstName, String infix, String lastName, int coinBalance, List<User> blockedUser) {
        this.userId = userId;
        this.typeOfUser = typeOfUser != null ? typeOfUser : DEFAULT_USER;
        this.username = username;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.coinBalance = coinBalance;
        this.blockedUser = blockedUser != null ? blockedUser : new ArrayList<>();
    }

    public User(UserDto dto, String hashedPassword, String salt) {
        this(DEFAULT_USER_ID,
                dto.getTypeOfUser(),
                dto.getUsername(),
                hashedPassword,
                salt,
                dto.getEmail(),
                dto.getPhone(),
                dto.getFirstName(),
                dto.getInfix(),
                dto.getLastName(),
                dto.getCoinBalance(),
                new ArrayList<>()
        );
    }

    // Constructor zonder geblokkeerde gebruikers
    public User(int userId, String typeOfUser, String username, String hashedPassword, String salt, String email, String phone,
                String firstName, String infix, String lastName, int coinBalance) {
        this(userId, typeOfUser, username, hashedPassword, salt, email, phone, firstName, infix, lastName, coinBalance, new ArrayList<>());
    }


    // Constructor voor nieuwe gebruikers zonder ID
    public User(String typeOfUser, String username, String hashedPassword, String salt, String email, String phone,
                String firstName, String infix, String lastName, int coinBalance) {
        this(DEFAULT_USER_ID, typeOfUser, username, hashedPassword, salt, email, phone, firstName, infix, lastName, coinBalance);
    }


    // Default constructor
    public User() {
        this(DEFAULT_USER_ID, "", "", "", "", "", "", "", "", "", DEFAULT_COIN_BALANCE, new ArrayList<>());
    }




    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

    public String getSalt() {
        return salt;
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

    public List<User> getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(List<User> blockedUser) {
        this.blockedUser = blockedUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", typeOfUser='" + typeOfUser + '\'' +
                ", username='" + username + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", firstName='" + firstName + '\'' +
                ", infix='" + infix + '\'' +
                ", lastName='" + lastName + '\'' +
                ", coinBalance=" + coinBalance +
                ", blockedUser=" + blockedUser +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return username.equals(user.username);
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
