package boerenkool.database.dao;

import java.util.List;

public interface GenericDAO<T> {
    List<T> getAll();//Read
    T getOne(int id);//Read
    void storeOne(T type);//Create
    boolean updateOne(T type);//Update
    boolean removeOne(int id);//Delete
}