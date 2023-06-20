package org.springframework.samples.petclinic.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.achievements.AchievementRepository;
import org.springframework.samples.petclinic.comment.CommentRepository;
import org.springframework.samples.petclinic.comment.CommentService;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.deck.Deck;
import org.springframework.samples.petclinic.deck.DeckRepository;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.deck.FactionCardRepository;
import org.springframework.samples.petclinic.deck.VoteCard;
import org.springframework.samples.petclinic.deck.VoteCardRepository;
import org.springframework.samples.petclinic.deck.VoteCardService;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.invitation.InvitationRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoService;
import org.springframework.samples.petclinic.progress.ProgressRepository;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCard;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCardRepository;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCardService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnRepository;
import org.springframework.samples.petclinic.turn.TurnService;
import org.springframework.samples.petclinic.user.AuthoritiesRepository;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(controllers = GameController.class, 
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
            classes = WebSecurityConfigurer.class), 
            excludeAutoConfiguration = SecurityConfiguration.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private GameService gameService;

    @MockBean 
    private PlayerService playerService;

    @MockBean
    private PlayerInfoService playerInfoService;

    private static final Integer TEST_GAME_ID = 1;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer MAX_PLAYERS = 8;
    private static final VCType TEST_VOTE = VCType.GREEN;
    private static final FCType TEST_FACTION = FCType.LOYAL;
   

    Game game;
    Player p1;

    @BeforeEach
    public void config() {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setTurn(new Turn());
        List<Game> games = new ArrayList<>();
        List<Game> noGames = new ArrayList<>();
        games.add(game);
        when(gameService.getPublicGamesByNameAndState(anyString(), any(State.class))).thenReturn(games).thenReturn(noGames);
        when(gameService.getPlayerGamesHistory(anyString(), any(Player.class), any(Boolean.class))).thenReturn(games).thenReturn(noGames);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        when(gameService.startGameIfNeeded(any(Game.class), any(SuffragiumCard.class))).thenReturn(game);
        when(gameService.gameRoleCardNumber(any(Game.class))).thenReturn(4);

        when(playerService.getPlayerByUsername(anyString())).thenReturn(new Player());

        p1 = new Player();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        when(playerInfoService.getPlayerInfosByGame(any(Game.class))).thenReturn(new ArrayList<>());
        when(playerInfoService.getPlayerInfoByGameAndPlayer(any(Game.class), any(Player.class))).thenReturn(new PlayerInfo());
        when(playerInfoService.getAllUsersByGame(any(Game.class))).thenReturn(players);
        when(playerInfoService.savePlayerInfo(any(PlayerInfo.class), any(Game.class), any(Player.class))).thenReturn(new PlayerInfo());
        when(playerInfoService.saveSpectatorInfo(any(PlayerInfo.class), any(Game.class), any(Player.class))).thenReturn(new PlayerInfo());
    
        when(suffragiumCardService.createSuffragiumCardIfNeeded(any(Game.class))).thenReturn(new SuffragiumCard());
        when(suffragiumCardService.getSuffragiumCardByGame(anyInt())).thenReturn(new SuffragiumCard());

        when(deckService.getDeckByPlayerAndGame(any(Player.class), any(Game.class))).thenReturn(new Deck());
        when(deckService.winnerPlayers(any(Game.class), any(Faction.class))).thenReturn(new ArrayList<>());
        when(deckService.loserPlayers(any(Game.class), anyList())).thenReturn(new ArrayList<>());

        when(voteCardService.getById(any(VCType.class))).thenReturn(new VoteCard());
        when(voteCardService.getChangeOptions(any(Game.class), any(VoteCard.class))).thenReturn(new ArrayList<>());
        
    } 
    
    @WithMockUser
    @Test
    public void testGamesHistoryForm() throws Exception {
        mockMvc.perform(get("/games/history/find"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesHistory"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesHistoryForm() throws Exception {
        mockMvc.perform(get("/games/history"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/gamesFinishedListAdmin"))
        .andExpect(model().attributeExists("returnButton"))
        .andExpect(model().attributeExists("publicGames"))
        .andExpect(model().attributeExists("privateGames"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesHistoryFormNoGamesFound() throws Exception {
        mockMvc.perform(get("/games/history")); // aux perform to /games/history in order to get the first return of when(getPublicGamesByNameAndState()) in config()
        mockMvc.perform(get("/games/history"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesHistory"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testGamesHistoryByPlayerForm() throws Exception {
        mockMvc.perform(get("/games/playerHistory/find"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesPlayerHistory"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesHistoryByPlayerForm() throws Exception {
        mockMvc.perform(get("/games/playerHistory"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/gamesFinishedList"))
        .andExpect(model().attributeExists("player"))
        .andExpect(model().attributeExists("gamesWinners"))
        .andExpect(model().attributeExists("returnButton"))
        .andExpect(model().attributeExists("publicGames"))
        .andExpect(model().attributeExists("privateGames"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesByPlayerHistoryFormNoGamesFound() throws Exception {
        mockMvc.perform(get("/games/playerHistory")); // aux perform to /games/playerHistory in order to get the first return of when(getPlayerGamesHistory()) in config()
        mockMvc.perform(get("/games/playerHistory"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesPlayerHistory"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testGamesInProcessForm() throws Exception {
        mockMvc.perform(get("/games/inProcess/find"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesInProcess"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesInProcessForm() throws Exception {
        mockMvc.perform(get("/games/inProcess"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/gamesInProcessList"))
        .andExpect(model().attributeExists("returnButton"))
        .andExpect(model().attributeExists("publicGames"))
        .andExpect(model().attributeExists("privateGames"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesInProcessFormNoGamesFound() throws Exception {
        mockMvc.perform(get("/games/inProcess")); // aux perform to /games/inProcess in order to get the first return of when(getPublicGamesByNameAndState()) in config()
        mockMvc.perform(get("/games/inProcess"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesInProcess"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testGamesStartingForm() throws Exception {
        mockMvc.perform(get("/games/starting/find"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesStarting"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesStartingForm() throws Exception {
        mockMvc.perform(get("/games/starting"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/gamesStartingList"))
        .andExpect(model().attributeExists("returnButton"))
        .andExpect(model().attributeExists("publicGames"))
        .andExpect(model().attributeExists("friendsGames"));
    }

    @WithMockUser
    @Test
    public void testProcessGamesStartingFormNoGamesFound() throws Exception {
        mockMvc.perform(get("/games/starting")); // aux perform to /games/starting in order to get the first return of when(getPublicGamesByNameAndState()) in config()
        mockMvc.perform(get("/games/starting"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesStarting"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testCreateGameForm() throws Exception {
        mockMvc.perform(get("/games/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/createGame"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testCreateGameSuccessful() throws Exception {
        mockMvc.perform(post("/games/create")
        .with(csrf())
        .param("name", "Test game")
        .param("publicGame", "true"))
        .andExpect(status().is3xxRedirection());
    }

    @WithMockUser
    @Test
    public void testCreateGameUnsuccessfulDueToShortName() throws Exception {
        mockMvc.perform(post("/games/create")
        .with(csrf())
        .param("name", "g")
        .param("publicGame", "true"))
        .andExpect(view().name("/games/createGame"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testShowLobby() throws Exception {
        mockMvc.perform(get("/games/{gameId}/lobby", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/gameLobby"))
        .andExpect(model().attributeExists("game"))
        .andExpect(model().attributeExists("playerInfos"))
        .andExpect(model().attributeExists("currentPlayerInfo"));
    }

    @WithMockUser
    @Test
    public void testShowLobbyRedirectToGame() throws Exception {
        game.setState(State.IN_PROCESS);
        mockMvc.perform(get("/games/{gameId}/lobby", TEST_GAME_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testJoinGame() throws Exception {
        mockMvc.perform(get("/games/{gameId}/join", TEST_GAME_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID+"/lobby"));
    }

    @WithMockUser
    @Test
    public void testJoinGameUnsuccessfulDueToPlayerWasAlreadyInTheGame() throws Exception {
        when(playerService.getPlayerByUsername(anyString())).thenReturn(p1);
        mockMvc.perform(get("/games/{gameId}/join", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesStarting"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testJoinGameUnsuccessfulDueToMaxNumPlayersReached() throws Exception {
        game.setNumPlayers(MAX_PLAYERS);
        mockMvc.perform(get("/games/{gameId}/join", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesStarting"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testSpectateGame() throws Exception {
        mockMvc.perform(get("/games/{gameId}/spectate", TEST_GAME_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID+"/lobby"));
    }

    @WithMockUser
    @Test
    public void testSpectateGameUnsuccessfulDueToPlayerWasAlreadyInTheGame() throws Exception {
        when(playerService.getPlayerByUsername(anyString())).thenReturn(p1);
        mockMvc.perform(get("/games/{gameId}/spectate", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/findGamesStarting"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("game"));
    }

    @WithMockUser
    @Test
    public void testExitGame() throws Exception {
        mockMvc.perform(get("/games/{gameId}/exit", TEST_GAME_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));
    }

    @WithMockUser
    @Test
    public void testShowGameForSpectator() throws Exception {
        mockMvc.perform(get("/games/{gameId}", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/game"))
        .andExpect(model().attributeExists("activePlayers"))
        .andExpect(model().attributeExists("votesAssigned"))
        .andExpect(model().attributeExists("roleCardNumber"))
        .andExpect(model().attributeExists("turn"))
        .andExpect(model().attributeExists("currentPlayer"))
        .andExpect(model().attributeExists("game"))
        .andExpect(model().attributeExists("playerInfos"))
        .andExpect(model().attributeExists("suffragiumCard"));
    }

    @WithMockUser
    @Test
    public void testShowGameForPlayer() throws Exception {
        when(playerInfoService.isSpectator(any(Player.class), any(Game.class))).thenReturn(false);
        mockMvc.perform(get("/games/{gameId}", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/game"))
        .andExpect(model().attributeExists("activePlayers"))
        .andExpect(model().attributeExists("votesAssigned"))
        .andExpect(model().attributeExists("roleCardNumber"))
        .andExpect(model().attributeExists("turn"))
        .andExpect(model().attributeExists("currentPlayer"))
        .andExpect(model().attributeExists("game"))
        .andExpect(model().attributeExists("playerInfos"))
        .andExpect(model().attributeExists("suffragiumCard"))
        .andExpect(model().attributeExists("playerDeck"));
    }

    @WithMockUser
    @Test
    public void testShowGameFinished() throws Exception {
        game.setState(State.FINISHED);
        mockMvc.perform(get("/games/{gameId}", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("/games/game"))
        .andExpect(model().attributeExists("activePlayers"))
        .andExpect(model().attributeExists("votesAssigned"))
        .andExpect(model().attributeExists("roleCardNumber"))
        .andExpect(model().attributeExists("turn"))
        .andExpect(model().attributeExists("currentPlayer"))
        .andExpect(model().attributeExists("game"))
        .andExpect(model().attributeExists("playerInfos"))
        .andExpect(model().attributeExists("suffragiumCard"))
        .andExpect(model().attributeExists("winnerPlayers"))
        .andExpect(model().attributeExists("loserPlayers"));
    }

    @WithMockUser
    @Test
    public void testPretorSelection() throws Exception {
        mockMvc.perform(get("/games/{gameId}/pretorSelection/{voteType}", TEST_GAME_ID, TEST_VOTE))
        .andExpect(status().isOk())
        .andExpect(view().name("games/pretorCardSelection"))
        .andExpect(model().attributeExists("game"))
        .andExpect(model().attributeExists("selectedCard"))
        .andExpect(model().attributeExists("changeOptions"));
    }

    @WithMockUser
    @Test
    public void testForcedVoteChange() throws Exception {
        mockMvc.perform(get("/games/{gameId}/forcedVoteChange/{playerId}", TEST_GAME_ID, TEST_PLAYER_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testPretorChange() throws Exception {
        mockMvc.perform(get("/games/{gameId}/pretorSelection/{voteType}/{changedVoteType}", TEST_GAME_ID, TEST_VOTE, TEST_VOTE))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testUpdateSuffragium() throws Exception {
        mockMvc.perform(get("/games/{gameId}/updateSuffragium", TEST_GAME_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testUpdateTurnVotes() throws Exception {
        mockMvc.perform(get("/games/{gameId}/updateVotes/{voteType}", TEST_GAME_ID, TEST_VOTE))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testSelectFaction() throws Exception {
        mockMvc.perform(get("/games/{gameId}/edit/{factionType}", TEST_GAME_ID, TEST_FACTION))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testRolesDesignation() throws Exception {
        mockMvc.perform(get("/games/{gameId}/rolesDesignation", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("games/rolesDesignation"))
        .andExpect(model().attributeExists("currentGame"))
        .andExpect(model().attributeExists("pretorCandidates"))
        .andExpect(model().attributeExists("edil1Candidates"))
        .andExpect(model().attributeExists("edil2Candidates"));
    }

    @WithMockUser
    @Test
    public void testFinalRolesDesignation() throws Exception {
        mockMvc.perform(get("/games/{gameId}/rolesDesignation/{pretorId}/{edil1Id}/{edil2Id}", TEST_GAME_ID, TEST_PLAYER_ID, TEST_PLAYER_ID, TEST_PLAYER_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID));
    }

    @WithMockUser
    @Test
    public void testSendComment() throws Exception {
        mockMvc.perform(get("/games/{gameId}/chat", TEST_GAME_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("games/sendComment"))
        .andExpect(model().attributeExists("comment"))
        .andExpect(model().attributeExists("gameId"));
    }

    @WithMockUser
    @Test
    public void testSaveComment() throws Exception {
        mockMvc.perform(post("/games/{gameId}/chat", TEST_GAME_ID)
        .with(csrf())
        .param("message", "Hello"))
        .andExpect(status().is3xxRedirection());
    }

    @WithMockUser
    @Test
    public void testSaveCommentUnsuccessfulDueToBlankMessage() throws Exception {
        mockMvc.perform(post("/games/{gameId}/chat", TEST_GAME_ID)
        .with(csrf())
        .param("message", ""))
        .andExpect(view().name("games/sendComment"))
        .andExpect(model().attributeExists("comment"))
        .andExpect(model().attributeExists("gameId"));
    }

    /* 
    The following MockBeans are necessary to make controller tests work correctly, since we're using the annotation
    "@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)" in "PetclinicApplication.java".
    This anotation is required to access audit information about players, but forces us to create the MockBeans.
    */

    @MockBean
	private SuffragiumCardService suffragiumCardService;

	@MockBean
	private DeckService deckService;

	@MockBean
	private TurnService turnService;

	@MockBean
	private VoteCardService voteCardService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private PlayerInfoRepository playerInfoRepository;

    @MockBean
	private SuffragiumCardRepository suffragiumCardRepository;

	@MockBean
	private DeckRepository deckRepository;

	@MockBean
	private TurnRepository turnRepository;

	@MockBean
	private VoteCardRepository voteCardRepository;

    @MockBean
    private InvitationRepository invitationRepository;

    @MockBean
    private AchievementRepository achievementRepository;

    @MockBean
    private ProgressRepository progressRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;

    @MockBean
    private FactionCardRepository factionCardRepository;

    @MockBean
    private CommentRepository commentRepository;

}
