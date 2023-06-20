package org.springframework.samples.petclinic.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.envers.DefaultRevisionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.data.history.RevisionMetadata.RevisionType;
import org.springframework.samples.petclinic.deck.Deck;
import org.springframework.samples.petclinic.deck.DeckRepository;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    @Mock
	UserRepository userRepository;

	@Mock
	PlayerInfoRepository playerInfoRepository;

	@Mock
	DeckRepository deckRepository;

	@Mock
	GameRepository gameRepository;

	@Mock
	SessionRegistry sessionRegistry;

    @Mock
	UserService userService;

    @Mock
	AuthoritiesService authoritiesService;

	@Mock
	DeckService deckService;

    PlayerService service;
    User user;
    Player player;
    Game game;


    @BeforeEach
    public void config() {
        service = new PlayerService(playerRepository);

        user = new User();
        user.setUsername("Player");

        player = new Player();
        player.setId(1);
        player.setUser(user);
        player.setOnline(false);

        List<Player> players = new ArrayList<>();
        players.add(player);

        List<String> usernames = new ArrayList<>();
        usernames.add("Test player");

        when(playerRepository.findAll()).thenReturn(players);
        when(playerRepository.findAllUsernames()).thenReturn(usernames);
        when(playerRepository.findPlayerByUsername(anyString())).thenReturn(player);

        DefaultRevisionEntity entity = new DefaultRevisionEntity();

        RevisionMetadata<Integer> rm = new DefaultRevisionMetadata(entity, RevisionType.INSERT);
        Revision<Integer, Player> revision = Revision.of(rm, player);

        List<Revision<Integer, Player>> revisionsList = new ArrayList<>();
        revisionsList.add(revision);
        
        Revisions<Integer, Player> revisions = Revisions.of(revisionsList);
        
        when(playerRepository.findRevisions(anyInt())).thenReturn(revisions);

        game = new Game();
        game.setName("Test game");
        game.setWinners(Faction.LOYALS);
        game.setStartDate(Date.from(Instant.parse("2023-01-11T18:00:00.00Z")));
        game.setEndDate(Date.from(Instant.parse("2023-01-11T18:25:00.00Z")));
        game.setNumPlayers(6);
        List<Game> games = new ArrayList<>();
        games.add(game);

        when(playerInfoRepository.findGamesByPlayer(any(Player.class))).thenReturn(games);
        when(playerInfoRepository.findPlayersByGame(any(Game.class))).thenReturn(players);

        Deck deck = new Deck();
        List<Deck> decks = new ArrayList<>();
        decks.add(deck);

        when(deckRepository.findPlayerDecks(anyInt())).thenReturn(decks);

        org.springframework.security.core.userdetails.User user2 = new org.springframework.security.core.userdetails.User("Player", "1234", new ArrayList<>());
        Object object = (Object) user2;
        List<Object> objects = new ArrayList<>();
        objects.add(object);

        SessionInformation sessionInformation = new SessionInformation(player, "a", new Date());
        List<SessionInformation> sessionInformations = new ArrayList<>();
        sessionInformations.add(sessionInformation);

        when(sessionRegistry.getAllPrincipals()).thenReturn(objects);
        when(sessionRegistry.getAllSessions(any(Object.class), anyBoolean())).thenReturn(sessionInformations);

        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findUserWithAuthority(anyString())).thenReturn(users);

        when(gameRepository.findByState(any(State.class))).thenReturn(games);

        when(deckService.winnerPlayers(any(Game.class), any(Faction.class))).thenReturn(players);
    }

    @Test
    public void testGetPageNumbers() {
        List<Integer> res = service.getPageNumbers();
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testDuplicatedUsername() {
        Boolean res = service.duplicatedUsername("Test player");
        assertNotNull(res);
        assertTrue(res);
    }

    @Test
    public void testSavePlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        try {
            service.savePlayer(player);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testSaveEditedPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        player.getUser().setPassword("1234");
        try {
            service.saveEditedPlayer(player);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testHasGamesPlayed() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Boolean res = service.hasGamesPlayed(player);
        assertNotNull(res);
        assertTrue(res);
    }

    @Test
    public void testCheckOnlineStatus() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        service.checkOnlineStatus();
        assertTrue(player.getOnline());
    }

    @Test
    public void testAuditPlayer() {
        List<String> res = service.auditPlayer(player);
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testGetGamesPlayedByPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Double res = service.getGamesPlayedByPlayer(player);
        assertEquals(1, res);  
    }

    @Test
    public void testfindWinsByPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Integer res = service.findWinsByPlayer(player);
        assertEquals(1, res);      
    }

    @Test
    public void testfindUserWinsAsTraitor() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        game.setWinners(Faction.TRAITORS);
        Double res = service.findUserWinsAsTraitor(user);
        assertEquals(1, res);      
    }

    @Test
    public void testfindUserWinsAsLoyal() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Double res = service.findUserWinsAsLoyal(user);
        assertEquals(1, res);      
    }

    @Test
    public void testfindUserWinsAsMerchant() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        game.setWinners(Faction.MERCHANTS);
        Double res = service.findUserWinsAsMerchant(user);
        assertEquals(1, res);      
    }

    @Test
    public void testGetTotalTimePlaying() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Integer time = service.getTotalTimePlaying(user);
        assertEquals(25, time);        
    }

    @Test
    public void testGetMinTimePlaying() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Integer time = service.getMinTimePlaying(user);
        assertEquals(25, time);     }

    @Test
    public void testGetMaxTimePlaying() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Integer time = service.getMaxTimePlaying(user);
        assertEquals(25, time);     }

    @Test
    public void testGetAvgNumPlayersByPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Double res = service.getAvgNumPlayersByPlayer(player);
        assertEquals(6, res);        
    }

    @Test
    public void testGetMinNumPlayersByPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Double res = service.getMinNumPlayersByPlayer(player);
        assertEquals(6, res);        
    }

    @Test
    public void testGetMaxNumPlayersByPlayer() {
        service = new PlayerService(playerRepository, userRepository, playerInfoRepository, deckRepository, gameRepository, sessionRegistry, userService, authoritiesService, deckService);
        Double res = service.getMaxNumPlayersByPlayer(player);
        assertEquals(6, res);        
    }

    
}
