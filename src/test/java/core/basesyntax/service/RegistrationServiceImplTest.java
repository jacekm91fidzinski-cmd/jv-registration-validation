package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationServiceImplTest {
    private StorageDaoImpl storageDao;
    private RegistrationServiceImpl registrationService;

    private static User user(String login, String password, Integer age) {
        User u = new User();
        u.setLogin(login);
        u.setPassword(password);
        u.setAge(age);
        return u;
    }

    @BeforeEach
    public void setUp() {
        storageDao = new StorageDaoImpl();
        storageDao.clear();
        registrationService = new RegistrationServiceImpl(storageDao);
    }

    @Test
    public void register_validUser_ok() {
        User user = user("validLogin", "strongPwd", 25);

        User returned = registrationService.register(user);

        assertNotNull(returned);
        assertNotNull(returned.getId(),
                "Id should be set by StorageDaoImpl");

        assertSame(
                user,
                returned,
                "register should return same object instance that was added"
        );

        assertEquals(1, storageDao.size());

        List<User> all = storageDao.getAll();
        assertEquals(returned, all.get(0));
    }
}
