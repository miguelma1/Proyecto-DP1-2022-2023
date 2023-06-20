package org.springframework.samples.petclinic.achievements;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.samples.petclinic.enums.AchievementType;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.progress.Progress;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="achievements")
public class Achievement extends NamedEntity {
    
    @NotEmpty
    private String description;
    
    @Enumerated(EnumType.STRING)
    private AchievementType type;

    @Min(0)
    private double threshold;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "achievement")
    private List<Progress> progress;

    public String getActualDescription() {
        return description.replace("<THRESHOLD>", String.valueOf(threshold));
    }
    
}