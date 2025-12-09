package core.basesyntax.service;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.model.User;

public class RegistrationServiceImpl implements RegistrationService {
    private static final int MIN_LOGIN_LENGTH = 6;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_AGE = 18;

    private final StorageDao storageDao;

    public RegistrationServiceImpl() {
        this(new StorageDaoImpl());
    }

    public RegistrationServiceImpl(StorageDao storageDao) {
        this.storageDao = storageDao;
    }

    @Override
    public User register(User user) {
        if (user == null) {
            throw new RegistrationException("User cannot be null");
        }

        validateLogin(user.getLogin());
        validatePassword(user.getPassword());
        validateAge(user.getAge());
        ensureLoginUnique(user.getLogin());

        return storageDao.add(user);
    }

    private void validateLogin(String login) {
        if (login == null) {
            throw new RegistrationException("Login cannot be null");
        }
        if (login.length() < MIN_LOGIN_LENGTH) {
            throw new RegistrationException(
                    "Login must be at least " + MIN_LOGIN_LENGTH
                            + " characters long. Provided login: '" + login + "'."
            );
        }
    }

    private void validatePassword(String password) {
        if (password == null) {
            throw new RegistrationException("Password cannot be null");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new RegistrationException(
                    "Password must be at least " + MIN_PASSWORD_LENGTH
                            + " characters long. Provided length: "
                            + password.length() + "."
            );
        }
    }

    private void validateAge(Integer age) {
        if (age == null) {
            throw new RegistrationException("Age cannot be null");
        }
        if (age < MIN_AGE) {
            throw new RegistrationException(
                    "Not valid age: " + age + ". Minimum allowed age is "
                            + MIN_AGE + "."
            );
        }
    }

    private void ensureLoginUnique(String login) {
        if (storageDao.get(login) != null) {
            throw new RegistrationException(
                    "User with login '" + login + "' is already registered."
            );
        }
    }
}
