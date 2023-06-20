package org.springframework.samples.petclinic.deck;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;

@DataJpaTest
public class DeckRepositoryTest {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    private static final Integer PLAYER_WITH_DECKS_ID = 3;
    private static final Integer PLAYER_WITHOUT_DECKS_ID = 13;
    private static final Integer TEST_GAME_ID = 2;

    @Test
    public void testFindPlayerDecks() {
        List<Deck> decks = deckRepository.findPlayerDecks(PLAYER_WITH_DECKS_ID);
        assertNotNull(decks);
        assertFalse(decks.isEmpty());
    }

    @Test
    public void testFindPlayerDecksNotExistingDecks() {
        List<Deck> decks = deckRepository.findPlayerDecks(PLAYER_WITHOUT_DECKS_ID);
        assertNotNull(decks);
        assertTrue(decks.isEmpty());
    }
 
    @Test
    public void testFindDeckByPlayerAndGame() {
        Player player = playerRepository.findPlayerByUsername("migmanalv");
        Game game = gameRepository.findById(TEST_GAME_ID);
        Deck deck = deckRepository.findDeckByPlayerAndGame(player, game);
        assertNotNull(deck);
    }

    @Test
    public void testFindDeckByPlayerAndGameNotExistingDecks() {
        Player player = playerRepository.findPlayerByUsername("player5");
        Game game = gameRepository.findById(TEST_GAME_ID);
        Deck deck = deckRepository.findDeckByPlayerAndGame(player, game);
        assertNull(deck);
    }
   
}
