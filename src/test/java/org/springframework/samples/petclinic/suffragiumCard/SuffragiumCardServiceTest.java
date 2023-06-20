package org.springframework.samples.petclinic.suffragiumCard;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.turn.Turn;

@ExtendWith(MockitoExtension.class)
public class SuffragiumCardServiceTest {

    @Mock
    SuffragiumCardRepository repo;

    private Game createGame(String name, Boolean publicGame) {
        Game game = new Game();
        game.setName(name);
        game.setPublicGame(publicGame);
        return game;
    }

    public Turn createTurn(Integer votesLoyal, Integer votesTraitor, Integer votesNeutral) {
        Turn turn = new Turn();
        turn.setCurrentTurn(1);
        turn.setVotesLoyal(votesLoyal);
        turn.setVotesTraitor(votesTraitor);
        turn.setVotesNeutral(votesNeutral);
        return turn;
    }

    private SuffragiumCard createSuffragiumCard(Integer loyalsVotes, Integer traitorsVotes, Integer voteLimit) {
        SuffragiumCard card = new SuffragiumCard();
        card.setLoyalsVotes(loyalsVotes);
        card.setTraitorsVotes(traitorsVotes);
        card.setVoteLimit(voteLimit);
        return card;
    }
/* 
    @Test
    @Disabled
    public void testUpdateVotes() {
        SuffragiumCard suffragiumCard = createSuffragiumCard(0, 0, 15);
        Turn turn = createTurn(1, 1, 0);
        SuffragiumCardService service = new SuffragiumCardService(repo);
        try {
            service.updateVotes(suffragiumCard, turn);
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }*/
    
}
