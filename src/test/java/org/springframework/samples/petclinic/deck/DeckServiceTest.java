package org.springframework.samples.petclinic.deck;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.RoleCard;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FactionCardRepository factionCardRepository;

    @Mock
    PlayerInfoRepository playerInfoRepository;

    @Mock
    VoteCardRepository voteCardRepository;

    Deck deck;
    FactionCard loyalCard;
    FactionCard traitorCard;
    FactionCard merchantCard;
    VoteCard redCard;
    VoteCard greenCard;
    VoteCard yellowCard;
    RoleCard edilCard;
    RoleCard pretorCard;
    RoleCard consulCard;
    Game game;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Player p5;
    List<Player> players;
    List<Deck> decksWithRoleCards;
    

    private List<Deck> createDecksWithRoleCards(Game game) {
        List<Deck> decks = new ArrayList<>();
        RoleCard[] roleCards = RoleCard.values();
        for (int i = 0; i<4; i++) {
            Deck deck = createDeck(roleCards[i]);
            decks.add(deck);
        }
        decks.add(createDeck(RoleCard.EDIL));
        decks.forEach(d -> d.setGame(game));
        return decks;
    }

    private FactionCard createFactionCard (FCType type) {
        FactionCard factionCard = new FactionCard();
        factionCard.setType(type);
        return factionCard;
    }

    private VoteCard createVoteCard (VCType type) {
        VoteCard voteCard = new VoteCard();
        voteCard.setType(type);
        return voteCard;
    }

    private RoleCard createRoleCard (String type) {
        return RoleCard.valueOf(type);
    }

    private Deck createDeck (RoleCard roleCard) {
        Deck deck = new Deck();
        deck.setRoleCard(roleCard);
        deck.setVoteCards(new ArrayList<>());
        return deck;
    }

    private Game createGame() {
        Game game = new Game();
        return game;
    }

    private Player createPlayer(Integer id) {
        Player player = new Player();
        player.setId(id);
        return player;
    }

    @BeforeEach
    public void config() {
        deck = createDeck(createRoleCard("NO_ROL"));
        game = createGame();
        decksWithRoleCards = createDecksWithRoleCards(game);
        loyalCard = createFactionCard(FCType.LOYAL);
        traitorCard = createFactionCard(FCType.TRAITOR);
        merchantCard = createFactionCard(FCType.MERCHANT);
        redCard = createVoteCard(VCType.RED);
        greenCard = createVoteCard(VCType.GREEN);
        yellowCard = createVoteCard(VCType.YELLOW);
        p1 = createPlayer(1);
        p2 = createPlayer(2);
        p3 = createPlayer(3);
        p4 = createPlayer(4);
        p5 = createPlayer(5);
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        players.add(p5);
        when(factionCardRepository.findById(FCType.LOYAL)).thenReturn(Optional.of(loyalCard));
        when(factionCardRepository.findById(FCType.TRAITOR)).thenReturn(Optional.of(traitorCard));
        when(factionCardRepository.findById(FCType.MERCHANT)).thenReturn(Optional.of(merchantCard));
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(voteCardRepository.findById(VCType.RED)).thenReturn(Optional.of(redCard));
        when(voteCardRepository.findById(VCType.GREEN)).thenReturn(Optional.of(greenCard));
        when(voteCardRepository.findById(VCType.YELLOW)).thenReturn(Optional.of(yellowCard));
        when(deckRepository.findAll()).thenReturn(decksWithRoleCards);



    }

    @ParameterizedTest
    @EnumSource(FCType.class)
    public void testUpdateFactionDeck(FCType type) { //ok
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        deckService.updateFactionDeck(deck, type);
        assertTrue(deck.getFactionCards().size() == 1);
        assertTrue(deck.getFactionCards().get(0).getType() == type);
        
    }

    @ParameterizedTest
    @EnumSource(VCType.class)
    public void testUpdateVotesDeck(VCType type) { //ok
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        deckService.updateVotesDeck(deck, type);
        assertTrue(deck.getVoteCardsNumber() == 1);
        assertTrue(deck.getVoteCards().get(0).getType() == type);
    }


    @Test
    public void testGetFirstRoundVoteCards() { //ok
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        List<VoteCard> cards = deckService.getFirstRoundVoteCards();
        assertTrue(cards.size() == 2);
        assertTrue(cards.contains(redCard));
        assertTrue(cards.contains(greenCard));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {5, 6, 7, 8})
    public void testGetFactionCards(Integer i) {//ok
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        List<FactionCard> factionCards = deckService.getFactionCards(i);
        assertTrue(factionCards.size() == i * 2);
        assertTrue(factionCards.stream().filter(c -> c.getType() == FCType.MERCHANT).count()== 2);
        assertTrue(factionCards.stream().filter(c -> c.getType() == FCType.LOYAL).count()== i-1);
        assertTrue(factionCards.stream().filter(c -> c.getType() == FCType.TRAITOR).count()== i-1);
    }

    @Test
    public void testGetPlayerFactionCards() {
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        List<FactionCard> factionCards = deckService.getFactionCards(5);
        Integer initialSize = factionCards.size();
        List<FactionCard> playerFactionCards = deckService.getPlayerFactionCards(factionCards);
        assertTrue(playerFactionCards.size() == 2);
        assertTrue(initialSize - 2 == factionCards.size());
    }

    @Test
    public void testClearDeckVoteCards() {
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        deck.setVoteCards(deckService.getFirstRoundVoteCards());
        assertTrue(deck.getVoteCards().size() == 2);
        deckService.clearDeckVoteCards(deck);
        assertTrue(deck.getVoteCards().size() == 0);
    }

    @Test
    public void testClearEdilVoteCards() {
        DeckService deckService = new DeckService(deckRepository, factionCardRepository, voteCardRepository);
        assertTrue(decksWithRoleCards.stream().filter(d -> d.getRoleCard() == RoleCard.EDIL).count() == 2);
        decksWithRoleCards.forEach(d -> {
            if (d.getRoleCard() == RoleCard.EDIL) {
                d.setVoteCards(deckService.getFirstRoundVoteCards());
            }
        });
        assertTrue(decksWithRoleCards.stream().map(d -> d.getVoteCards().size()).collect(Collectors.summingInt(Integer::intValue)) == 4);
        deckService.clearEdilVoteCards(game);
        assertTrue(decksWithRoleCards.stream().map(d -> d.getVoteCards().size()).collect(Collectors.summingInt(Integer::intValue)) == 0);

    }
}
