package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class GameTest {

    @Test
    public void testGetSuffragiumLimit() {
        Game game = new Game();
        game.setNumPlayers(8);
        assertEquals(game.getSuffragiumLimit(), 20);
    }

    @Test 
    public void testGetDuration() {
        Game game = new Game();
        game.setStartDate(Date.from(Instant.parse("2023-01-11T18:00:00.00Z")));
        game.setEndDate(Date.from(Instant.parse("2023-01-11T18:25:00.00Z")));
        assertEquals(game.getDuration(), 25);

    }
    
}
