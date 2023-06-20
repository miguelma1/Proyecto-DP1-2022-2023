package org.springframework.samples.petclinic.turn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurnService {

    @Autowired
    TurnRepository turnRepository;
    
    @Autowired
    GameRepository gameRepository;

    @Autowired
    public TurnService (TurnRepository turnRepository, GameRepository gameRepository) {
        this.turnRepository = turnRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public void save (Turn turn) {
        turnRepository.save(turn);
    }

    @Transactional
    public void updateTurnVotes (Turn turn, VCType voteType) {
        if (voteType == VCType.GREEN) {
			turn.setVotesLoyal(turn.getVotesLoyal() == null ? 1 : turn.getVotesLoyal() + 1); //creo que aparece null como predeterminado pq esta en la base de dato, si no, no deberia
		}
		if (voteType == VCType.RED) {
			turn.setVotesTraitor(turn.getVotesTraitor() == null ? 1 : turn.getVotesTraitor() + 1);
		}
        if (voteType == VCType.YELLOW) {
            turn.setVotesNeutral(turn.getVotesNeutral() == null ? 1 : turn.getVotesNeutral() + 1);
        }
		turnRepository.save(turn);
    }

    @Transactional
    public void newTurn (Turn turn) {
        Game game = turnRepository.findGameByTurn(turn); //COJO EL GAME ACTUAL
         
        turn.setCurrentTurn(turn.getCurrentTurn() + 1);
        turn.setVotesLoyal(0);
        turn.setVotesTraitor(0);
        turn.setVotesNeutral(0);
        if (turn.getCurrentTurn() > game.getNumPlayers()) {
            turn.setCurrentTurn(1);
            if(game.getRound() == CurrentRound.FIRST) {
                game.setRound(CurrentRound.SECOND);
                gameRepository.save(game);
            }
        }
        turnRepository.save(turn);  
    }

    @Transactional
    public void pretorVoteChange (VCType currentVoteType, VCType changedVoteType, Game game) {
        Turn currentTurn = game.getTurn();
        Integer currentLoyalVotes = currentTurn.getVotesLoyal() == null ? 1 : currentTurn.getVotesLoyal();
        Integer currentTraitorVotes = currentTurn.getVotesTraitor()  == null ? 1 : currentTurn.getVotesTraitor();
        //los condicionales se deberian de borrar, estan para que en las pruebas no de errores

    
        if (currentVoteType != changedVoteType) {
            
            if (currentVoteType == VCType.GREEN) {
                currentTurn.setVotesLoyal(currentLoyalVotes-1);
                if (changedVoteType == VCType.RED) {
                    currentTurn.setVotesTraitor(currentTraitorVotes+1);
                }
            }
            else if (currentVoteType == VCType.RED) {
                currentTurn.setVotesTraitor(currentTraitorVotes - 1);
                if (changedVoteType == VCType.GREEN) {
                    currentTurn.setVotesLoyal(currentLoyalVotes + 1);
                }
            }
            
            }
            turnRepository.save(currentTurn);
        }

}