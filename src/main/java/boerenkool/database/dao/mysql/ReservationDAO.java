package boerenkool.database.dao.mysql;

import boerenkool.business.model.Reservation;
import boerenkool.database.dao.GenericDAO;
import java.util.List;
import java.util.Optional;

/**
 * @author Adnan Kilic
 * @project Boerenkool
 * @created 08/08/2024 - 13:53
 */

public interface ReservationDAO extends GenericDAO<Reservation> {

    @Override
    boolean storeOne (Reservation reservation);

    @Override
    List<Reservation> getAll ();

    @Override
    Optional<Reservation> getOneById (int id);

    @Override
    boolean updateOne (Reservation reservation);

    @Override
    boolean removeOneById (int id);

    List<Reservation> getAllReservationsByLandlord(int landlordId);

    List<Reservation> getAllReservationsByTenant(int tenantId);

    List<Reservation> getAllReservationsByHouseId(int houseId);

    List<Reservation> getAllReservationsByUserId(int userId);
}
