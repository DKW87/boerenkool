package boerenkool.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservationRepository {

    private final Logger logger = LoggerFactory.getLogger(ReservationRepository.class);

    public ReservationRepository() {
        super();
        logger.info("ReservationRepository initialized");
    }

}
