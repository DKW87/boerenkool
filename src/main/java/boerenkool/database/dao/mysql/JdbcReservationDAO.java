package boerenkool.database.dao.mysql;

import boerenkool.business.model.Reservation;
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

    @Autowired
    public JdbcReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("New JdbcReservationDAO instance created.");
    }

    @Override
    public List<Reservation> getAll() {
        String sql = "select * from Reservation";
        return jdbcTemplate.query(sql, new ReservationMapper());
    }

    @Override
    public Optional<Reservation> getOneById(int id) {
        String sql = "select * from Reservation where reservationId = ?";
        List<Reservation> reservations = jdbcTemplate.query(sql, new ReservationMapper(), id);
        return reservations.stream().findFirst();
    }

    @Override
    public boolean storeOne(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> insertReservationStatement(reservation, connection), keyHolder);

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            int newKey = keyHolder.getKey().intValue();
            reservation.setReservationId(newKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOne(Reservation reservation) {
        int rowsAffected = jdbcTemplate.update(connection -> updateReservationStatement(reservation, connection));
        return rowsAffected > 0;
    }

    @Override
    public boolean removeOneById(int id) {
        String sql = "delete from Reservation where reservationId = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    private void extracted(Reservation reservation, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, reservation.getReservedByUser().getUserId());
        preparedStatement.setInt(2, reservation.getHouse().getHouseId());
        preparedStatement.setDate(3, java.sql.Date.valueOf(reservation.getStartDate()));
        preparedStatement.setDate(4, java.sql.Date.valueOf(reservation.getEndDate()));
        preparedStatement.setInt(5, reservation.getGuestCount());
    }

    private PreparedStatement insertReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        String sql = "INSERT INTO Reservation (reservedByUserId, houseId, startDate, endDate, guestCount) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        extracted(reservation, preparedStatement);
        return preparedStatement;
    }

    private PreparedStatement updateReservationStatement(Reservation reservation, Connection connection) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "UPDATE Reservation SET reservedByUserId=?, houseId=?, startDate=?, endDate=?, guestCount=? WHERE reservationId = ?";
        preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        extracted(reservation, preparedStatement);
        preparedStatement.setInt(6, reservation.getReservationId());
        return preparedStatement;
    }

    private static class ReservationMapper implements RowMapper<Reservation> {

        @Override
        public Reservation mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            int reservationId = resultSet.getInt("reservationId");
            int reservedByUserId = resultSet.getInt("reservedByUserId");
            int houseId = resultSet.getInt("houseId");
            LocalDate startDate = resultSet.getDate("startDate").toLocalDate();
            LocalDate endDate = resultSet.getDate("endDate").toLocalDate();
            int guestCount = resultSet.getInt("guestCount");

            Reservation reservation = new Reservation();
            reservation.setReservationId(reservationId);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setGuestCount(guestCount);
            reservation.getHouse().setHouseId(houseId);
            reservation.getReservedByUser().setUserId(reservedByUserId);

            return reservation;
        }
    }
}



