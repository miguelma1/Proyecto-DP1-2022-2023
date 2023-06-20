package org.springframework.samples.petclinic.player;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.samples.petclinic.deck.Deck;
import org.springframework.samples.petclinic.invitation.Invitation;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.progress.Progress;
import org.springframework.samples.petclinic.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="players")
@Audited
public class Player extends BaseEntity {

    @NotAudited
    private Boolean online;

    private Boolean playing;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @NotAudited
	private User user;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @NotAudited
    private List<Progress> progress;

    @OneToMany (mappedBy = "player")
    @NotAudited
    private List<Deck> decks;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @NotAudited
    private List<Invitation> invitationsSent;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    @NotAudited
    private List<Invitation> invitationsReceived;
}