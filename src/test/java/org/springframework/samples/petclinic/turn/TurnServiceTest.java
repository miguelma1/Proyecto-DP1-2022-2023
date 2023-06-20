package org.springframework.samples.petclinic.turn;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.game.GameService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TurnServiceTest {

    @Mock
    TurnRepository turnRepository;

    @Mock
    GameRepository gameRepository;

    Turn turn;
    Game game;
    Integer originalLoyalVotes;
    Integer originalTraitorVotes;
    Integer originalNeutralVotes;
    Integer originalTurn;

    private Turn createTurn(Integer currentTurn, Integer votesLoyal, Integer votesTraitor, Integer votesNeutral) {
        Turn turn = new Turn();
        turn.setCurrentTurn(currentTurn);
        turn.setVotesLoyal(votesLoyal);
        turn.setVotesTraitor(votesTraitor);
        turn.setVotesNeutral(votesNeutral);
        return turn;
    }

    private Game createGame(String name, Boolean publicGame) {
        Game game = new Game();
        game.setName(name);
        game.setPublicGame(publicGame);
        game.setState(State.IN_PROCESS);
        game.setNumPlayers(5);
        game.setRound(CurrentRound.FIRST);
        game.setTurn(turn);
        return game;
    }

    @BeforeEach
    public void config() {
        turn = createTurn(4, 0, 0, 0);
        game = createGame("Testing game", true);
        game.setTurn(turn);
        originalTurn = turn.getCurrentTurn();
        originalLoyalVotes = turn.getVotesLoyal();
        originalTraitorVotes = turn.getVotesTraitor();
        originalNeutralVotes = turn.getVotesNeutral();

        when(turnRepository.findGameByTurn(any(Turn.class))).thenReturn(game);
    }

    @Test
    public void testSaveTurnSuccessful() {
        TurnService turnService = new TurnService(turnRepository, gameRepository);
        try {
            turnService.save(turn);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testUpdateTurnVotes () { //funciona
        
        TurnService turnService = new TurnService(turnRepository, gameRepository);

        turnService.updateTurnVotes(turn, VCType.YELLOW);
        assertTrue(turn.getVotesLoyal() == originalLoyalVotes);
        assertTrue(turn.getVotesTraitor() == originalTraitorVotes);
        assertTrue(turn.getVotesNeutral() == originalNeutralVotes + 1);

        turnService.updateTurnVotes(turn, VCType.GREEN);
        assertTrue(turn.getVotesLoyal() == originalLoyalVotes + 1);
        assertTrue(turn.getVotesTraitor() == originalTraitorVotes);
        assertTrue(turn.getVotesNeutral() == originalNeutralVotes + 1);

        turnService.updateTurnVotes(turn, VCType.RED);
        assertTrue(turn.getVotesLoyal() == originalLoyalVotes + 1);
        assertTrue(turn.getVotesTraitor() == originalTraitorVotes + 1);
        assertTrue(turn.getVotesNeutral() == originalNeutralVotes + 1);        
    }

    @Test
    public void testNewTurn() { //no sale
        TurnService turnService = new TurnService(turnRepository, gameRepository);
        turnService.newTurn(turn);
        assertTrue(turn.getCurrentTurn() == originalTurn + 1);
        assertTrue(game.getRound() == CurrentRound.FIRST);
        turnService.newTurn(turn);
        assertTrue(turn.getCurrentTurn() == 1);
        assertTrue(game.getRound() == CurrentRound.SECOND);

    }

    @Test
    public void testPretorVoteChange() { //funciona
        
        TurnService turnService = new TurnService(turnRepository, gameRepository);

        turnService.pretorVoteChange(VCType.GREEN, VCType.GREEN, game);
        assertTrue(originalLoyalVotes == game.getTurn().getVotesLoyal());
        assertTrue(originalTraitorVotes == game.getTurn().getVotesTraitor());

        turnService.pretorVoteChange(VCType.RED, VCType.RED, game);
        assertTrue(originalLoyalVotes == game.getTurn().getVotesLoyal());
        assertTrue(originalTraitorVotes == game.getTurn().getVotesTraitor());

        turnService.pretorVoteChange(VCType.RED, VCType.GREEN, game);
        assertTrue(originalLoyalVotes == game.getTurn().getVotesLoyal() - 1);
        assertTrue(originalTraitorVotes == game.getTurn().getVotesTraitor() + 1);

        turn = createTurn(3, 0, 0, 0);
        game = createGame("Testing game", true);
        game.setTurn(turn);

        turnService.pretorVoteChange(VCType.GREEN, VCType.RED, game);
        assertTrue(originalLoyalVotes == game.getTurn().getVotesLoyal() + 1);
        assertTrue(originalTraitorVotes == game.getTurn().getVotesTraitor() - 1);

    }
    
}
