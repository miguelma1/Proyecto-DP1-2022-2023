package org.springframework.samples.petclinic.progress;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;

@DataJpaTest
public class ProgressRepositoryTest {

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testFindProgressByPlayer() {
        Player player = playerRepository.findPlayerByUsername("alvgonfri");
        List<Progress> progress= progressRepository.findProgressByPlayer(player);
        assertNotNull(progress);
    }
    
}
