package org.springframework.samples.petclinic.playerInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.player.Player;

@ExtendWith(MockitoExtension.class)
public class PlayerInfoServiceTest {

    @Mock
    PlayerInfoRepository repo;

    private PlayerInfo createPlayerInfo(Boolean creator, Boolean spectator, Game game, Player player) {
        PlayerInfo creatorInfo = new PlayerInfo();
        creatorInfo.setCreator(creator);
        creatorInfo.setSpectator(spectator);
        creatorInfo.setGame(game);
        return creatorInfo;
    }

    private Game createGame(String name, Boolean publicGame) {
        Game game = new Game();
        game.setName(name);
        game.setPublicGame(publicGame);
        return game;
    }

    private Player createPlayer() {
        Player player = new Player();
        return player;
    }

    @Test
    public void testGetActivePlayersPlayerInfosByGame() {
        Game game = createGame("Test game", true);
        PlayerInfo playerInfo = createPlayerInfo(false, false, game, null);
        List<PlayerInfo> playerInfos = new ArrayList<>();
        playerInfos.add(playerInfo);
        when(repo.findPlayerInfosByGame(any(Game.class))).thenReturn(playerInfos);
        PlayerInfoService service = new PlayerInfoService(repo);
        List<PlayerInfo> res = service.getActivePlayersPlayerInfosByGame(game);
        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testIsSpectator() {
        Game game = createGame("Test game", true);
        Player player = createPlayer();
        when(repo.findPlayersByGame(game)).thenReturn(new ArrayList<>());
        PlayerInfoService service = new PlayerInfoService(repo);
        Boolean res = service.isSpectator(player, game);
        assertTrue(res);
    }

    @Test
    public void testSaveCreatorInfo() {
        Game game = createGame("Test game", true);
        Player player = createPlayer();
        PlayerInfoService service = new PlayerInfoService(repo);
        try {
            service.saveCreatorInfo(new PlayerInfo(), game, player);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testSavePlayerInfo() {
        Game game = createGame("Test game", true);
        Player player = createPlayer();
        PlayerInfoService service = new PlayerInfoService(repo);
        try {
            service.savePlayerInfo(new PlayerInfo(), game, player);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testSaveSpectatorInfo() {
        Game game = createGame("Test game", true);
        Player player = createPlayer();
        PlayerInfoService service = new PlayerInfoService(repo);
        try {
            service.saveSpectatorInfo(new PlayerInfo(), game, player);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }
   
}
