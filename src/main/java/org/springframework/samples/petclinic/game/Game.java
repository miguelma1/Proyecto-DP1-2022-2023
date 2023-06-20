package org.springframework.samples.petclinic.game;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.EnumType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.invitation.Invitation;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCard;
import org.springframework.samples.petclinic.turn.Turn;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game extends NamedEntity {
    
    @NotNull
    private Boolean publicGame;

    @Enumerated(EnumType.STRING)
    private State state;

    private Integer numPlayers; 

    private Date startDate;
    
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private CurrentRound round;

    @OneToOne(optional = true)
    private Turn turn;

    @Enumerated(EnumType.STRING)
    private CurrentStage stage;

    @Enumerated(EnumType.STRING)
    private Faction winners;

    @OneToOne(optional = true)
    private SuffragiumCard suffragiumCard;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private List<Invitation> gameInvitations;


    public Integer getSuffragiumLimit() {
        Integer players = this.getNumPlayers();
        Integer res = null;
        if (players == 5) {
         res = 13;
        }
        else if (players == 6) {
         res = 15;
        }
        else if (players == 7) {
         res = 17;
        }
        else if (players == 8) {
         res = 20;
        }
        return res;
     }

     public Integer getDuration() {
        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        return (int) ChronoUnit.MINUTES.between(startInstant, endInstant);
     }
}
