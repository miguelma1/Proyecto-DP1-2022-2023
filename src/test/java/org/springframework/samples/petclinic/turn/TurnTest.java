package org.springframework.samples.petclinic.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TurnTest {

    @Test
    public void testGetVoteCount() {
        Turn turn = new Turn();
        turn.setVotesLoyal(1);
        turn.setVotesNeutral(1);
        assertEquals(turn.getVoteCount(), 2);
    }

    
}
