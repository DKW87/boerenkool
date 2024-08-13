package boerenkool;

import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.JdbcUserDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Optional;

public class LauncherLeo {

    public static void main(String[] args) {
        // Step 1: Set up the DataSource for MySQL
        DataSource dataSource = createDataSource();

        // Step 2: Create a JdbcTemplate using the DataSource
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);


        // Step 3: Create an instance of JdbcUserDAO with the JdbcTemplate
        JdbcUserDAO userDAO = new JdbcUserDAO(jdbcTemplate);

        // Step 4: Create a new User object
        User newUser = new User("huurder", "testuser", "password", "testuser@example.com", "0612345678", "Test", null, "User", 100);

        // Step 5: Store the User in the database
        userDAO.storeOne(newUser);
        System.out.println("User saved with ID: " + newUser.getUserId());

        // Step 6: Retrieve the User by ID
        Optional<User> retrievedUser = userDAO.getOneById(newUser.getUserId());
        retrievedUser.ifPresent(user -> System.out.println("Retrieved User: " + user.getUsername()));

        // Step 7: Update the User's details
        newUser.setUsername("updateduser");
        userDAO.updateOne(newUser);
        System.out.println("User updated with new username: " + newUser.getUsername());

        // Step 8: Retrieve the updated User
        Optional<User> updatedUser = userDAO.getOneById(newUser.getUserId());
        updatedUser.ifPresent(user -> System.out.println("Updated User: " + user.getUsername()));

        // Step 9: Delete the User by ID
        boolean deleted = userDAO.removeOneById(newUser.getUserId());
        System.out.println("User deleted: " + deleted);

        // Step 10: Try to retrieve the User again after deletion
        Optional<User> deletedUser = userDAO.getOneById(newUser.getUserId());
        System.out.println("User exists after deletion: " + deletedUser.isPresent());
    }

    private static DataSource createDataSource() {
        // Configure the DataSource for MySQL
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");  // Ensure you have the MySQL Connector/J driver
        dataSource.setUrl("jdbc:mysql://oege.ie.hva.nl:3306/zkwantd1?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dataSource.setUsername("kwantd1");  // Replace with your MySQL username
        dataSource.setPassword("Izl6yos8LRH2Ly");  // Replace with your MySQL password
        return dataSource;
    }
}
