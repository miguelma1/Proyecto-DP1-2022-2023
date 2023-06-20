package org.springframework.samples.petclinic.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @Transactional
    public Map<Player, Integer> listRankingUserVictory() throws DataAccessException {
        Map<Player, Integer> result = new LinkedHashMap<>();
        Map<Player, Integer> allPlayersAndVictories = new LinkedHashMap<>();
        List<Player> players = playerRepository.findAll();

        for (Player p: players){
            Integer victories = playerService.findWinsByPlayer(p);
            allPlayersAndVictories.put(p, victories);

        }

        Map<Player, Integer> sortedMap = allPlayersAndVictories.entrySet().stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        int count = 0;
        for (Map.Entry<Player, Integer> entry : sortedMap.entrySet()) {
            if (count == 10) {
                break;
            }
                result.put(entry.getKey(), entry.getValue());
                count++;
            }   
        return result;
    }
    
    @Transactional
    public List<Double> listStatistics(User user) throws DataAccessException{
        List<Double> list =new ArrayList<Double>();
        
        Player player = playerRepository.findPlayerByUsername(user.getUsername());
        
		Double gamesPlayed = playerService.getGamesPlayedByPlayer(player);
        
        List<Game> allFinishedGames = gameService.getGamesByState(State.FINISHED);

        Double victory = (double) playerService.findWinsByPlayer(player);
 
        Double loss = gamesPlayed-victory;
        
        Double timePlaying = Double.valueOf(playerService.getTotalTimePlaying(user));
        Double PerWin;
        Double PerLos;
        if (gamesPlayed==0.0){
            PerWin=0.0;
            PerLos=0.0;
        }
        else{
            PerWin = victory*100/gamesPlayed;
            PerLos = loss*100/gamesPlayed;
        }
        Double averageTimePlaying = playerService.getTotalTimePlaying(user) == 0 ? 0. : timePlaying/gamesPlayed;
        Double maxTimePlaying = Double.valueOf(playerService.getMaxTimePlaying(user));
        Double minTimePlaying = Double.valueOf(playerService.getMinTimePlaying(user));
        Double globalTimePlaying = Double.valueOf(gameService.getGlobalTimePlaying());
        Double globalAverageTimePlaying = allFinishedGames.size() == 0 ? 0. : globalTimePlaying/allFinishedGames.size(); 
        Double globalMaxTimePlaying = Double.valueOf(gameService.getGlobalMaxTimePlaying());
        Double globalMinTimePlaying = Double.valueOf(gameService.getGlobalMinTimePlaying());
        Double winsAsTraitor = playerService.findUserWinsAsTraitor(user);
        Double perWinsAsTraitor;
        Double winsAsLoyal = playerService.findUserWinsAsLoyal(user);
        Double perWinsAsLoyal;
        Double winsAsMerchant = playerService.findUserWinsAsMerchant(user);
        Double perWinsAsMerchant;
        Double avgNumPlayers = playerService.getAvgNumPlayersByPlayer(player);
        Double minNumPlayers = playerService.getMinNumPlayersByPlayer(player);
        Double maxNumPlayers = playerService.getMaxNumPlayersByPlayer(player);
        Double globalAvgNumPlayers = gameService.getGlobalAvgNumPlayers();
        Double globalMinNumPlayers = gameService.getGlobalMinNumPlayers();
        Double globalMaxNumPlayers = gameService.getGlobalMaxNumPlayers();
        Integer totalGamesPlayed =  gameRepository.findByState(State.FINISHED).size();
        if (winsAsTraitor == 0.0){
            perWinsAsTraitor = 0.0;
        } else {
            perWinsAsTraitor = winsAsTraitor*100/victory;
        }
        if (winsAsLoyal == 0.0){
            perWinsAsLoyal = 0.0;
        } else {
            perWinsAsLoyal = winsAsLoyal*100/victory;
        }if (winsAsMerchant == 0.0){
            perWinsAsMerchant = 0.0;
        } else {
            perWinsAsMerchant = winsAsMerchant*100/victory;
        }
		list.add(gamesPlayed);
		list.add((double) victory);
        list.add(loss);
		list.add(PerWin);
        list.add(PerLos);
        list.add(timePlaying);
        list.add(globalTimePlaying);
        list.add(averageTimePlaying);
        list.add(globalAverageTimePlaying);
        list.add(maxTimePlaying);
        list.add(globalMaxTimePlaying);
        list.add(minTimePlaying);
        list.add(globalMinTimePlaying);
        list.add(winsAsLoyal);
        list.add(perWinsAsLoyal);
        list.add(winsAsMerchant);
        list.add(perWinsAsMerchant);
        list.add(winsAsTraitor);
        list.add(perWinsAsTraitor);
        list.add(avgNumPlayers);
        list.add(minNumPlayers);
        list.add(maxNumPlayers);
        list.add(globalAvgNumPlayers);
        list.add(globalMinNumPlayers);
        list.add(globalMaxNumPlayers);
        list.add((double) totalGamesPlayed);
		return list;
    }

}
