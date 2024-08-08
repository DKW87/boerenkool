package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.database.dao.GenericDAO;
import java.util.Optional;

import java.util.List;

public interface MessageDAO extends GenericDAO<Message> {
    @Override
    List<Message> getAll();

    List<Message> getAllForRecipient(User recipient);

    @Override
    Optional<Message> getOneById(int id);

    @Override
    void storeOne(Message message);

    @Override
    boolean updateOne(Message message);

    @Override
    boolean removeOneById(int id);
}
