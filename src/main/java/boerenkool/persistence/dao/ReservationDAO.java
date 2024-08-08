package boerenkool.persistence.dao;

import boerenkool.business.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationDAO extends GenericsDAO<Reservation> {

    @Override
    void storeOne (Reservation reservation);

    @Override
    List<Reservation> getAll ();

    @Override
    Optional<Reservation> getOneById (int id);

    @Override
    void updateOne (Reservation reservation);

    @Override
    void removeOneById (int id);

}
