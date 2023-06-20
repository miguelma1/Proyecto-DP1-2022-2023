package org.springframework.samples.petclinic.invitation;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.invitation.exceptions.DuplicatedInvitationException;
import org.springframework.samples.petclinic.invitation.exceptions.NullInvitationTypeException;
import org.springframework.samples.petclinic.invitation.exceptions.NullRecipientException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("")
public class InvitationController {
    
    private static final String INVITATIONS_LIST = "invitations/invitationsList";
    private static final String SEND_INVITATION = "invitations/sendInvitation";
    private static final String SEND_GAME_INVITATION = "invitations/sendGameInvitation";
    private static final String FRIENDS_LIST = "invitations/friendsList";

    private static final Integer MAX_PLAYERS = 8;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerInfoService playerInfoService;

    @Autowired
    public InvitationController(InvitationService iS) {
        this.invitationService = iS;
    }

    @GetMapping("/invitations")
    public ModelAndView showInvitationsByPlayer(@AuthenticationPrincipal UserDetails user){
        ModelAndView result = new ModelAndView(INVITATIONS_LIST);
        Player recipient = playerService.getPlayerByUsername(user.getUsername());
        result.addObject("invitations", invitationService.getFrienshipInvitationsReceived(recipient));
        result.addObject("playerInvitations", invitationService.getValidGameInvitationsReceivedByType(recipient, InvitationType.GAME_PLAYER));
        result.addObject("spectatorInvitations", invitationService.getValidGameInvitationsReceivedByType(recipient, InvitationType.GAME_SPECTATOR));
        return result;
    }

    @GetMapping("/friends")
    public ModelAndView showFriends(@AuthenticationPrincipal UserDetails user) {
        playerService.checkOnlineStatus();
        ModelAndView result = new ModelAndView(FRIENDS_LIST);
        Player recipient = playerService.getPlayerByUsername(user.getUsername());
        result.addObject("friendsInvitations", invitationService.getFriendsInvitations(recipient));
        return result;
    }

    @GetMapping("/invitations/send")
    public ModelAndView sendInvitation(@AuthenticationPrincipal UserDetails user) {
        Invitation invitation = new Invitation();
        Player sender = playerService.getPlayerByUsername(user.getUsername());
        List<Player> players = playerService.getAll();
        List<Player> senderFriends = invitationService.getFriends(sender);
        players.remove(sender);
        players.removeAll(senderFriends);
        ModelAndView result = new ModelAndView(SEND_INVITATION);
        result.addObject("players", players);
        result.addObject("invitation", invitation);
        return result;
    }

    @PostMapping("/invitations/send")
    public ModelAndView saveInvitation(@Valid Invitation invitation, BindingResult br, @AuthenticationPrincipal UserDetails user) throws DuplicatedInvitationException, NullRecipientException {
        ModelAndView result = null;
        Player sender = playerService.getPlayerByUsername(user.getUsername());
        List<Player> players = playerService.getAll();
        List<Player> senderFriends = invitationService.getFriends(sender);
        players.remove(sender);
        players.removeAll(senderFriends);
        if(br.hasErrors()) {
            log.error("Input value error");
            Map<String, Object> map = br.getModel();
            map.put("players", players);
            map.put("invitation", invitation);
            return new ModelAndView(SEND_INVITATION, map);
        } else {
            try {
                invitationService.saveInvitation(invitation, sender);
                log.info("Invitation created");
                result = showInvitationsByPlayer(user);
                result.addObject("message", "Invitation sent successfully!");
            } catch (NullRecipientException e) {
                log.warn("Recipient not selected");
                result = new ModelAndView(SEND_INVITATION);
                result.addObject("players", players);
                result.addObject("invitation", invitation);
                result.addObject("message", "Please, select the player who you want to invite");
                return result;
            } catch (DuplicatedInvitationException e) {
                log.warn("Duplicated invitation");
                result = new ModelAndView(SEND_INVITATION);
                result.addObject("players", players);
                result.addObject("invitation", invitation);
                result.addObject("message", "An invitation between you and that player already exists!");
                return result;
            }
        }
        return result;
    }

    @GetMapping("/invitations/{id}/accept")
    public ModelAndView acceptInvitation(@PathVariable Integer id, @AuthenticationPrincipal UserDetails user, ModelMap model) {
        invitationService.acceptInvitationById(id);
        log.info("Invitation accepted"); 
        model.put("message", "Invitation accepted successfully!");
        return showInvitationsByPlayer(user);
    }

    @GetMapping("/invitations/{id}/reject")
    public ModelAndView rejectInvitation(@PathVariable Integer id, @AuthenticationPrincipal UserDetails user, ModelMap model) {
        try{
            invitationService.rejectInvitationById(id);
            log.info("Invitation deleted"); 
            model.put("message", "Invitation rejected successfully!");     
        } catch(EmptyResultDataAccessException e) {
            log.warn("Not existing invitation");
            model.put("message", "Invitation " + id + " does not exist");
        }
        return showInvitationsByPlayer(user);
    }

    @GetMapping("/invitations/{id}/cancelFriendship")
    public ModelAndView cancelFriendship(@PathVariable Integer id, @AuthenticationPrincipal UserDetails user, ModelMap model) {
        try{
            invitationService.rejectInvitationById(id);
            log.info("Invitation deleted");   
            model.put("message", "Friendship cancelled successfully!");     
        } catch(EmptyResultDataAccessException e) {
            log.warn("Not existing invitation");
            model.put("message", "That's not your friend");
        }
        return showFriends(user);
    }

