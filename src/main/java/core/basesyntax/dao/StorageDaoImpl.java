package core.basesyntax.dao;

import core.basesyntax.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageDaoImpl implements StorageDao {
    private final List<User> people = new ArrayList<>();
    private Long index = 0L;

    @Override
    public User add(User user) {
        user.setId(++index);
        people.add(user);
        return user;
    }

    @Override
    public User get(String login) {
        for (User user : people) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    // helper methods used in tests (not part of interface)
    public void clear() {
        people.clear();
        index = 0L;
    }

    public List<User> getAll() {
        return Collections.unmodifiableList(people);
    }

    public int size() {
        return people.size();
    }
}
