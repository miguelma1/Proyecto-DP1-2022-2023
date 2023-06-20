package org.springframework.samples.petclinic.achievements;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTests {

    @Mock
    AchievementRepository achievementRepository;

    private Achievement createAchievement(String name, String description, double threshold) {
        Achievement achievement = new Achievement();
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setThreshold(threshold);
        achievement.setProgress(null);
        return achievement;
    }

    @Test
    public void testSaveAchievement() {
        Achievement achievement = createAchievement("Test achievement", "Please, pass this test", 10.);
        AchievementService service = new AchievementService(achievementRepository);
        try {
            service.saveAchievement(achievement);
            verify(achievementRepository).save(achievement);
        } catch (Exception e) {
            fail("No exception should be thrown");
        }
    }

}
