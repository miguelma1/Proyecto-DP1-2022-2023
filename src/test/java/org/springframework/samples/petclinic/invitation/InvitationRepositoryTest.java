package org.springframework.samples.petclinic.invitation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;

@DataJpaTest
public class InvitationRepositoryTest {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testFindAllFrienshipInvitations() {
        List<Invitation> invitations = invitationRepository.findAllFriendshipInvitations();
        assertNotNull(invitations);
        assertFalse(invitations.isEmpty());
    }

    @Test
    public void testFindAllGameInvitations() {
        List<Invitation> invitations = invitationRepository.findAllGameInvitations();
        assertNotNull(invitations);
        assertFalse(invitations.isEmpty());
    }
 
    @Test
    public void testFindFriendshipInvitationsReceivedWithInvitations() {
        Player p = playerRepository.findPlayerByUsername("alvgonfri");
        List<Invitation> invitations = invitationRepository.findFriendshipInvitationsReceived(p);
        assertNotNull(invitations);
        assertFalse(invitations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"player1", "CristianoRonaldo_7"}) // Existing player and not-existing player
    public void testFindFriendshipInvitationsReceivedWithoutInvitations(String username) {
        Player p = playerRepository.findPlayerByUsername(username);
        List<Invitation> invitations = invitationRepository.findFriendshipInvitationsReceived(p);
        assertNotNull(invitations);
        assertTrue(invitations.isEmpty());
    }

    @Test
    public void testFindValidGameInvitationsReceivedByTypeWithInvitations() {
        Player p = playerRepository.findPlayerByUsername("player5");
        List<Invitation> invitations = invitationRepository.findValidGameInvitationsReceivedByType(p, InvitationType.GAME_PLAYER);
        assertNotNull(invitations);
        assertFalse(invitations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"player1", "CristianoRonaldo_7"}) // Existing player and not-existing player
    public void testFindValidGameInvitationsReceivedByTypeWithoutInvitations(String username) {
        Player p = playerRepository.findPlayerByUsername(username);
        List<Invitation> invitations = invitationRepository.findValidGameInvitationsReceivedByType(p, InvitationType.GAME_PLAYER);
        assertNotNull(invitations);
        assertTrue(invitations.isEmpty());
    }
 
    @Test
    public void testFindInvitationsSentByTypeWithInvitations() {
        Player p = playerRepository.findPlayerByUsername("alvgonfri");
        List<Invitation> invitations = invitationRepository.findInvitationsSentByType(p, InvitationType.FRIENDSHIP);
        assertNotNull(invitations);
        assertFalse(invitations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"player1", "CristianoRonaldo_7"}) // Existing player and not-existing player
    public void testFindInvitationsSenByTypeWithoutInvitations(String username) {
        Player p = playerRepository.findPlayerByUsername(username);
        List<Invitation> invitations = invitationRepository.findInvitationsSentByType(p, InvitationType.GAME_SPECTATOR);
        assertNotNull(invitations);
        assertTrue(invitations.isEmpty());
    }
    
}
