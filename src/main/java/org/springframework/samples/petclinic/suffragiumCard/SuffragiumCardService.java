package org.springframework.samples.petclinic.suffragiumCard;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuffragiumCardService {

    private SuffragiumCardRepository suffragiumCardRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    PlayerInfoRepository playerInfoRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameService gameService;

    @Autowired
    public SuffragiumCardService(SuffragiumCardRepository repo) {
        this.suffragiumCardRepository = repo;
    }

    @Transactional(readOnly = true)
    public SuffragiumCard getSuffragiumCardByGame(Integer gameId) {
        return suffragiumCardRepository.findSuffragiumCardByGame(gameId);
    }


    @Transactional(readOnly = true)
    public Game getGameBySuffragiumCard(SuffragiumCard suffragiumCard) {
        return suffragiumCardRepository.findGameBySuffragiumCard(suffragiumCard);
    }

    @Transactional
    public void updateVotes(SuffragiumCard card, Turn turn, Integer gameId) {
        SuffragiumCard cardToUpdate = suffragiumCardRepository.findById(card.getId()).get();
        Game game = gameRepository.findById(gameId);
        Integer limit = game.getSuffragiumLimit();
        cardToUpdate.setLoyalsVotes(cardToUpdate.getLoyalsVotes() + turn.getVotesLoyal());
        cardToUpdate.setTraitorsVotes(cardToUpdate.getTraitorsVotes() + turn.getVotesTraitor());
    
        if (card.getLoyalsVotes() >= limit || card.getTraitorsVotes() >= limit ) {
            game.setState(State.FINISHED);
            game.setEndDate(Date.from(Instant.now()));
            gameRepository.save(game);
            playerInfoRepository.findPlayersByGame(game).forEach(p -> gameService.checkPlayerIsPlaying(p));
        }
        suffragiumCardRepository.save(cardToUpdate);
    }

    @Transactional
    public SuffragiumCard createSuffragiumCardIfNeeded(Game game) throws DataAccessException{
        SuffragiumCard card = suffragiumCardRepository.findSuffragiumCardByGame(game.getId());
        if(card==null) {
            SuffragiumCard suffragiumCard = new SuffragiumCard();
            suffragiumCard.setLoyalsVotes(0);
            suffragiumCard.setTraitorsVotes(0);
            suffragiumCard.setVoteLimit(game.getSuffragiumLimit());
            return suffragiumCardRepository.save(suffragiumCard);
        }
        return card;
    }
    
}
