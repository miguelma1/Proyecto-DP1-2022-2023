package org.springframework.samples.petclinic.playerInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class PlayerInfoRepositoryTest {

    @Autowired
    private PlayerInfoRepository playerInfoRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testFindPlayerInfosByGame() {
        Game game = gameRepository.findById(2);
        List<PlayerInfo> playerInfos = playerInfoRepository.findPlayerInfosByGame(game);
        assertNotNull(playerInfos);
        assertFalse(playerInfos.isEmpty());
    }

    @Test
    public void testPlayerInfoByGameAndPlayer() {
        Game game = gameRepository.findById(4);
        Player player = playerRepository.findPlayerByUsername("alvgonfri");
        PlayerInfo playerInfo = playerInfoRepository.findPlayerInfoByGameAndPlayer(game, player);
        assertNotNull(playerInfo);
        assertTrue(playerInfo.getGame() == game);
        assertTrue(playerInfo.getPlayer() == player);
    }

    @Test
    public void testFindGamesByPlayer() {
        Player player = playerRepository.findPlayerByUsername("alvgonfri");
        List<Game> games = playerInfoRepository.findGamesByPlayer(player);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testFindGamesByPlayerWithoutGames() {
        Player player = playerRepository.findPlayerByUsername("player9");
        List<Game> games = playerInfoRepository.findGamesByPlayer(player);
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testFindGamesInProcessByPlayer() {
        Player player = playerRepository.findPlayerByUsername("alvgonfri");
        List<Game> games = playerInfoRepository.findGamesInProcessByPlayer(player);
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    public void testFindPlayersByGame() {
        Game game = gameRepository.findById(2);
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        assertNotNull(players);
        assertFalse(players.isEmpty());
    }

    @Test
    public void testFindAllUsersByGame() {
        Game game = gameRepository.findById(2);
        List<Player> players = playerInfoRepository.findAllUsersByGame(game);
        assertNotNull(players);
        assertFalse(players.isEmpty());
    }
    
}
