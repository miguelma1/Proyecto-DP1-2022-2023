package org.springframework.samples.petclinic.progress;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends CrudRepository<Progress, Integer> {
    List<Progress> findAll();

    @Query("SELECT p FROM Progress p where p.player LIKE :player")
    List<Progress> findProgressByPlayer(@Param("player") Player player);
}
