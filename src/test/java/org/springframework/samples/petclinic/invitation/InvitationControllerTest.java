package org.springframework.samples.petclinic.invitation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.achievements.AchievementRepository;
import org.springframework.samples.petclinic.comment.CommentRepository;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoService;
import org.springframework.samples.petclinic.progress.ProgressRepository;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.samples.petclinic.deck.DeckRepository;
import org.springframework.samples.petclinic.deck.FactionCardRepository;
import org.springframework.samples.petclinic.deck.VoteCardRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.user.AuthoritiesRepository;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCardRepository;
import org.springframework.samples.petclinic.turn.TurnRepository;

@WebMvcTest(controllers = InvitationController.class, 
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
            classes = WebSecurityConfigurer.class), 
            excludeAutoConfiguration = SecurityConfiguration.class)
public class InvitationControllerTest {

    private static final Integer TEST_INVITATION_ID = 1;
    private static final Integer TEST_GAME_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvitationService invitationService;

    Game game;
    Player p1;

    @BeforeEach
    public void config() {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(5);
        List<Game> games = new ArrayList<>();
        games.add(game);
        when(gameService.getGameById(anyInt())).thenReturn(game);     

        when(playerService.getPlayerByUsername(anyString())).thenReturn(new Player());

        when(invitationService.getFriends(any(Player.class))).thenReturn(new ArrayList<>());
        when(invitationService.getGameInvitationTypes()).thenReturn(new ArrayList<>());

        p1 = new Player();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        when(playerInfoService.getAllUsersByGame(any(Game.class))).thenReturn(players);
    } 
 
    @WithMockUser
    @Test
    public void testShowInvitationsByPlayer() throws Exception {
        mockMvc.perform(get("/invitations"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    @WithMockUser
    @Test
    public void testShowFriends() throws Exception {
        mockMvc.perform(get("/friends"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/friendsList"))
        .andExpect(model().attributeExists("friendsInvitations"));
    }
 
    @WithMockUser
    @Test
    public void testSendInvitation() throws Exception {
        mockMvc.perform(get("/invitations/send"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/sendInvitation"))
        .andExpect(model().attributeExists("players"))
        .andExpect(model().attributeExists("invitation"));
    }
 
    @WithMockUser
    @Test
    public void testSaveInvitationSuccessful() throws Exception {
        mockMvc.perform(post("/invitations/send")
        .with(csrf())
        .param("recipient", "player1")
        .param("message", "Testing invitations"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("invitations"));
    }

    @WithMockUser
    @Test
    public void testSaveInvitationUnsuccesfulDueToShortMessage() throws Exception {
        mockMvc.perform(post("/invitations/send")
        .with(csrf())
        .param("recipient", "player1")
        .param("message", "a"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/sendInvitation"))
        .andExpect(model().attributeExists("players"))
        .andExpect(model().attributeExists("invitation"));
    }

    @WithMockUser
    @Test
    public void testAcceptInvitation() throws Exception {
        mockMvc.perform(get("/invitations/"+TEST_INVITATION_ID+"/accept"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    @WithMockUser
    @Test
    public void testRejectInvitation() throws Exception {
        mockMvc.perform(get("/invitations/"+TEST_INVITATION_ID+"/reject"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    @WithMockUser
    @Test
    public void testCancelFriendship() throws Exception {
        mockMvc.perform(get("/invitations/"+TEST_INVITATION_ID+"/cancelFriendship"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/friendsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("friendsInvitations"));
    }

    @WithMockUser
    @Test
    public void testSendGameInvitation() throws Exception {
        mockMvc.perform(get("/gameInvitations/"+TEST_GAME_ID+"/send"))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/sendGameInvitation"))
        .andExpect(model().attributeExists("friends"))
        .andExpect(model().attributeExists("types"))
        .andExpect(model().attributeExists("invitation"));
        
    }

    @WithMockUser
    @Test
    public void testSaveGameInvitation() throws Exception {
        when(gameService.getGameById(anyInt())).thenReturn(game); 
        mockMvc.perform(post("/gameInvitations/{gameId}/send", TEST_GAME_ID)
        .with(csrf())
        .param("recipient", "player1")
        .param("invitationType", "GAME_PLAYER")
        .param("message", "Testing invitations"))
        .andExpect(status().is3xxRedirection());
    }

    @WithMockUser
    @Test
    public void testSaveGameInvitationUnsuccessfulDueToShortMessage() throws Exception {
        mockMvc.perform(post("/gameInvitations/{gameId}/send", TEST_GAME_ID)
        .with(csrf())
        .param("recipient", "player1")
        .param("invitationType", "GAME_PLAYER")
        .param("message", "m"))
        .andExpect(view().name("invitations/sendGameInvitation"))
        .andExpect(model().attributeExists("friends"))
        .andExpect(model().attributeExists("types"))
        .andExpect(model().attributeExists("invitation"));
    }

    @WithMockUser
    @Test
    public void testAcceptGamePlayerInvitation() throws Exception {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(5);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        mockMvc.perform(get("/gameInvitations/{gameId}/{id}/acceptPlayer", TEST_GAME_ID, TEST_INVITATION_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID+"/lobby"));
    }

    @WithMockUser
    @Test
    public void testAcceptGamePlayerInvitationUnsuccessfulDueToPlayerWasAlreadyInTheGame() throws Exception {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(5);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        p1 = new Player();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        when(playerService.getPlayerByUsername(anyString())).thenReturn(p1);
        when(playerInfoService.getAllUsersByGame(any(Game.class))).thenReturn(players);
        mockMvc.perform(get("/gameInvitations/{gameId}/{id}/acceptPlayer", TEST_GAME_ID, TEST_INVITATION_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    @WithMockUser
    @Test
    public void testAcceptGamePlayerInvitationUnsuccessfulDueToMaxNumPlayersReached() throws Exception {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(8);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        mockMvc.perform(get("/gameInvitations/{gameId}/{id}/acceptPlayer", TEST_GAME_ID, TEST_INVITATION_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    @WithMockUser
    @Test
    public void testAcceptGameSpectatorInvitation() throws Exception {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(5);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        mockMvc.perform(get("/gameInvitations/{gameId}/{id}/acceptSpectator", TEST_GAME_ID, TEST_INVITATION_ID))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/games/"+TEST_GAME_ID+"/lobby"));
    }

    @WithMockUser
    @Test
    public void testAcceptGameSpectatorInvitationUnsuccessfulDueToPlayerWasAlreadyInTheGame() throws Exception {
        game = new Game();
        game.setId(TEST_GAME_ID);
        game.setNumPlayers(5);
        when(gameService.getGameById(anyInt())).thenReturn(game);
        p1 = new Player();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        when(playerService.getPlayerByUsername(anyString())).thenReturn(p1);
        when(playerInfoService.getAllUsersByGame(any(Game.class))).thenReturn(players);
        mockMvc.perform(get("/gameInvitations/{gameId}/{id}/acceptSpectator", TEST_GAME_ID, TEST_INVITATION_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("invitations/invitationsList"))
        .andExpect(model().attributeExists("message"))
        .andExpect(model().attributeExists("invitations"))
        .andExpect(model().attributeExists("playerInvitations"))
        .andExpect(model().attributeExists("spectatorInvitations"));
    }

    /* 
    The following MockBeans are necessary to make controller tests work correctly, since we're using the annotation
    "@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)" in "PetclinicApplication.java".
    This anotation is required to access audit information about players, but forces us to create the MockBeans.
    */

    @MockBean
    private PlayerService playerService;

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerInfoService playerInfoService;

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
