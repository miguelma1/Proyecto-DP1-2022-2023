package org.springframework.samples.petclinic.player;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.player.exceptions.DuplicatedUsernameException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/players")
public class PlayerController {

    private PlayerService playerService;

    private static final String PLAYER_REGISTRATION = "/players/playerRegistration";
    private static final String UPDATE_PLAYER_PASSWORD = "/users/updatePlayerPassword";

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

    @InitBinder("player")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PlayerValidator());
	}

    @GetMapping("/register")
    public ModelAndView playerRegistration() {
        ModelAndView res = new ModelAndView(PLAYER_REGISTRATION);
        Player player = new Player();       
        res.addObject("player", player);                                
        return res;
    }

	@PostMapping(value = "/register")
	public String savePlayer(@Valid Player player, BindingResult result) throws DuplicatedUsernameException {
		if (result.hasErrors()) {
            log.error("Input value error");
			return PLAYER_REGISTRATION;
		}
		else {
            try {
                playerService.savePlayer(player);
                log.info("Player created");
                return "redirect:/";
            } catch (DuplicatedUsernameException e) {
                log.warn("Username already exists");
                result.rejectValue("user.username", "This username already exists, please try again", 
                "This username already exists, please try again");
                return PLAYER_REGISTRATION;
            }
		}
	}

    @GetMapping("/edit")
    public ModelAndView editPlayerForm(@AuthenticationPrincipal UserDetails user) {
		ModelAndView res = new ModelAndView(UPDATE_PLAYER_PASSWORD);
        Player player = playerService.getPlayerByUsername(user.getUsername());        
        res.addObject("player", player);
        return res;
    }

	@PostMapping("/edit")
    public ModelAndView editPlayer(@Valid Player player, BindingResult br, @AuthenticationPrincipal UserDetails user) {
        ModelAndView res = new ModelAndView("welcome");
        if (br.hasFieldErrors()) {
            log.error("Input value error");
            return new ModelAndView(UPDATE_PLAYER_PASSWORD, br.getModel());
        }
        Player playerToBeUpdated = playerService.getPlayerByUsername(user.getUsername()); 
        BeanUtils.copyProperties(player, playerToBeUpdated,"id", "online", "playing", "progress");
        playerService.saveEditedPlayer(playerToBeUpdated);
        log.info("Player edited");
        res.addObject("message", "Password changed succesfully!");
        return res;
    }
}
