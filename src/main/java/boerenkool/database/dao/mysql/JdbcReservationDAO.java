package boerenkool.database.dao.mysql;

import boerenkool.business.model.House;
import boerenkool.business.model.Reservation;
import boerenkool.business.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Adnan Kilic
 * @project Boerenkool
 * @created 07/08/2024 - 20:53
 */

@Repository
public class JdbcReservationDAO implements ReservationDAO {

    private final Logger logger = LoggerFactory.getLogger(JdbcReservationDAO.class);
    private JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final HouseDAO houseDAO;

    @Autowired
    public JdbcReservationDAO(JdbcTemplate jdbcTemplate, UserDAO userDAO, HouseDAO houseDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDAO = userDAO;
        this.houseDAO = houseDAO;
        logger.info("New JdbcReservationDAO instance created.");
    }

    @Override
    public List<Reservation> getAll() {
        String sql = "select * from Reservation;";
        return jdbcTemplate.query(sql, new ReservationMapper(userDAO, houseDAO));
    }

    @Override
    public Optional<Reservation> getOneById(int id) {
        String sql = "select * from Reservation where reservationId = ?;";
        List<Reservation> reservations = jdbcTemplate.query(sql, new ReservationMapper(userDAO, houseDAO), id);
        if (reservations.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(reservations.getFirst());
        }
    }

    @Override
    public void storeOne(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertReservationStatement(reservation, connection), keyHolder);
        int newKey = keyHolder.getKey().intValue();
        reservation.setReservationId(newKey);
    }

    @Override
    public boolean updateOne(Reservation reservation) {
        jdbcTemplate.update(connection -> updateReservationStatement(reservation, connection));
        return true;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "delete from Reservation where reservationId = ?;";
        jdbcTemplate.update(sql, id);
        return true;
    }

    private PreparedStatement insertReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        String sql = "INSERT INTO Reservation (reservedByUserId, houseId, startDate, endDate, guestCount) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, reservation.getReservedByUser().getUserId());
        preparedStatement.setInt(2, reservation.getHouse().getHouseId());
        preparedStatement.setDate(3, java.sql.Date.valueOf(reservation.getStartDate()));
        preparedStatement.setDate(4, java.sql.Date.valueOf(reservation.getEndDate()));
        preparedStatement.setInt(5, reservation.getGuestCount());
        return preparedStatement;
    }

    private PreparedStatement updateReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "UPDATE Reservation SET reservedByUserId=?, houseId=?, startDate=?, endDate=?, guestCount=? WHERE reservationId = ?";
        preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, reservation.getReservedByUser().getUserId());
        preparedStatement.setInt(2, reservation.getHouse().getHouseId());
        preparedStatement.setDate(3, java.sql.Date.valueOf(reservation.getStartDate()));
        preparedStatement.setDate(4, java.sql.Date.valueOf(reservation.getEndDate()));
        preparedStatement.setInt(5, reservation.getGuestCount());
        preparedStatement.setInt(6, reservation.getReservationId());
        return preparedStatement;
    }

    private static class ReservationMapper implements RowMapper<Reservation> {

        private final UserDAO userDAO;
        private final HouseDAO houseDAO;

        public ReservationMapper(UserDAO userDAO, HouseDAO houseDAO) {
            this.userDAO = userDAO;
            this.houseDAO = houseDAO;
        }

        @Override
        public Reservation mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            int reservationId = resultSet.getInt("reservationId");
            int reservedByUserId = resultSet.getInt("reservedByUserId");
            User reservedByUser = userDAO.getOneById(reservedByUserId)
                    .orElseThrow(() -> new SQLException("User not found with ID: " + reservedByUserId));

            int houseId = resultSet.getInt("houseId");
            House house = houseDAO.getOneById(houseId)
                    .orElseThrow(() -> new SQLException("House not found with ID: " + houseId));

            LocalDate startDate = resultSet.getDate("startDate").toLocalDate();
            LocalDate endDate = resultSet.getDate("endDate").toLocalDate();
            int guestCount = resultSet.getInt("guestCount");

            return new Reservation(reservationId, house, reservedByUser, startDate, endDate, guestCount);
        }
    }
}



