package org.springframework.samples.petclinic.invitation;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.player.Player;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Integer> {

    List<Invitation> findAll();

    @Query("SELECT i FROM Invitation i WHERE i.invitationType = 'FRIENDSHIP'")
    public List<Invitation> findAllFriendshipInvitations();

    @Query("SELECT i FROM Invitation i WHERE i.invitationType = 'GAME_PLAYER' OR i.invitationType = 'GAME_SPECTATOR'")
    public List<Invitation> findAllGameInvitations();
    
    @Query("SELECT i FROM Invitation i WHERE i.recipient=:recipient AND i.invitationType = 'FRIENDSHIP'")
    public List<Invitation> findFriendshipInvitationsReceived(@Param("recipient") Player recipient);

    @Query("SELECT i FROM Invitation i WHERE i.recipient=:recipient AND i.invitationType=:type AND i.game.state = 'STARTING'")
    public List<Invitation> findValidGameInvitationsReceivedByType(@Param("recipient") Player recipient, @Param("type") InvitationType type);

    @Query("SELECT i FROM Invitation i WHERE i.sender=:sender AND i.invitationType=:type")
    public List<Invitation> findInvitationsSentByType(@Param("sender") Player sender, @Param("type") InvitationType type);

    

}
