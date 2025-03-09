package service.utils;

import service.dao.UserDao;
import service.exceptions.ObjectNullException;


public class Authenticate<T> {
    private final UserDao<T> userDao;
    private final UserFactory<T> userFactory;

    public Authenticate(final UserDao<T> userDao, final UserFactory<T> userFactory) {
        this.userDao = userDao;
        this.userFactory = userFactory;
    }

    public T login(String name) throws ObjectNullException {
        T user = userDao.getUser(name);

        if (user == null) {
            throw new ObjectNullException();
        }

        return user;
    }

    public T register(String name) {
        T user = userFactory.createUser(name);
        userDao.addUser(user);

        return user;
    }
}
