package org.springframework.samples.petclinic.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;

public class FactionCardTest {

    @Test
    public void testGetCard() {
        FactionCard card = new FactionCard();
        card.setType(FCType.LOYAL);
        assertEquals(card.getCard(), "/resources/images/Loyal.PNG");
    }
    
}
