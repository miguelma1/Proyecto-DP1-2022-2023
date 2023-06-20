package org.springframework.samples.petclinic.game;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.samples.petclinic.deck.Deck;
import org.springframework.samples.petclinic.deck.DeckRepository;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.deck.VoteCard;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.RoleCard;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCard;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnRepository;
import org.springframework.samples.petclinic.user.User;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GameServiceTest {
    
    @Mock
    GameRepository gameRepository;

    @Mock
    PlayerInfoRepository playerInfoRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    TurnRepository turnRepository;

    @Mock
    DeckRepository deckRepository;

    @Mock 
    InvitationService invitationService;

    @Mock
    DeckService deckService;

    Game game;
    Game gameInProcess;
    Game gameFinished;
    Player p1;
    Player p2;
    Deck deck;

    private Game createGame(String name, Boolean publicGame) {
        Game game = new Game();
        game.setName(name);
        game.setPublicGame(publicGame);
        game.setState(State.STARTING);
        game.setNumPlayers(1);
        game.setStartDate(Date.from(Instant.now()));
        return game;
    }

    public Turn createTurn(Integer votesLoyal, Integer votesTraitor, Integer votesNeutral) {
        Turn turn = new Turn();
        turn.setCurrentTurn(1);
        turn.setVotesLoyal(votesLoyal);
        turn.setVotesTraitor(votesTraitor);
        turn.setVotesNeutral(votesNeutral);
        return turn;
    }

    private SuffragiumCard createSuffragiumCard(Integer loyalsVotes, Integer traitorsVotes, Integer voteLimit) {
        SuffragiumCard card = new SuffragiumCard();
        card.setLoyalsVotes(loyalsVotes);
        card.setTraitorsVotes(traitorsVotes);
        card.setVoteLimit(voteLimit);
        return card;
    }

    @BeforeEach
    public void config() {
        game = createGame("Test game", true);
        gameInProcess = createGame("Test game in process", true);
        gameInProcess.setState(State.IN_PROCESS);
        gameFinished = createGame("Test game finished", true);
        gameFinished.setState(State.FINISHED);
        gameFinished.setStartDate(Date.from(Instant.parse("2023-01-11T18:00:00.00Z")));
        gameFinished.setEndDate(Date.from(Instant.parse("2023-01-11T18:25:00.00Z")));
        gameFinished.setNumPlayers(6);
        List<Game> games = new ArrayList<>();
        List<Game> gamesInProcess = new ArrayList<>();
        List<Game> gamesFinished = new ArrayList<>();
        games.add(game);
        gamesInProcess.add(gameInProcess);
        gamesFinished.add(gameFinished);
        when(gameRepository.findByName(anyString())).thenReturn(games).thenReturn(new ArrayList<>()).thenReturn(gamesFinished);
        when(gameRepository.findPublicGamesByName(anyString())).thenReturn(games).thenReturn(new ArrayList<>());
        when(gameRepository.findPrivateGamesByName(anyString())).thenReturn(games).thenReturn(new ArrayList<>());
        when(gameRepository.findAll()).thenReturn(games);
        when(gameRepository.findByState(any(State.class))).thenReturn(gamesFinished);

        p1 = new Player();
        User u1 = new User();
        u1.setUsername("player1");
        p1.setUser(u1);
        p2 = new Player();
        User u2 = new User();
        u2.setUsername("player2");
        p2.setUser(u2);
        List<Player> players = new ArrayList<>();
        players.add(p2);
        when(invitationService.getFriends(any(Player.class))).thenReturn(players);

        when(playerInfoRepository.findPlayersByGame(any(Game.class))).thenReturn(players);
        when(playerInfoRepository.findGamesByPlayer(any(Player.class))).thenReturn(gamesFinished);
        when(playerInfoRepository.findGamesInProcessByPlayer(any(Player.class))).thenReturn(gamesInProcess);

        when(playerRepository.save(any(Player.class))).thenReturn(p1);

        when(turnRepository.save(any(Turn.class))).thenReturn(new Turn());

        deck = new Deck();
        deck.setGame(game);
        deck.setRoleCard(RoleCard.CONSUL);
        List<Deck> decks = new ArrayList<>();
        decks.add(deck);
        when(deckRepository.findAll()).thenReturn(decks);

        List<Player> winners = new ArrayList<>();
        winners.add(p1);
        when(deckService.winnerPlayers(any(Game.class), any(Faction.class))).thenReturn(winners);
    }

    @Test
    public void testGetGamesByNameAndState() {
        GameService service = new GameService(gameRepository);
        List<Game> games = service.getGamesByNameAndState("Test game", State.STARTING);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testGetGamesByNameAndStateNotExistingGames() {
        GameService service = new GameService(gameRepository);
        service.getGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        List<Game> games = service.getGamesByNameAndState("jasdbjfahl", State.STARTING);
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetPublicGamesByNameAndState() {
        GameService service = new GameService(gameRepository);
        List<Game> games = service.getPublicGamesByNameAndState("Test game", State.STARTING);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testGetPublicGamesByNameAndStateNotExistingGames() {
        GameService service = new GameService(gameRepository);
        service.getPublicGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getPublicGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        List<Game> games = service.getPublicGamesByNameAndState("jasdbjfahl", State.STARTING);
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetPrivateGamesByNameAndState() {
        GameService service = new GameService(gameRepository);
        List<Game> games = service.getPrivateGamesByNameAndState("Test game", State.STARTING);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testGetPrivateGamesByNameAndStateNotExistingGames() {
        GameService service = new GameService(gameRepository);
        service.getPrivateGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getPrivateGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        List<Game> games = service.getPrivateGamesByNameAndState("jasdbjfahl", State.STARTING);
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetFriendGamesByNameAndState() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        List<Game> games = service.getFriendGamesByNameAndState("Test game", State.STARTING, new Player());
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testGetFriendGamesByNameAndStateNotExistingGames() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        service.getFriendGamesByNameAndState("jasdbjfahl", State.STARTING, new Player()); // aux call to getFriendGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        List<Game> games = service.getFriendGamesByNameAndState("jasdbjfahl", State.STARTING, new Player());
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testGetPlayerGamesHistory() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        service.getGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        service.getGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getGamesByNameAndState() in order to get the second return of when(findByName()) in config()
        List<Game> games = service.getPlayerGamesHistory("Test game finished", p1, true);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testGetPlayerGamesHistoryNotExistingPrivateGames() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        service.getGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getGamesByNameAndState() in order to get the first return of when(findByName()) in config()
        service.getGamesByNameAndState("jasdbjfahl", State.STARTING); // aux call to getGamesByNameAndState() in order to get the second return of when(findByName()) in config()
        List<Game> games = service.getPlayerGamesHistory("Test game finished", p1, false);
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testSaveGame() {
        Game game = createGame("Test game", true);
        Turn turn = createTurn(0, 0, 0);
        GameService service = new GameService(gameRepository);
        try {
            service.saveGame(game, turn);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }
 
    @Test
    public void testStartGameIfNeeded() {
        Game game = createGame("Game to start", true);
        SuffragiumCard suffragiumCard = createSuffragiumCard(0, 0, 15);
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        service.startGameIfNeeded(game, suffragiumCard);
        assertTrue(game.getState() == State.IN_PROCESS);        
    }

    @Test
    public void testCheckPlayerIsPlaying() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        service.checkPlayerIsPlaying(p1);
        assertTrue(p1.getPlaying());        
    }

    @Test
    public void testJoinGame() {
        GameService service = new GameService(gameRepository);
        service.joinGame(game);
        assertTrue(game.getNumPlayers()==2);        
    }

    @Test
    public void testExitGame() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setId(1);
        playerInfo.setSpectator(false);
        game.setNumPlayers(4);
        service.exitGame(playerInfo, game);
        assertTrue(game.getNumPlayers()==3);        
    }

    @Test
    public void testChangeStage() {
        GameService service = new GameService(gameRepository);
        service.changeStage(game, CurrentStage.END_OF_TURN);
        assertTrue(game.getStage() == CurrentStage.END_OF_TURN);        
    }

    @Test
    public void testChangeStageIfVotesCompletedToVeto() {
        game.setTurn(createTurn(1, 1, 0));
        GameService service = new GameService(gameRepository);
        service.changeStageIfVotesCompleted(game);
        assertTrue(game.getStage() == CurrentStage.VETO);        
    }

    @Test
    public void testChangeStageIfVotesCompletedToScoring() {
        game.setTurn(createTurn(2, 1, 0));
        GameService service = new GameService(gameRepository);
        service.changeStageIfVotesCompleted(game);
        assertTrue(game.getStage() == CurrentStage.SCORING);        
    }

    @Test
    public void testChangeStageIfVotesCompletedNoChange() {
        game.setStage(CurrentStage.VOTING);
        game.setTurn(createTurn(0, 0, 0));
        GameService service = new GameService(gameRepository);
        service.changeStageIfVotesCompleted(game);
        assertTrue(game.getStage() == CurrentStage.VOTING);        
    }

    @Test
    public void testChangeTurnAndRoundToNewTurn() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setTurn(createTurn(1, 1, 0));
        game.setNumPlayers(5);
        service.changeTurnAndRound(game);
        assertTrue(game.getTurn().getCurrentTurn() == 2);        
    }

    @Test
    public void testChangeTurnAndRoundToNewTurnAndRound() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        Turn turn = createTurn(1, 1, 0);
        turn.setCurrentTurn(5);
        game.setTurn(turn);
        game.setRound(CurrentRound.FIRST);
        game.setNumPlayers(5);
        service.changeTurnAndRound(game);
        assertTrue(game.getTurn().getCurrentTurn() == 1); 
        assertTrue(game.getRound() == CurrentRound.SECOND);       
    }

    @Test
    public void testChangeTurnAndRoundGameFinished() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        Turn turn = createTurn(1, 1, 0);
        turn.setCurrentTurn(5);
        game.setTurn(turn);
        game.setRound(CurrentRound.SECOND);
        game.setNumPlayers(5);
        service.changeTurnAndRound(game);
        assertTrue(game.getState() == State.FINISHED);      
    }

    @Test
    public void testGameRoleCardNumber() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        Integer number = service.gameRoleCardNumber(game);
        assertTrue(number == 1);        
    }

    @Test
    public void testWinnerFactionLoyalsFailedConspiracy() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setNumPlayers(5);
        game.setSuffragiumCard(createSuffragiumCard(1, 13, 13));
        service.winnerFaction(game);
        assertTrue(game.getWinners() == Faction.LOYALS);  
    }
    @Test
    public void testWinnerFactionTraitorsFailedConspiracy() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setNumPlayers(5);
        game.setSuffragiumCard(createSuffragiumCard(13, 2, 13));
        service.winnerFaction(game);
        assertTrue(game.getWinners() == Faction.TRAITORS);  
    }
    
    @Test
    public void testWinnerFactionLoyalsIdesOfMarch() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setNumPlayers(5);
        game.setSuffragiumCard(createSuffragiumCard(12, 6, 13));
        service.winnerFaction(game);
        assertTrue(game.getWinners() == Faction.LOYALS);  
    }

    @Test
    public void testWinnerFactionTraitorsIdesOfMarch() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setNumPlayers(5);
        game.setSuffragiumCard(createSuffragiumCard(7, 11, 13));
        service.winnerFaction(game);
        assertTrue(game.getWinners() == Faction.TRAITORS);  
    }

    @Test
    public void testWinnerFactionMerchantsIdesOfMarch() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setNumPlayers(5);
        game.setSuffragiumCard(createSuffragiumCard(8, 9, 13));
        service.winnerFaction(game);
        assertTrue(game.getWinners() == Faction.MERCHANTS);  
    }
 
    @Test
    public void testWinnersByGame() {
        game.setWinners(Faction.LOYALS);
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        Map<Game, List<Player>> winners2 = service.winnersByGame();
        assertTrue(winners2.get(game).contains(p1));  
    }
    
    @Test
    public void testActivePlayersConsulInVoting() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setStage(CurrentStage.VOTING);
        when(deckService.votesAsigned(any(Game.class))).thenReturn(false);
        when(deckRepository.findDeckByPlayerAndGame(any(Player.class), any(Game.class))).thenReturn(deck);
        List<String> usernames = service.activePlayers(game);
        assertTrue(usernames.contains("player2"));        
    }

    @Test
    public void testActivePlayersEdil() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setStage(CurrentStage.VOTING);
        when(deckService.votesAsigned(any(Game.class))).thenReturn(true);
        deck.setRoleCard(RoleCard.EDIL);
        List<VoteCard> voteCards = new ArrayList<>();
        voteCards.add(new VoteCard());
        voteCards.add(new VoteCard());
        deck.setVoteCards(voteCards);
        when(deckRepository.findDeckByPlayerAndGame(any(Player.class), any(Game.class))).thenReturn(deck);
        List<String> usernames = service.activePlayers(game);
        assertTrue(usernames.contains("player2"));        
    }
    
    @Test
    public void testActivePlayersPretor() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setStage(CurrentStage.VETO);
        deck.setRoleCard(RoleCard.PRETOR);
        when(deckRepository.findDeckByPlayerAndGame(any(Player.class), any(Game.class))).thenReturn(deck);
        List<String> usernames = service.activePlayers(game);
        assertTrue(usernames.contains("player2"));        
    }

    @Test
    public void testActivePlayersConsulInEndOfTurn() {
        GameService service = new GameService(gameRepository, playerInfoRepository, playerRepository, turnRepository, deckRepository, invitationService, deckService);
        game.setStage(CurrentStage.END_OF_TURN);
        when(deckRepository.findDeckByPlayerAndGame(any(Player.class), any(Game.class))).thenReturn(deck);
        List<String> usernames = service.activePlayers(game);
        assertTrue(usernames.contains("player2"));        
    }

    @Test
    public void testGetGlobalTimePlaying() {
        GameService service = new GameService(gameRepository);
        Integer time = service.getGlobalTimePlaying();
        assertEquals(time, 25);;        
    }

    @Test
    public void testGetGlobalMaxTimePlaying() {
        GameService service = new GameService(gameRepository);
        Integer time = service.getGlobalMaxTimePlaying();
        assertEquals(time, 25);;        
    }

    @Test
    public void testGetGlobalMinTimePlaying() {
        GameService service = new GameService(gameRepository);
        Integer time = service.getGlobalMinTimePlaying();
        assertEquals(time, 25);;        
    }

    @Test
    public void testGetGlobalAvgNumPlayers() {
        GameService service = new GameService(gameRepository);
        Double res = service.getGlobalAvgNumPlayers();
        assertEquals(res, 6);;        
    }

    @Test
    public void testGetGlobalMinNumPlayers() {
        GameService service = new GameService(gameRepository);
        Double res = service.getGlobalMinNumPlayers();
        assertEquals(res, 6);;        
    }

    @Test
    public void testGetGlobalMaxNumPlayers() {
        GameService service = new GameService(gameRepository);
        Double res = service.getGlobalMaxNumPlayers();
        assertEquals(res, 6);;        
    }





}
