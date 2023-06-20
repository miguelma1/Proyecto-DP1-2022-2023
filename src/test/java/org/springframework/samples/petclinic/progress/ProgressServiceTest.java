package org.springframework.samples.petclinic.progress;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.data.util.Pair;
import java.util.ArrayList;
import java.util.List;
import org.springframework.samples.petclinic.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.samples.petclinic.achievements.Achievement;
import org.springframework.samples.petclinic.achievements.AchievementRepository;
import org.springframework.samples.petclinic.enums.AchievementType;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.player.PlayerService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProgressServiceTest {
    
    @Mock
    ProgressRepository progressRepository;

    @Mock
    AchievementRepository achievementRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    PlayerService playerService;

    Player player;
    Achievement a1;
    Achievement a2;
    Achievement a3;
    Progress p1;
    Progress p2;
    Progress p3;
    List<Achievement> allAchievements;
    List<Progress> actualProgress;
    List<Progress> updatedProgresses;


   

    private Player createPlayer() {
        Player player = new Player();
        player.setUser(new User());
        return player;
    }

    private Achievement createAchievement(String name, String description, AchievementType type) {
        Achievement achievement = new Achievement();
        achievement.setType(type);
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setThreshold(50.0);
        achievement.setProgress(null);
        return achievement;
    }

    private Progress createProgress(Achievement achievement, Player player) {
        Progress progress = new Progress(achievement, player);
        return progress;
    }

    @BeforeEach
    private void config() {
        player = createPlayer();

        a1 = createAchievement("Achievement 1", "Type duration threshold is 50", AchievementType.TIME);
        a2 = createAchievement("Achievement 2", "Type games threshold is 50", AchievementType.GAMES);
        a3 = createAchievement("No type achievement", "This achievement has no type", null);


        p1 = createProgress(a1, player);
        p2 = createProgress(a2, player);
        p3 = createProgress(a3, player);

        allAchievements = new ArrayList<>();
        allAchievements.add(a1);
        allAchievements.add(a2);
        allAchievements.add(a3);
        actualProgress = new ArrayList<>();
        actualProgress.add(p1);
        actualProgress.add(p2);

        updatedProgresses = new ArrayList<>();
        updatedProgresses.add(p1);
        updatedProgresses.add(p2);
        updatedProgresses.add(p3);

        when(achievementRepository.findAll()).thenReturn(allAchievements);
        when(progressRepository.findProgressByPlayer(any(Player.class))).thenReturn(actualProgress).thenReturn(updatedProgresses);
        when(playerService.getTotalTimePlaying(player.getUser())).thenReturn(10);
        when(playerService.getGamesPlayedByPlayer(any(Player.class))).thenReturn(10.0);
    }

    @Test
    public void testSaveTurnSuccessful() {
        ProgressService progressService = new ProgressService(progressRepository, achievementRepository, playerService);
        try {
            progressService.saveProgress(p1);;
        } catch (Exception e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testAddAchievementPlayer() { 
        ProgressService progressService = new ProgressService(progressRepository, achievementRepository, playerService);
        try {
            progressService.addAchievementPlayer(a1, player);;
        } catch (Exception e) {
            fail("no exception should be thrown");
        }

    }

    @Test
    public void testGetPlayerProgress() {
        ProgressService progressService = new ProgressService(progressRepository, achievementRepository, playerService);
        assertTrue(actualProgress.size() == 2);
        assertFalse(actualProgress.contains(p3));
        assertTrue(progressService.getPlayerProgress(player).size() == 3);
        assertTrue(progressService.getPlayerProgress(player).contains(p3));

        
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testAchievementProgress(Integer i) {
        ProgressService progressService = new ProgressService(progressRepository, achievementRepository, playerService);
        
        List<Pair<Achievement,Double>> achievementProgress = progressService.achievementProgress(updatedProgresses);
        assertTrue(achievementProgress.get(i).getFirst() == updatedProgresses.get(i).getAchievement());
        assertTrue(achievementProgress.get(i).getSecond() >= 0.0);
        assertTrue(achievementProgress.get(i).getSecond() <= 100.0);
        
    }


}
