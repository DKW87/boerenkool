package boerenkool.persistence.dao;

import boerenkool.business.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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


public class JdbcReservationDAO {

    private final Logger logger = LoggerFactory.getLogger(JdbcReservationDAO.class);
    private JdbcTemplate jdbcTemplate;
    private final UserDAO userDAO;
    private final HouseDAO houseDAO;

    @Autowired
    public JdbcReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("ReservationDAO instantiated");
        userDAO = new UserDAO(jdbcTemplate);
        houseDAO = new HouseDAO(jdbcTemplate);
    }

    public List<Reservation> getAll() {
        String sql = "select * from Reservation;";
        return jdbcTemplate.query(sql, new ReservationMapper());
    }

    public Optional<Reservation> getOneById(int id) {
        String sql = "select * from Reservation where reservationId = ?;";
        List<Reservation> reservations = jdbcTemplate.query(sql, new ReservationMapper(), id);
        if (reservations.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(reservations.getFirst());
        }
    }

    public void storeOne(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> insertReservationStatement(reservation, connection), keyHolder);
        int newKey = keyHolder.getKey().intValue();
        reservation.setReservationId(newKey);
    }

    public void updateOne(Reservation reservation) {
        jdbcTemplate.update(connection -> updateReservationStatement(reservation, connection));
    }

    public void removeOneById(int id) {
        String sql = "delete from Reservation where reservationId = ?;";
        jdbcTemplate.update(sql, id);
    }

    private PreparedStatement insertReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "INSERT INTO Reservation (reservationId, reservedByUserId, houseId, startDate, endDate, guestCount) VALUES (?, ?, ?, ?, ?, ?);";
        preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, reservation.getReservationId());
        preparedStatement.setInt(2, reservation.getReservedByUser().getUserId());
        preparedStatement.setInt(3, reservation.getHouse().getHouseId());
        preparedStatement.setDate(4, java.sql.Date.valueOf(reservation.getStartDate()));
        preparedStatement.setDate(5, java.sql.Date.valueOf(reservation.getEndDate()));
        preparedStatement.setInt(6, reservation.getGuestCount());
        return preparedStatement;
    }

    private PreparedStatement updateReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "UPDATE Reservation SET reservationId=?, reservedByUserId=?, houseId=?, startDate=?, endDate=?, guestCount=? WHERE reservationId = ?";
        preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, reservation.getReservationId());
        preparedStatement.setInt(2, reservation.getReservedByUser().getUserId());
        preparedStatement.setInt(3, reservation.getHouse().getHouseId());
        preparedStatement.setDate(4, java.sql.Date.valueOf(reservation.getStartDate()));
        preparedStatement.setDate(5, java.sql.Date.valueOf(reservation.getEndDate()));
        preparedStatement.setInt(6, reservation.getGuestCount());
        return preparedStatement;
    }

    private static class ReservationMapper implements RowMapper<Reservation> {

        @Override
        public Reservation mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            int reservationId= resultSet.getInt("reservationId");
            User reservedByUser= resultSet.getInt("reservedByUserId");
            House houseId = resultSet.getInt("houseId");
            LocalDate startDate = resultSet.getDate("startDate").toLocalDate();
            LocalDate endDate= resultSet.getDate("endDate").toLocalDate();
            int guestCount = resultSet.getInt("guestCount");

            return new Reservation(reservationId, reservedByUser, houseId, startDate, endDate, guestCount);
        }
    }


}
