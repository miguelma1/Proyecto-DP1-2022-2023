package org.springframework.samples.petclinic.player;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class PlayerFormatter implements Formatter<Player> {

    private final PlayerService playerService;

    @Autowired
    public PlayerFormatter(PlayerService service) {
        this.playerService = service;
    }

    @Override
	public String print(Player player, Locale locale) {
		return player.getUser().getUsername();
	}

    @Override
	public Player parse(String text, Locale locale) throws ParseException {
		List<Player> players = this.playerService.getAll();
		for (Player p : players) {
			if (p.getUser().getUsername().equals(text)) {
				return p;
			}
		}
		throw new ParseException("player not found: " + text, 0);
    }
}
