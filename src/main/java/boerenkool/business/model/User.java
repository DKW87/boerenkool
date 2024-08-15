package boerenkool.business.model;

import boerenkool.communication.dto.UserDto;
import boerenkool.utilities.authorization.PasswordService;

import java.util.ArrayList;
import java.util.List;

public class User {
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
        this.salt = salt;  // Set the salt passed as a parameter
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.coinBalance = coinBalance;
        this.blockedUser = blockedUser != null ? blockedUser : new ArrayList<>();
    }
//De UserDto klasse wordt gebruikt om de gegevens te representeren zoals ze van buitenaf worden ontvangen, bijvoorbeeld
// in een JSON-formaat via een HTTP-request. Wanneer deze gegevens in de backend worden ontvangen, moeten ze vaak worden
// omgezet naar een User object, wat het domeinmodel is dat de kernlogica van je applicatie bevat.
    //Constructor dto
// Constructor voor het aanmaken van een nieuwe gebruiker vanuit UserDto
public User(UserDto dto) {
    this(DEFAULT_USER_ID,
            dto.getTypeOfUser(),
            dto.getUsername(),
            null,  // Pass null for hashedPassword, which will be set later
            null,  // Pass null for salt, which will be set later
            dto.getEmail(),
            dto.getPhone(),
            dto.getFirstName(),
            dto.getInfix(),
            dto.getLastName(),
            dto.getCoinBalance(),
            new ArrayList<>() // Create an empty list for blockedUser
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
        this.hashedPassword = PasswordService.hashPassword(password, this.salt);
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
