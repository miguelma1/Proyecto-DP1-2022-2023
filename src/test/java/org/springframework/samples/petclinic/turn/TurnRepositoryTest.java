package org.springframework.samples.petclinic.turn;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.game.Game;

@DataJpaTest
public class TurnRepositoryTest {

    @Autowired
    private TurnRepository turnRepository;

    @Test
    public void testFindGameByTurn() {
        Turn turn = turnRepository.findById(2).get();
        Game game = turnRepository.findGameByTurn(turn);
        assertNotNull(game);
    }
    
}
