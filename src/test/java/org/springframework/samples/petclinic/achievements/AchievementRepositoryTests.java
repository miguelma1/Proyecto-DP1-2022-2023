package org.springframework.samples.petclinic.achievements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AchievementRepositoryTests {

    @Autowired
    AchievementRepository achievementRepository;

    @Test
    public void initialDataAndFindAllTest() {
        List<Achievement> achievements = achievementRepository.findAll();
        assertNotNull(achievements);
        assertFalse(achievements.isEmpty());
    }
    
}
