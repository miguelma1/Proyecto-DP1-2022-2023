package org.springframework.samples.petclinic.achievements;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AchievementTests {
    
    @Test
    public void getActualDescriptionSuccessTest() {
        Achievement achievement = new Achievement();
        achievement.setName("Example achievement");
        achievement.setDescription("The threshold of this achievement is <THRESHOLD>");
        achievement.setThreshold(10.0);

        assertEquals(achievement.getActualDescription(), "The threshold of this achievement is 10.0");
    }
    
}
