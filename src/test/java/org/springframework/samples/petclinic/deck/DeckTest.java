package org.springframework.samples.petclinic.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.enums.RoleCard;

public class DeckTest {

    @Test
    public void testGetRoleCardImg() {
        Deck deck = new Deck();
        deck.setRoleCard(RoleCard.CONSUL);
        assertEquals(deck.getRoleCardImg(), "/resources/images/Consul.png");
    }
    
}
