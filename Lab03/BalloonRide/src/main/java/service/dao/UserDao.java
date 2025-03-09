package service.dao;

public interface UserDao<T> {
    void addUser(T user);
    T getUser(String name);
}
