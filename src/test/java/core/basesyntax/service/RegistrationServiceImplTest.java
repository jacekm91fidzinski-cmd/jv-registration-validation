package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


import core.basesyntax.db.Storage;
import core.basesyntax.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationServiceImplTest {
    private RegistrationServiceImpl registrationService;

    @BeforeEach
    public void setUp() {
        Storage.people.clear();;
        registrationService = new RegistrationServiceImpl();
    }

    @Test
    public void register_validUser_ok() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("strongPwd");
        user.setAge(25);

        User returned = registrationService.register(user);

        assertNotNull(returned);
        assertNotNull(returned.getId());
        assertEquals(1, Storage.people.size());
        assertEquals(returned, Storage.people.get(0));
    }

    @Test
    public void register_nullUser_notOk() {
        assertThrows(RegistrationException.class,
                () -> registrationService.register(null));
    }

    @Test
    public void register_nullLogin_notOK() {
        User user = new User();
        user.setLogin(null);
        user.setPassword("password");
        user.setAge(20);

        assertThrows(RegistrationException.class,
                () -> registrationService.register(user));
    }

    @Test
    public void register_shortLogin_notOk() {
        User user1 = new User();
        user1.setLogin("");
        user1.setPassword("password");
        user1.setAge(20);

        User user2 = new User();
        user2.setLogin("abc");
        user2.setPassword("password");
        user2.setAge(20);

        User user3 = new User();
        user3.setLogin("abcde");
        user3.setPassword("password");
        user3.setAge(20);

        assertThrows(RegistrationException.class, () -> registrationService.register(user1));
        assertThrows(RegistrationException.class, () -> registrationService.register(user2));
        assertThrows(RegistrationException.class, () -> registrationService.register(user3));

        User userOK = new User();
        userOK.setLogin("abcdef");
        userOK.setPassword("password");
        userOK.setAge(20);

        User returned = registrationService.register(userOK);
        assertNotNull(returned.getId());
    }

    @Test
    public void register_nullPassword_notOk() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword(null);
        user.setAge(20);

        assertThrows(RegistrationException.class, () -> registrationService.register(user));
    }

    @Test
    public void  short_password_notOk() {
        User p0 = new User();
        p0.setLogin("login01");
        p0.setPassword("abc");
        p0.setAge(20);

        User p3 = new User();
        p3.setLogin("login02");
        p3.setPassword("abc");
        p3.setAge(20);

        User p5 = new User();
        p5.setLogin("login03");
        p5.setPassword("abcde");
        p5.setAge(20);

        assertThrows(RegistrationException.class, () -> registrationService.register(p0));
        assertThrows(RegistrationException.class, () -> registrationService.register(p3));
        assertThrows(RegistrationException.class, () -> registrationService.register(p5));

        User ok = new User();
        ok.setLogin("login04");
        ok.setPassword("abcdef");
        ok.setAge(20);

        User returned = registrationService.register(ok);
        assertNotNull(returned.getId());
    }

    @Test
    public void register_nullAge_notOk() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("password");
        user.setAge(null);

        assertThrows(RegistrationException.class, () -> registrationService.register(user));
    }
    @Test
    public void register_underage_notOK() {
        User user17 = new User();
        user17.setLogin("user17");
        user17.setPassword("password");
        user17.setAge(17);

        assertThrows(RegistrationException.class, () -> registrationService.register(user17));

        User negative = new User();
        negative.setLogin("negUser");
        negative.setPassword("password");
        negative.setAge(-1);

        assertThrows(RegistrationException.class, () -> registrationService.register(negative));
    }

    @Test
    public void register_ageBoundary18_ok() {
        User user = new User();
        user.setLogin("age18ok");
        user.setPassword("password");
        user.setAge(18);

        User returned = registrationService.register(user);
        assertNotNull(returned.getId());
        assertEquals(1, Storage.people.size());
    }

    @Test
    public void register_existingLogin_notOk() {
        User existing = new User();
        existing.setLogin("duplicate");
        existing.setPassword("pwd12345");
        existing.setAge(30);
        existing.setId(1L);
        Storage.people.add(existing);

        User newUser = new User();
        newUser.setLogin("duplicate");
        newUser.setPassword("anotherpwd");
        newUser.setAge(25);

        assertThrows(RegistrationException.class, () -> registrationService.register(newUser));
    }
}
