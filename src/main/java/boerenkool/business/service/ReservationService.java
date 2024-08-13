//package boerenkool.business.service;
//
//import boerenkool.business.model.Reservation;
//import boerenkool.database.repository.ReservationRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ReservationService {
//
//    private final ReservationRepository reservationRepository;
//
//    @Autowired
//    public ReservationService(ReservationRepository reservationRepository) {
//        this.reservationRepository = reservationRepository;
//    }
//
//    public List<Reservation> getAllReservations() {
//        return reservationRepository.getAllReservations();
//    }
//
//    public Optional<Reservation> getReservationById(int id) {
//        return reservationRepository.getReservationById(id);
//    }
//
//    public Reservation saveReservation(Reservation reservation) {
//        validateReservation(reservation);
//        return reservationRepository.saveReservation(reservation);
//    }
//
//    public boolean deleteReservationById(int id) {
//        return reservationRepository.deleteReservationById(id);
//    }
//
//    private void validateReservation(Reservation reservation) {
//        if (reservation.getGuestCount() < 0) {
//            throw new IllegalArgumentException("Guest count cannot be negative.");
//        }
//
//        LocalDate startDate = reservation.getStartDate();
//        LocalDate endDate = reservation.getEndDate();
//
//        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
//            throw new IllegalArgumentException("End date cannot be before start date.");
//        }
//    }
//}
//
