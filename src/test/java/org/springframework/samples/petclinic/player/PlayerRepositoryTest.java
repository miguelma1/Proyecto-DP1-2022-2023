package org.springframework.samples.petclinic.player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testFindAllPageable() {
        List<Player> players = playerRepository.findAllPageable(PageRequest.of(1, 5));
        assertNotNull(players);
        assertFalse(players.isEmpty());
    }

    @Test
    public void testFindPlayerByUsername() {
        Player player = playerRepository.findPlayerByUsername("player1");
        assertNotNull(player);
    }

    @Test
    public void testFindPlayerByUsernameNotExistingPlayer() {
        Player player = playerRepository.findPlayerByUsername("player999");
        assertNull(player);
    }
    
    @Test
    public void testFindAllUsernames() {
        List<String> usernames = playerRepository.findAllUsernames();
        assertNotNull(usernames);
        assertFalse(usernames.isEmpty());
    }
}
