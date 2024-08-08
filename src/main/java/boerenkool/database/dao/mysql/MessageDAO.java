package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;

import java.util.Optional;

import java.util.List;

public interface MessageDAO extends GenericDAO<Message> {
    @Override
    void storeOne(Message message);

    @Override
    List<Message> getAll();

    List<Message> getAllForReceiver(User receiver);

    @Override
    Optional<Message> getOneById(int id);

    @Override
    boolean updateOne(Message message);

    @Override
    boolean removeOneById(int id);
}


