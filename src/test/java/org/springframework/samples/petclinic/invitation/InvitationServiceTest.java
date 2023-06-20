package org.springframework.samples.petclinic.invitation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.util.Pair;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.invitation.exceptions.DuplicatedInvitationException;
import org.springframework.samples.petclinic.invitation.exceptions.NullInvitationTypeException;
import org.springframework.samples.petclinic.invitation.exceptions.NullRecipientException;
import org.springframework.samples.petclinic.player.Player;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InvitationServiceTest {

    @Mock
    InvitationRepository invitationRepository;

    Player p1;
    Player p2;
    Game game;

    private Invitation createInvitation(Player recipient, String message) {
        Invitation invitation = new Invitation();
        invitation.setRecipient(recipient);
        invitation.setMessage(message);
        return invitation;
    }

    private Invitation createGameInvitation(Player recipient, String message, InvitationType type) {
        Invitation invitation = new Invitation();
        invitation.setInvitationType(type);
        invitation.setRecipient(recipient);
        invitation.setMessage(message);
        return invitation;
    }

    @BeforeEach
    public void config() {
        p1 = new Player();
        p2 = new Player();
        game = new Game();

        Invitation invitation = createInvitation(p2, "Hi, this is a test invitation");
        invitation.setSender(p1);
        invitation.setInvitationType(InvitationType.FRIENDSHIP);
        invitation.setAccepted(true);

        List<Invitation> invitations = new ArrayList<>();
        invitations.add(invitation);
        List<Invitation> noInvitations = new ArrayList<>();

        when(invitationRepository.findInvitationsSentByType(any(Player.class), any(InvitationType.class)))
        .thenReturn(invitations).thenReturn(noInvitations);

        when(invitationRepository.findAllFriendshipInvitations()).thenReturn(invitations);

        invitation.setInvitationType(InvitationType.GAME_PLAYER);
        invitation.setGame(game);
        List<Invitation> gameInvitations = new ArrayList<>();
        gameInvitations.add(invitation);

        when(invitationRepository.findAllGameInvitations()).thenReturn(gameInvitations);
    }

    @Test
    public void testGetFriendsWithFriends() {
        InvitationService service = new InvitationService(invitationRepository);
        List<Player> friends = service.getFriends(p1);
        assertNotNull(friends);
        assertFalse(friends.isEmpty());
    }

    @Test
    public void testGetFriendsWithoutFriends() {
        InvitationService service = new InvitationService(invitationRepository);
        service.getFriends(p1); // aux call to getFriends() in order to get the first return of when(findInvitationsSentByType()) in config()
        List<Player> friends = service.getFriends(p1);
        assertNotNull(friends);
        assertTrue(friends.isEmpty());
    }

    @Test
    public void testGetFriendsInvitationsWithFriends() {
        InvitationService service = new InvitationService(invitationRepository);
        List<Pair<Player, Invitation>> friends = service.getFriendsInvitations(p1);
        assertNotNull(friends);
        assertFalse(friends.isEmpty());
    }

    @Test
    public void testGetFriendsInvitationsWithoutFriends() {
        InvitationService service = new InvitationService(invitationRepository);
        service.getFriendsInvitations(p1); // aux call to getFriendsInvitations() in order to get the first return of when(findInvitationsSentByType()) in config()
        List<Pair<Player, Invitation>> friends = service.getFriendsInvitations(p1);
        assertNotNull(friends);
        assertTrue(friends.isEmpty());
    }
 
    @Test
    public void testSaveInvitationSuccessful() {
        Invitation invitation = createInvitation(new Player(), "Hi, this is a test invitation");
        InvitationService service = new InvitationService(invitationRepository);
        try {
            service.saveInvitation(invitation, new Player());
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testSaveInvitationUnsuccessfulDueToNullRecipient() {
        Invitation invitation = createInvitation(null, "Hi, this is a test invitation");
        InvitationService service = new InvitationService(invitationRepository);
        assertThrows(NullRecipientException.class, () -> service.saveInvitation(invitation, new Player()));
    }

    @Test
    public void testSaveInvitationUnsuccessfulDueToDuplicatedInvitation() {
        Invitation invitation = createInvitation(p1, "Hi, this is a test invitation");
        InvitationService service = new InvitationService(invitationRepository);
        assertThrows(DuplicatedInvitationException.class, () -> service.saveInvitation(invitation, p2));
    }

    @Test
    public void testSaveGameInvitationSuccessful() {
        Invitation invitation = createGameInvitation(new Player(), "Hi, this is a test invitation", InvitationType.GAME_PLAYER);
        InvitationService service = new InvitationService(invitationRepository);
        try {
            service.saveGameInvitation(invitation, p1, new Game());
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testSaveGameInvitationUnsuccessfulDueToNullRecipient() {
        Invitation invitation = createGameInvitation(null, "Hi, this is a test invitation", InvitationType.GAME_SPECTATOR);
        InvitationService service = new InvitationService(invitationRepository);
        assertThrows(NullRecipientException.class, () -> service.saveGameInvitation(invitation, p1, new Game()));
    }

    @Test
    public void testSaveGameInvitationUnsuccessfulDueToNullInvitationType() {
        Invitation invitation = createGameInvitation(p2, "Hi, this is a test invitation", null);
        InvitationService service = new InvitationService(invitationRepository);
        assertThrows(NullInvitationTypeException.class, () -> service.saveGameInvitation(invitation, p1, new Game()));
    }
 
    @Test
    public void testSaveGameInvitationUnsuccessfulDueToDuplicatedInvitation() {
        Invitation invitation = createGameInvitation(p2, "Hi, this is a test invitation", InvitationType.GAME_PLAYER);
        InvitationService service = new InvitationService(invitationRepository);
        assertThrows(DuplicatedInvitationException.class, () -> service.saveGameInvitation(invitation, p1, game));
    }
    
}
