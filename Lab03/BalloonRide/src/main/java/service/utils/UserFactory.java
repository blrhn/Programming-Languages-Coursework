package service.utils;

public interface UserFactory<T> {
    T createUser(String name);
}
