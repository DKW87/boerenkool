package boerenkool.database.repository;

import boerenkool.business.model.HouseType;
import boerenkool.database.dao.mysql.HouseTypeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HouseTypeRepository {

    private final Logger logger = LoggerFactory.getLogger(HouseTypeRepository.class);

    private final HouseTypeDAO houseTypeDAO;

    public HouseTypeRepository(HouseTypeDAO houseTypeDAO) {
        this.houseTypeDAO = houseTypeDAO;
        logger.info("New HouseTypeRepository");
    }

    public void storeOne(HouseType houseType) {
        houseTypeDAO.storeOne(houseType);
    }

    public void removeOneById(int id) {
        houseTypeDAO.removeOneById(id);
    }

    public List<HouseType> getAll() {
        return houseTypeDAO.getAll();
    }

    public Optional<HouseType> getOneById(int id) {
        return houseTypeDAO.getOneById(id);
    }

    public boolean updateOne(HouseType houseType) {
        return houseTypeDAO.updateOne(houseType);
    }

    public Optional<HouseType> findByName(String houseTypeName) {
        return houseTypeDAO.findByName(houseTypeName);
    }
}
