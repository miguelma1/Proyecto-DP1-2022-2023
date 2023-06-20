package org.springframework.samples.petclinic.invitation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.invitation.exceptions.DuplicatedInvitationException;
import org.springframework.samples.petclinic.invitation.exceptions.NullInvitationTypeException;
import org.springframework.samples.petclinic.invitation.exceptions.NullRecipientException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationService {

    private InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(InvitationRepository repo) {
        this.invitationRepository = repo;
    }

    @Transactional(readOnly = true)
    public Invitation getById(Integer id) {
        return invitationRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public List<Invitation> getFrienshipInvitationsReceived(Player recipient) {
        return invitationRepository.findFriendshipInvitationsReceived(recipient);
    }

    @Transactional(readOnly = true)
    public List<Invitation> getValidGameInvitationsReceivedByType(Player recipient, InvitationType type) {
        return invitationRepository.findValidGameInvitationsReceivedByType(recipient, type);
    }

    @Transactional(readOnly = true)
    public List<Player> getFriends(Player player) {
        List<Player> res = new ArrayList<>();
        List<Invitation> invitationsReceived = invitationRepository.findFriendshipInvitationsReceived(player);
        List<Invitation> invitationsSent = invitationRepository.findInvitationsSentByType(player, InvitationType.FRIENDSHIP);
        for(Invitation i:invitationsReceived) {
            if(i.getAccepted()) {
                res.add(i.getSender());
            }
        }
        for(Invitation i:invitationsSent) {
            if(i.getAccepted()) {
                res.add(i.getRecipient());
            }
        }
        return res;
    }

    @Transactional(readOnly = true)
    public List<Player> getFriendsOnline(Player player) {
        return getFriends(player).stream().filter(f -> f.getOnline() == true).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Pair<Player, Invitation>> getFriendsInvitations(Player player) {
        List<Pair<Player, Invitation>> res = new ArrayList<>();
        List<Invitation> invitationsReceived = invitationRepository.findFriendshipInvitationsReceived(player);
        List<Invitation> invitationsSent = invitationRepository.findInvitationsSentByType(player, InvitationType.FRIENDSHIP);
        for(Invitation i:invitationsReceived) {
            if(i.getAccepted()) {
                res.add(Pair.of(i.getSender(), i));
            }
        }
        for(Invitation i:invitationsSent) {
            if(i.getAccepted()) {
                res.add(Pair.of(i.getRecipient(), i));
            }
        }
        return res;
    }

    @Transactional(readOnly = true) 
    public Boolean invitationIsDuplicated(Invitation invitation, Player sender) {
        Boolean res = false;
        Player p1 = sender;
        Player p2 = invitation.getRecipient();
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        List<Invitation> invitationsInDB = invitationRepository.findAllFriendshipInvitations();
        for(Invitation i: invitationsInDB) {
            Player p1InDB = i.getSender();
            Player p2InDB = i.getRecipient();
            if(players.contains(p1InDB) && players.contains(p2InDB)) res = true;
        }
        return res;
    }

    @Transactional(rollbackFor = {DuplicatedInvitationException.class, NullRecipientException.class})
	public void saveInvitation(Invitation invitation, Player sender) throws DuplicatedInvitationException, NullRecipientException {
        if(invitation.getRecipient() == null) {
            throw new NullRecipientException();
        } else if(invitationIsDuplicated(invitation, sender)) {
            throw new DuplicatedInvitationException();
        } else {
            invitation.setInvitationType(InvitationType.FRIENDSHIP);
            invitation.setAccepted(false);
            invitation.setSender(sender);
            invitationRepository.save(invitation);
        }
	}

    @Transactional
    public void acceptInvitationById(Integer id) throws DataAccessException {
        Invitation i = getById(id);
        i.setAccepted(true);
        invitationRepository.save(i);
    }

    @Transactional
    public void rejectInvitationById(Integer id) throws DataAccessException {
        invitationRepository.deleteById(id);
    }

    public List<String> getGameInvitationTypes() {
        List<String> types = new ArrayList<>();
        types.add("GAME_PLAYER");
        types.add("GAME_SPECTATOR");
        return types;
    }

    @Transactional(readOnly = true) 
    public Boolean gameInvitationIsDuplicated(Invitation invitation, Player sender, Game game) {
        Boolean res = false;
        Player recipient = invitation.getRecipient();
        List<Invitation> invitationsInDB = invitationRepository.findAllGameInvitations();
        for(Invitation i: invitationsInDB) {
            Player senderInDB = i.getSender();
            Player recipientInDB = i.getRecipient();
            Game gameInDB = i.getGame();
            if(sender == senderInDB && recipient == recipientInDB && game == gameInDB) res = true;
        }
        return res;
    }

    @Transactional(rollbackFor = {DuplicatedInvitationException.class, NullRecipientException.class, NullInvitationTypeException.class})
	public void saveGameInvitation(Invitation invitation, Player sender, Game game) throws DuplicatedInvitationException, NullRecipientException, NullInvitationTypeException {
        if(invitation.getRecipient() == null) {
            throw new NullRecipientException();
        } else if(invitation.getInvitationType() == null) {
            throw new NullInvitationTypeException();
        } else if(gameInvitationIsDuplicated(invitation, sender, game)) {
            throw new DuplicatedInvitationException();
        } else {
            invitation.setAccepted(false);
            invitation.setSender(sender);
            invitation.setGame(game);
            invitationRepository.save(invitation);
        }
	}
    
}