    @GetMapping("/gameInvitations/{gameId}/send")
    public ModelAndView sendGameInvitation(@PathVariable("gameId") Integer gameId, @AuthenticationPrincipal UserDetails user) {
        Invitation invitation = new Invitation();
        Player sender = playerService.getPlayerByUsername(user.getUsername());
        List<Player> friends = invitationService.getFriends(sender);
        Game game = gameService.getGameById(gameId);
        ModelAndView result = new ModelAndView(SEND_GAME_INVITATION);
        result.addObject("friends", friends);
        result.addObject("types", invitationService.getGameInvitationTypes());
        result.addObject("invitation", invitation);
        result.addObject("game", game);
        return result;
    }

    @PostMapping("/gameInvitations/{gameId}/send")
    public String saveGameInvitation(@Valid Invitation invitation, BindingResult br, @PathVariable("gameId") Integer gameId, ModelMap model, @AuthenticationPrincipal UserDetails user) throws DuplicatedInvitationException, NullRecipientException, NullInvitationTypeException {
        Player sender = playerService.getPlayerByUsername(user.getUsername());
        List<Player> friends = invitationService.getFriends(sender);
        Game game = gameService.getGameById(gameId);
        List<String> types = invitationService.getGameInvitationTypes();
        if(br.hasErrors()) {
            log.error("Input value error");
            model.put("friends", friends);
            model.put("types", types);
            model.put("invitation", invitation);
            model.put("game", game);
            return SEND_GAME_INVITATION;
        } else {
            try {
                invitationService.saveGameInvitation(invitation, sender, game);
                log.info("Invitation created");
                model.put("message", "Invitation sent successfully!");
                return "redirect:/games/" + gameId.toString() + "/lobby";
            } catch (NullRecipientException e) {
                log.warn("Recipient not selected");
                model.put("friends", friends);
                model.put("types", types);
                model.put("invitation", invitation);
                model.put("game", game);
                model.put("message", "Please, select the friend who you want to invite");
                return SEND_GAME_INVITATION;
            } catch (NullInvitationTypeException e) {
                log.warn("Invitation type not selected");
                model.put("friends", friends);
                model.put("types", types);
                model.put("invitation", invitation);
                model.put("game", game);
                model.put("message", "Please, select the invitation type");
                return SEND_GAME_INVITATION;
            } catch (DuplicatedInvitationException e) {
                log.warn("Duplicated invitation");
                model.put("friends", friends);
                model.put("types", types);
                model.put("invitation", invitation);
                model.put("game", game);
                model.put("message", "You have already invited this friend!");
            return SEND_GAME_INVITATION;
            }
        }
    }

    @GetMapping("/gameInvitations/{gameId}/{id}/acceptPlayer")
    public String acceptGamePlayerInvitation(@PathVariable Integer gameId, @PathVariable Integer id, @AuthenticationPrincipal UserDetails user, ModelMap model) {
        Game game = gameService.getGameById(gameId);
        Player player=playerService.getPlayerByUsername(user.getUsername());
        if(playerInfoService.getAllUsersByGame(game).contains(player)) {
			log.warn("Player was already in the game");
			model.put("message", "You are already in this game!");
			model.put("invitations", invitationService.getFrienshipInvitationsReceived(player));
            model.put("playerInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_PLAYER));
            model.put("spectatorInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_SPECTATOR));
			return INVITATIONS_LIST;
		}
        if(game.getNumPlayers() == MAX_PLAYERS) {
			model.put("message", "This game has reached the maximum number of players!");
            model.put("invitations", invitationService.getFrienshipInvitationsReceived(player));
            model.put("playerInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_PLAYER));
            model.put("spectatorInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_SPECTATOR));
			return INVITATIONS_LIST;
		}
        invitationService.acceptInvitationById(id);
        log.info("Invitation accepted");
        gameService.joinGame(game);
		playerInfoService.savePlayerInfo(new PlayerInfo(), game, player);
		log.info("Player joined"); 
        return "redirect:/games/" + game.getId().toString() + "/lobby";
    }

    @GetMapping("/gameInvitations/{gameId}/{id}/acceptSpectator")
    public String acceptGameSpectatorInvitation(@PathVariable Integer gameId, @PathVariable Integer id, @AuthenticationPrincipal UserDetails user, ModelMap model) {
        Game game = gameService.getGameById(gameId);
        Player player=playerService.getPlayerByUsername(user.getUsername());
        if(playerInfoService.getAllUsersByGame(game).contains(player)) {
			log.warn("Player was already in the game");
			model.put("message", "You are already in this game!");
			model.put("invitations", invitationService.getFrienshipInvitationsReceived(player));
            model.put("playerInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_PLAYER));
            model.put("spectatorInvitations", invitationService.getValidGameInvitationsReceivedByType(player, InvitationType.GAME_SPECTATOR));
			return INVITATIONS_LIST;
		}
        invitationService.acceptInvitationById(id);
        log.info("Invitation accepted");
		playerInfoService.saveSpectatorInfo(new PlayerInfo(), game, player);
		log.info("Spectator joined");
        return "redirect:/games/" + game.getId().toString() + "/lobby";
    }

    



}
