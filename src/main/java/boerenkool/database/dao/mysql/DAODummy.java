package boerenkool.database.dao.mysql;

import boerenkool.database.dao.GenericDAO;

import java.util.List;

public class DAODummy implements GenericDAO<DAODummy> {
    @Override
    public List<DAODummy> getAll() {
        return List.of();
    }

    @Override
    public DAODummy getOne(int id) {
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
    public boolean removeOne(int id) {
        return false;
    }
}
