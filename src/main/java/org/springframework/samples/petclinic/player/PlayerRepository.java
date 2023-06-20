package org.springframework.samples.petclinic.player;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer>, RevisionRepository<Player, Integer, Integer> {

    List<Player> findAll(); 

    @Query("SELECT p FROM Player p")
    public List<Player> findAllPageable(Pageable pageable);

    @Query("SELECT p FROM Player p WHERE p.user.username = ?1")
    public Player findPlayerByUsername(@Param("username") String username);

    @Query("SELECT p.user.username FROM Player p")
    public List<String> findAllUsernames();
    
}
