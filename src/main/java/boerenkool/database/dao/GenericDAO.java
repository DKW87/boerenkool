package boerenkool.database.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T> {
    List<T> getAll();//Read
    Optional<T> getOneById(int id);//Read
    void storeOne(T type);//Create
    boolean updateOne(T type);//Update
    boolean removeOneById(int id);//Delete
}