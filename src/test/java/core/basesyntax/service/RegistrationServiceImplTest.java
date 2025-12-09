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
        assertNotNull(returned.getId(), "Id should be set by StorageDaoImpl");
        assertSame(user, returned, "register should return the very same object instance that was added");
        assertEquals(1, storageDao.size());
        List<User> all = storageDao.getAll();
        assertEquals(returned, all.get(0));
    }

    @Test
    public void register_nullUser_notOk() {
        RegistrationException e = assertThrows(RegistrationException.class,
                () -> registrationService.register(null));
        assertEquals("User cannot be null", e.getMessage());
    }

    @Test
    public void register_nullLogin_notOk() {
        User u = user(null, "password", 20);

        RegistrationException e = assertThrows(RegistrationException.class,
                () -> registrationService.register(u));
        assertEquals("Login cannot be null", e.getMessage());
    }

    @Test
    public void register_shortLogin_notOk_and_edge_ok() {
        User u0 = user("", "password", 20);
        User u3 = user("abc", "password", 20);
        User u5 = user("abcde", "password", 20);

        assertThrows(RegistrationException.class, () -> registrationService.register(u0));
        assertThrows(RegistrationException.class, () -> registrationService.register(u3));
        assertThrows(RegistrationException.class, () -> registrationService.register(u5));

        User u6 = user("abcdef", "password", 20);
        User returned = registrationService.register(u6);
        assertNotNull(returned.getId());
    }

    @Test
    public void register_nullPassword_notOk() {
        User u = user("validLogin", null, 20);

        RegistrationException e = assertThrows(RegistrationException.class,
                () -> registrationService.register(u));
        assertEquals("Password cannot be null", e.getMessage());
    }

    @Test
    public void short_password_notOk_and_edge_ok() {
        User p0 = user("login01", "", 20);
        User p3 = user("login02", "abc", 20);
        User p5 = user("login03", "abcde", 20);

        assertThrows(RegistrationException.class, () -> registrationService.register(p0));
        assertThrows(RegistrationException.class, () -> registrationService.register(p3));
        assertThrows(RegistrationException.class, () -> registrationService.register(p5));

        User ok = user("login04", "abcdef", 20);
        User returned = registrationService.register(ok);
        assertNotNull(returned.getId());
    }

    @Test
    public void register_nullAge_notOk() {
        User u = user("validLogin", "password", null);

        RegistrationException e = assertThrows(RegistrationException.class,
                () -> registrationService.register(u));
        assertEquals("Age cannot be null", e.getMessage());
    }

    @Test
    public void register_underage_notOk() {
        User u17 = user("user17", "password", 17);

        RegistrationException e1 = assertThrows(RegistrationException.class,
                () -> registrationService.register(u17));
        assertEquals("Not valid age: 17. Minimum allowed age is 18.", e1.getMessage());

        User negative = user("negUser", "password", -1);
        RegistrationException e2 = assertThrows(RegistrationException.class,
                () -> registrationService.register(negative));
        assertEquals("Not valid age: -1. Minimum allowed age is 18.", e2.getMessage());
    }

    @Test
    public void register_ageBoundary18_ok() {
        User u = user("age18ok", "password", 18);

        User returned = registrationService.register(u);
        assertNotNull(returned.getId());
        assertEquals(1, storageDao.size());
    }

    @Test
    public void register_existingLogin_notOk() {
        User existing = user("duplicate", "pwd12345", 30);
        storageDao.add(existing);

        User newUser = user("duplicate", "anotherpwd", 25);

        RegistrationException e = assertThrows(RegistrationException.class,
                () -> registrationService.register(newUser));
        assertEquals("User with login 'duplicate' is already registered.", e.getMessage());
    }
}
