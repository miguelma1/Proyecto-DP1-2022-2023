package org.springframework.samples.petclinic.game;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.deck.DeckRepository;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.RoleCard;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCard;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {
    
    @Autowired
    private GameRepository repo;

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private PlayerInfoRepository playerInfoRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private DeckService deckService;

    @Autowired
    public GameService(GameRepository repo) {
        this.repo = repo;
    }

    public GameService(GameRepository repo, PlayerInfoRepository playerInfoRepository, PlayerRepository playerRepository, TurnRepository turnRepository, DeckRepository deckRepository, InvitationService invitationService, DeckService deckService) {
        this.repo = repo;
        this.playerInfoRepository = playerInfoRepository;
        this.playerRepository = playerRepository;
        this.turnRepository = turnRepository;
        this.deckRepository = deckRepository;
        this.invitationService = invitationService;
        this.deckService = deckService;
    }

    @Transactional(readOnly = true)
    public Game getGameById(Integer id){
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Game> getGamesByState(State state){
        return repo.findByState(state);
    }

    @Transactional(readOnly = true)
    public List<Game> getGamesByNameAndState(String name, State s) {
        return repo.findByName(name).stream().filter(g -> g.getState() == s).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Game> getPublicGamesByNameAndState(String name, State s) {
        return repo.findPublicGamesByName(name).stream().filter(g -> g.getState() == s).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Game> getPrivateGamesByNameAndState(String name, State s) {
        return repo.findPrivateGamesByName(name).stream().filter(g -> g.getState() == s).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Game> getFriendGamesByNameAndState(String name, State s, Player player) {
        List<Game> res = new ArrayList<>();
        List<Game> privateGames = getPrivateGamesByNameAndState(name, s);
        List<Player> friends = invitationService.getFriends(player);
        for(Game game: privateGames) {
            for(Player friend: friends) {
                if(!res.contains(game) && playerInfoRepository.findPlayersByGame(game).contains(friend)) {
                    res.add(game);
                }
            }
        }
        return res;
    }

    @Transactional(readOnly = true)
    public List<Game> getPlayerGamesHistory(String name, Player player, Boolean publicGame) {
        List<Game> finishedGames = getGamesByNameAndState(name, State.FINISHED);
        List<Game> playerGames = playerInfoRepository.findGamesByPlayer(player);
        return getListIntersection(finishedGames, playerGames).stream()
                .filter(g -> g.getPublicGame() == publicGame).collect(Collectors.toList());
    }

    public static List<Game> getListIntersection(List<Game> firstList, List<Game> secondList) {
        List<Game> resultList = new ArrayList<Game>();
        List <Game> result = new ArrayList <Game> (firstList);  
        HashSet <Game> othHash = new HashSet <Game> (secondList); 
        Iterator <Game> iter = result.iterator();
        while(iter.hasNext()) {
            if(!othHash.contains(iter.next())) {  
                iter.remove();            
            }     
        }
        resultList = new ArrayList<Game>(result);
        return resultList;
    }

    private static final Integer STARTING_NUMBER_OF_PLAYERS = 1;

    @Transactional
    public Game saveGame(Game game, Turn turn) throws DataAccessException {
        game.setState(State.STARTING);
        game.setNumPlayers(STARTING_NUMBER_OF_PLAYERS);
        game.setStartDate(null);
        game.setEndDate(null);
        game.setRound(CurrentRound.FIRST);
        game.setTurn(turn);
        game.setStage(CurrentStage.VOTING);
        game.setWinners(null);
        game.setSuffragiumCard(null);
        return repo.save(game);
    }

    @Transactional
    public Game startGameIfNeeded(Game game, SuffragiumCard suffragiumCard) throws DataAccessException {
        if(game.getState() == State.STARTING) {
            game.setState(State.IN_PROCESS);
            Date date = Date.from(Instant.now());
            game.setStartDate(date);
            game.setSuffragiumCard(suffragiumCard);
            for(Player p: playerInfoRepository.findPlayersByGame(game)) {
                checkPlayerIsPlaying(p);
            }
        }
        return repo.save(game);
    }

    @Transactional
	public void checkPlayerIsPlaying(Player player) {
		List<Game> gamesInProcess = playerInfoRepository.findGamesInProcessByPlayer(player);
		if(!gamesInProcess.isEmpty()) {
			player.setPlaying(true);
		} else {
			player.setPlaying(false);
		}
        playerRepository.save(player);
	}

    @Transactional
    public void joinGame(Game game) throws DataAccessException {
        game.setNumPlayers(game.getNumPlayers()+1);
        repo.save(game);
    }

    
    @Transactional
    public void exitGame(PlayerInfo playerInfo, Game game) throws DataAccessException {
        if(!playerInfo.getSpectator()) {
            game.setNumPlayers(game.getNumPlayers()-1);
            repo.save(game);
        }
        if(game.getState() == State.STARTING){
           playerInfoRepository.deleteById(playerInfo.getId()); 
        }
    }
    
    @Transactional
    public void changeStage(Game game, CurrentStage stage) {
        game.setStage(stage);
        if (stage == CurrentStage.VOTING) {
            changeTurnAndRound(game); //si cambiamos a voting es pq pasamos de turno
        }
        repo.save(game);
    }
    
    private static final Integer TOTAL_VOTES_NUMBER = 2;

    @Transactional
    public void changeStageIfVotesCompleted(Game game) {
        if(game.getTurn().getVoteCount() == TOTAL_VOTES_NUMBER) {
            game.setStage(CurrentStage.VETO);
            repo.save(game);
        }
        else if(game.getTurn().getVoteCount() > TOTAL_VOTES_NUMBER) {
            game.setStage(CurrentStage.SCORING);
            repo.save(game);
        }
    }
    
    private static final Integer NEW_TURN_INITIAL_VOTES = 0;

    @Transactional
    public void changeTurnAndRound(Game game) {
        Turn turnToChange = game.getTurn();
        turnToChange.setVotesLoyal(NEW_TURN_INITIAL_VOTES);
        turnToChange.setVotesTraitor(NEW_TURN_INITIAL_VOTES);
        turnToChange.setVotesNeutral(NEW_TURN_INITIAL_VOTES);
        turnToChange.setCurrentTurn(turnToChange.getCurrentTurn() + 1);
        if(game.getTurn().getCurrentTurn() > game.getNumPlayers()) {
            turnToChange.setCurrentTurn(1);
            turnRepository.save(turnToChange);
            if(game.getRound() == CurrentRound.FIRST){
                game.setRound(CurrentRound.SECOND);
            }
            else if (game.getRound() == CurrentRound.SECOND) {
                game.setState(State.FINISHED);
                game.setEndDate(Date.from(Instant.now()));
                playerInfoRepository.findPlayersByGame(game).forEach(p -> checkPlayerIsPlaying(p));
            }
        }
        turnRepository.save(turnToChange);
        repo.save(game);
    }

    @Transactional
    public Integer gameRoleCardNumber (Game game) {
        Integer res = deckRepository.findAll().stream()
            .filter(x -> x.getGame() == game).filter(y -> y.getRoleCard() != RoleCard.NO_ROL).collect(Collectors.toList()).size();
        return res;
    }

    @Transactional
    public void winnerFaction (Game game) {
        Integer loyalVotes = game.getSuffragiumCard().getLoyalsVotes();
        Integer traitorVotes = game.getSuffragiumCard().getTraitorsVotes();
        Integer voteLimit = game.getSuffragiumLimit();
        Faction winner;

        if (loyalVotes >= voteLimit || traitorVotes >= voteLimit) { //conspiracion fallida
            if (loyalVotes >= voteLimit) {
                winner = Faction.TRAITORS; //si supera leales gana traidor
            }
            else {
                winner = Faction.LOYALS; //si no, esque ha superado traidor y gana leales
            }
        }

        else { //idus de marzo
            if ((loyalVotes + 1) < traitorVotes) {
                winner =  Faction.TRAITORS;
            }

            else if ((traitorVotes + 1) < loyalVotes) {
                winner = Faction.LOYALS;
            }
            
            else {
                winner =  Faction.MERCHANTS;
            }
        }
        game.setWinners(winner);
        repo.save(game);
    }

    @Transactional(readOnly = true)
    public Map<Game,List<Player>> winnersByGame () {
        Map<Game,List<Player>> res = new HashMap<>();
        List<Game> games = repo.findAll();
        games.forEach(g -> {
            List<Player> winners = deckService.winnerPlayers(g, g.getWinners());
            res.put(g, winners);
        });
        return res;
    }

    @Transactional
    List<String> activePlayers (Game game) {
        List <String> activePlayers = null;
        if (game.getStage() == CurrentStage.VOTING) {
            if (!deckService.votesAsigned(game)) {
                activePlayers = playerInfoRepository.findPlayersByGame(game).stream()
                .filter(p -> deckRepository.findDeckByPlayerAndGame(p, game).getRoleCard() == RoleCard.CONSUL)
                    .map(p -> p.getUser().getUsername()).collect(Collectors.toList());
            }
            else {
            activePlayers = playerInfoRepository.findPlayersByGame(game).stream()
                .filter(p -> deckRepository.findDeckByPlayerAndGame(p, game).getRoleCard() == RoleCard.EDIL && deckRepository.findDeckByPlayerAndGame(p, game).getVoteCards().size() > 1)
                    .map(p -> p.getUser().getUsername()).collect(Collectors.toList());
            }

        }
        else if (game.getStage() == CurrentStage.VETO) {
             activePlayers = playerInfoRepository.findPlayersByGame(game).stream()
                .filter(p -> deckRepository.findDeckByPlayerAndGame(p, game).getRoleCard() == RoleCard.PRETOR)
                    .map(p -> p.getUser().getUsername()).collect(Collectors.toList());

        }
        else if (game.getStage() == CurrentStage.END_OF_TURN) {
            activePlayers = playerInfoRepository.findPlayersByGame(game).stream()
                .filter(p -> deckRepository.findDeckByPlayerAndGame(p, game).getRoleCard() == RoleCard.CONSUL)
                    .map(p -> p.getUser().getUsername()).collect(Collectors.toList());
        }
        return activePlayers;
    }

    @Transactional(readOnly = true)
	public Integer getGlobalTimePlaying() {
		List<Game> allFinishedGames = repo.findByState(State.FINISHED);
		return allFinishedGames.stream().map(x -> x.getDuration()).mapToInt(Integer::intValue).sum();
	}

    @Transactional(readOnly = true)
    public Integer getGlobalMaxTimePlaying() {
        List<Game> allFinishedGames = repo.findByState(State.FINISHED);
		return allFinishedGames.stream().map(x -> x.getDuration()).sorted(Comparator.reverseOrder()).findFirst().get();
    }

    @Transactional(readOnly = true)
    public Integer getGlobalMinTimePlaying() {
        List<Game> allFinishedGames = repo.findByState(State.FINISHED);
		return allFinishedGames.stream().map(x -> x.getDuration()).sorted().findFirst().get();
    }

    @Transactional(readOnly = true)
	public Double getGlobalAvgNumPlayers() {
		List<Game> games = repo.findByState(State.FINISHED);
		Double a = 0.;
		Double b = 0.;
		for(Game g: games) {
			a += g.getNumPlayers();
			b ++;
		}
        if(b==0) return 0.;
		return a/b;
	}

    @Transactional(readOnly = true)
	public Double getGlobalMinNumPlayers() {
		List<Game> games = repo.findByState(State.FINISHED);
		List<Double> numsPlayers = new ArrayList<>();
		for(Game g: games) {
			numsPlayers.add((double) g.getNumPlayers());
		}
        if(numsPlayers.isEmpty()) return 0.;
		return Collections.min(numsPlayers);
	}

    @Transactional(readOnly = true)
	public Double getGlobalMaxNumPlayers() {
		List<Game> games = repo.findByState(State.FINISHED);
		List<Double> numsPlayers = new ArrayList<>();
		for(Game g: games) {
			numsPlayers.add((double) g.getNumPlayers());
		}
        if(numsPlayers.isEmpty()) return 0.;
		return Collections.max(numsPlayers);
	}
    
}
