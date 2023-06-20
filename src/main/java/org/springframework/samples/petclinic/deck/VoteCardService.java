package org.springframework.samples.petclinic.deck;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteCardService {

    @Autowired
    VoteCardRepository voteCardRepository;

    @Autowired
    GameRepository gameRepository ;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    public VoteCardService (VoteCardRepository voteCardRepository) {
        this.voteCardRepository = voteCardRepository;
    }

    @Transactional(readOnly = true)
    public List<VoteCard> getAll() {
        return voteCardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public VoteCard getById (VCType type) {
        return voteCardRepository.findById(type).get();
    }

    @Transactional
    public List<VoteCard> getChangeOptions(Game game, VoteCard selectedCard) {
        List<VoteCard> res = voteCardRepository.findAll();
        if (game.getRound() == CurrentRound.FIRST) {
            res.remove(selectedCard);
            res.remove(voteCardRepository.findById(VCType.YELLOW).get());
        }
        if (game.getRound() == CurrentRound.SECOND) {
            res.remove(selectedCard);
        }
        return res;
    }

    @Transactional
    public void forcedVoteChange(Game game, Player voter) {
        Deck voterDeck = deckRepository.findDeckByPlayerAndGame(voter, game);
        List<VoteCard> newVotes = new ArrayList<>();
       
        newVotes.add(voteCardRepository.findById(VCType.RED).get());
        newVotes.add(voteCardRepository.findById(VCType.GREEN).get());

        voterDeck.setVoteCards(newVotes);
        game.setStage(CurrentStage.VOTING);

        deckRepository.save(voterDeck);    
        gameRepository.save(game);
    }

}
