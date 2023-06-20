package org.springframework.samples.petclinic.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;

public class VoteCardTest {

    @Test
    public void testGetCard() {
        VoteCard card = new VoteCard();
        card.setType(VCType.YELLOW);
        assertEquals(card.getCard(), "/resources/images/YellowVote.PNG");
    }
    
}
