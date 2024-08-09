package boerenkool.database.dao.mysql;

import boerenkool.database.dao.GenericDAO;

import java.util.List;
import java.util.Optional;

public class DAODummy implements GenericDAO<DAODummy> {
    @Override
    public List<DAODummy> getAll() {
        return List.of();
    }

    @Override
    public Optional<DAODummy> getOneById(int id) {
        return null;
    }

    @Override
    public void storeOne(DAODummy type) {
    }

    @Override
    public boolean updateOne(DAODummy type) {
        return false;
    }

    @Override
    public boolean removeOneById(int id) {
        return false;
    }
}
