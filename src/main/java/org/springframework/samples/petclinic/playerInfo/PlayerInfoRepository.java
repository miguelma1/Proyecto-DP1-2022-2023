package org.springframework.samples.petclinic.playerInfo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerInfoRepository extends CrudRepository<PlayerInfo,Integer>{

    List<PlayerInfo> findAll(); 

    void deleteById(Integer id);

    @Query("SELECT pI FROM PlayerInfo pI WHERE pI.game =?1")
	public List<PlayerInfo> findPlayerInfosByGame(@Param("game") Game game);

    @Query("SELECT pI FROM PlayerInfo pI WHERE pI.game =?1 AND pI.player =?2")
	public PlayerInfo findPlayerInfoByGameAndPlayer(@Param("game") Game game, @Param("player") Player player);

    @Query("SELECT pI.game FROM PlayerInfo pI WHERE pI.player =?1")
    public List<Game> findGamesByPlayer(@Param("player") Player player);

    @Query("SELECT pI.game FROM PlayerInfo pI WHERE pI.player =?1 AND pI.game.state = 'IN_PROCESS'")
    public List<Game> findGamesInProcessByPlayer(@Param("player") Player player);

    @Query("SELECT pI.player FROM PlayerInfo pI WHERE pI.game =?1 AND pI.spectator=0")
	public List<Player> findPlayersByGame(@Param("game") Game game);

    @Query("SELECT pI.player FROM PlayerInfo pI WHERE pI.game =?1")
	public List<Player> findAllUsersByGame(@Param("game") Game game);

}
