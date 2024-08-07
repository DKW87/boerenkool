package boerenkool.business.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

    private final Logger logger = LoggerFactory.getLogger(User.class);
    private int userId;
    private String typeOfUser;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String infix;
    private String lastName;
    private int coinBalance;

    private final static int DEFAULT_COIN_BALANCE = 0;
    private final static int DEFAULT_USER_ID = 0;

    public User(int userId, String typeOfUser, String username, String password, String email, String phone, String firstName, String infix, String lastName, int coinBalance) {
        this.userId = userId;
        this.typeOfUser = typeOfUser;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.coinBalance = DEFAULT_COIN_BALANCE;
        logger.info("New user");
    }

    //user object zonder id
    public User(String typeOfUser, String username, String password, String email, String phone, String firstName, String infix, String lastName, int coinBalance) {
        this(DEFAULT_USER_ID, typeOfUser, username, password, email, phone, firstName, infix, lastName, coinBalance);
    }

    public User () {
        logger.info("User created with no-arg constructor");
    }

    public Logger getLogger() {
        return logger;
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

    @Override
    public String toString() {
        return "User{" +
                "typeOfUser='" + typeOfUser + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", firstName='" + firstName + '\'' +
                ", infix='" + infix + '\'' +
                ", lastName='" + lastName + '\'' +
                ", coinBalance=" + coinBalance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return (!username.equals(user.username));
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
