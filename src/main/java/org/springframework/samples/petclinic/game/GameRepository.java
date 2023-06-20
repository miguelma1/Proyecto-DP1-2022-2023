package org.springframework.samples.petclinic.game;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long>{
    
    List<Game> findAll(); 

    @Query("SELECT DISTINCT g FROM Game g WHERE g.id = ?1")
	public Game findById(@Param("id") Integer id);

    @Query("SELECT DISTINCT g FROM Game g WHERE g.name LIKE :name%")
	public List<Game> findByName(@Param("name") String name);

    @Query("SELECT g FROM Game g WHERE g.state = :state")
    public List<Game> findByState(@Param("state") State state);
    
    @Query("SELECT DISTINCT g FROM Game g WHERE g.publicGame = 1 AND g.name LIKE :name%")
	public List<Game> findPublicGamesByName(@Param("name") String name);

    @Query("SELECT DISTINCT g FROM Game g WHERE g.publicGame = 0 AND g.name LIKE :name%")
	public List<Game> findPrivateGamesByName(@Param("name") String name);

}
