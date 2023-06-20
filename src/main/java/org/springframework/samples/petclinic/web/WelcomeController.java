package org.springframework.samples.petclinic.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	@Autowired
	private PlayerService playerService;

	@Autowired
	private UserService userService;

	@Autowired
	private InvitationService invitationService;
	
	  @GetMapping({"/","/welcome"})
	  public String welcome(Map<String, Object> model, @AuthenticationPrincipal UserDetails userDetails) {
		playerService.checkOnlineStatus();
		if(userDetails == null) {
			model.put("numFriendsOnline", 0);
			return "welcome";
		}
		User user = userService.getUserByUsername(userDetails.getUsername());
		model.put("user", user);
		if(userService.getUsersWithAuthority("player").contains(user)) {
			Player player = playerService.getPlayerByUsername(userDetails.getUsername());
			model.put("numFriendsOnline", invitationService.getFriendsOnline(player).size());
		} else {
			model.put("numFriendsOnline", 0);
		}
	    return "welcome";
	  }
}
