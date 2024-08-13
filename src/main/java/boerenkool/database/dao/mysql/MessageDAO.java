package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.database.dao.GenericDAO;

import java.util.Optional;

import java.util.List;

public interface MessageDAO {
    // extends GenericDAO<Message> // heb ik even uitgezet vanwege DTO experiment
//    @Override
    void storeOne(Message message);

//    @Override
    List<MessageDTO> getAll();

    List<MessageDTO> getAllForReceiver(User receiver);

//    @Override
    Optional<MessageDTO> getOneById(int id);

//    @Override
    boolean updateOne(Message message);

//    @Override
    boolean removeOneById(int id);
}


