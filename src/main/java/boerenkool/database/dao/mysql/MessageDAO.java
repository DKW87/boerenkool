package boerenkool.database.dao.mysql;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.database.dao.GenericDAO;

import java.util.Optional;

import java.util.List;

/**
 * @author Bart Notelaers
 */
public interface MessageDAO extends GenericDAO<Message> {
    @Override
    boolean storeOne(Message message);

    @Override
    List<Message> getAll();

    List<Message> getAllByUserId(int senderId);
    List<Message> getAllFromSenderId(int senderId);
    List<Message> getAllToReceiverId(int receiverId);

    @Override
    Optional<Message> getOneById(int messageId);

    int numberOfUnreadMessages(int receiverId);

    @Override
    boolean updateOne(Message message);

    boolean setReadByReceiver(Message message);

    @Override
    boolean removeOneById(int id);
}


