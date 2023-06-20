package org.springframework.samples.petclinic.deck;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Integer> {
    List<Deck> findAll();

    @Query("SELECT d FROM Deck d WHERE d.player.id LIKE :id")
	public List<Deck> findPlayerDecks(@Param("id") Integer playerId);

    @Query("SELECT d FROM Deck d WHERE d.player=:player AND d.game=:game")
    public Deck findDeckByPlayerAndGame(@Param("player") Player player, @Param("game") Game game);
    
    
}
