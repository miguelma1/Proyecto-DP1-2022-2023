package org.springframework.samples.petclinic.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.samples.petclinic.user.User;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PlayerFormatterTest {
    
    @Mock
    private PlayerService playerService;

    private PlayerFormatter playerFormatter;

    @BeforeEach
    public void config() {
        playerFormatter = new PlayerFormatter(playerService);
        User user = new User();
        user.setUsername("Test player");
        Player player = new Player();
        player.setUser(user);
        List<Player> players = new ArrayList<>();
        players.add(player);
        when(playerService.getAll()).thenReturn(players);
    }

    @Test
    public void testPrint() {
        User user = new User();
        user.setUsername("Player to print");
        Player player = new Player();
        player.setUser(user);
        String playerUsername = playerFormatter.print(player, Locale.ENGLISH);
        assertEquals("Player to print", playerUsername);
    }

    @Test
    public void testParse() throws ParseException {
        Player player = playerFormatter.parse("Test player", Locale.ENGLISH);
        assertEquals("Test player", player.getUser().getUsername());
    }

    @Test
    public void testParseThrowsException() throws ParseException {
        assertThrows(ParseException.class, () -> playerFormatter.parse("player1", Locale.ENGLISH));
    }

}
