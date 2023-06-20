package org.springframework.samples.petclinic.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindUsernames() {
        List<String> usernames = userRepository.findUsernames();
        assertNotNull(usernames);
        assertFalse(usernames.isEmpty());
    }

    @Test
    public void testFindUserWithAuthority() {
        List<User> users = userRepository.findUserWithAuthority("player");
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
    
}
