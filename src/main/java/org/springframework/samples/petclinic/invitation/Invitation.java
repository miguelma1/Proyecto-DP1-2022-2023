package org.springframework.samples.petclinic.invitation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.samples.petclinic.enums.InvitationType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="invitations")
public class Invitation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private InvitationType invitationType;

    @Size(min=3, max=100)
    private String message;

    private Boolean accepted;

    @ManyToOne(optional = false)
    private Player sender;

    @ManyToOne(optional = false)
    private Player recipient;

    @ManyToOne(optional = true)
    private Game game;
}