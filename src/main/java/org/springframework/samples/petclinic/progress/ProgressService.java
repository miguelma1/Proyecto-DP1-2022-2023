package org.springframework.samples.petclinic.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.samples.petclinic.achievements.Achievement;
import org.springframework.samples.petclinic.achievements.AchievementRepository;
import org.springframework.samples.petclinic.enums.AchievementType;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {
    
    @Autowired
    ProgressRepository progressRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    AchievementRepository achievementRepository;
    @Autowired
    InvitationService invitationService;
    @Autowired
    PlayerService playerService;

    @Autowired
    public ProgressService(ProgressRepository progressRepository, AchievementRepository achievementRepository, PlayerService playerService) {
        this.progressRepository = progressRepository;
        this.achievementRepository = achievementRepository;
        this.playerService = playerService;
    }

    public List<Progress> getProgress() {
        return progressRepository.findAll();
    }

    @Transactional
    public List<Progress> getPlayerProgress(Player player) {

        List <Achievement> achievementsNotFound = achievementRepository.findAll();
        List <Progress> actualPlayerProgress = progressRepository.findProgressByPlayer(player);
        

        for (Progress progress : actualPlayerProgress) {
            if (achievementsNotFound.contains(progress.getAchievement())) {
                achievementsNotFound.remove(progress.getAchievement());
            }
        }

        for (Achievement achievement : achievementsNotFound) {
            addAchievementPlayer(achievement, player);
        }

        return progressRepository.findProgressByPlayer(player);
    }

    public void addAchievementPlayer (Achievement achievement, Player player) {
        progressRepository.save(new Progress(achievement, player));
    }

    public void saveProgress (Progress progress) { 
        progressRepository.save(progress);
    }

    public List<Pair<Achievement,Double>> achievementProgress (List<Progress> progress) {
        List<Pair<Achievement,Double>> res = new ArrayList<>();
        progress.forEach(x -> {
        Double completedPercentage;
        if (x.getAchievement().getType() == AchievementType.FRIENDS) {
            List<Player> friends = invitationService.getFriendsInvitations(x.getPlayer())
                                .stream().map(y -> y.getFirst()).collect(Collectors.toList());
            completedPercentage = friends.size()/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));
        }
        else if (x.getAchievement().getType() == AchievementType.TIME) {
           Integer totalTimePlayed = playerService.getTotalTimePlaying(x.getPlayer().getUser());
            completedPercentage = totalTimePlayed/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));

        }

        else if (x.getAchievement().getType() == AchievementType.GAMES) {
            Double gamesNumber = playerService.getGamesPlayedByPlayer(x.getPlayer());
            completedPercentage = gamesNumber/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));


        }
        else if (x.getAchievement().getType() == AchievementType.VICTORY && x.getAchievement().getDescription().toUpperCase().contains("MERCHANT")) {
            Double winsNumber = playerService.findUserWinsAsMerchant(x.getPlayer().getUser());
            completedPercentage = winsNumber/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));

        }
        else if (x.getAchievement().getType() == AchievementType.VICTORY && x.getAchievement().getDescription().toUpperCase().contains("LOYAL")) {
            Double winsNumber = playerService.findUserWinsAsLoyal(x.getPlayer().getUser());
            completedPercentage = winsNumber/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));

        }
        else if (x.getAchievement().getType() == AchievementType.VICTORY && x.getAchievement().getDescription().toUpperCase().contains("TRAITOR")) {
            Double winsNumber = playerService.findUserWinsAsTraitor(x.getPlayer().getUser());
            completedPercentage = winsNumber/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));

        }
        else if (x.getAchievement().getType() == AchievementType.VICTORY) {
            Double winsNumber = (double) playerService.findWinsByPlayer(x.getPlayer());
            completedPercentage = winsNumber/x.getAchievement().getThreshold()*100;
            completedPercentage = completedPercentage > 100.0 ? 100.0 : completedPercentage;
            res.add(Pair.of(x.getAchievement(), completedPercentage));

        }
        else {
            res.add(Pair.of(x.getAchievement(), 0.0));
        }

       });
       return res;
    }
}
