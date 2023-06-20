package org.springframework.samples.petclinic.suffragiumCard;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.samples.petclinic.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "suffragium_cards")
public class SuffragiumCard extends BaseEntity {
    
    private Integer loyalsVotes;
    private Integer traitorsVotes;
    private Integer voteLimit;
}