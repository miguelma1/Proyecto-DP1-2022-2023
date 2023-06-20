package org.springframework.samples.petclinic.player;

import org.springframework.samples.petclinic.user.User;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PlayerValidator implements Validator {

	@Override
	public void validate(Object obj, Errors errors) {
		Player player = (Player) obj;
        User user = player.getUser();

        if(!StringUtils.hasLength(user.getUsername()) || user.getUsername().length()>30 || user.getUsername().length()<3) {
            errors.rejectValue("user.username", "Your username must have between 3 and 30 characters", 
            "Your username must have between 3 and 30 characters");
        }
        
        if(!StringUtils.hasLength(user.getPassword()) || user.getPassword().length()<3) {
            errors.rejectValue("user.password", "Your password is too short, 3 characters are required at least", 
            "Your password is too short, 3 characters are required at least");
        }		

	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Player.class.isAssignableFrom(clazz);
	}

}

