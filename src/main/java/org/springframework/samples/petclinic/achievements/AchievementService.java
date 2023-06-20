package org.springframework.samples.petclinic.achievements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.enums.AchievementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AchievementService {
    
    AchievementRepository achievementRepository;

    @Autowired
    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Transactional(readOnly = true)
    public List<Achievement> getAchievements() {
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Achievement getById (int id) {
        return achievementRepository.findById(id).get();
    }

    @Transactional
    public void deleteAchievementById (int id) {
        achievementRepository.deleteById(id);
    }

    @Transactional
    public void saveAchievement (Achievement achievement) { 
        achievementRepository.save(achievement);
    }

    public List<AchievementType> getAllTypes () {
        return List.of(AchievementType.values());
    }

}
