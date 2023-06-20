package org.springframework.samples.petclinic.suffragiumCard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.stereotype.Repository;

@Repository
public interface SuffragiumCardRepository extends CrudRepository<SuffragiumCard, Integer> {

    @Query("SELECT g.suffragiumCard FROM Game g WHERE g.id =?1")
	public SuffragiumCard findSuffragiumCardByGame(@Param("gameId") Integer gameId);

    @Query("SELECT g FROM Game g WHERE g.suffragiumCard =?1")
	public Game findGameBySuffragiumCard(@Param("suffragiumCard") SuffragiumCard suffragiumCard);
    
}
