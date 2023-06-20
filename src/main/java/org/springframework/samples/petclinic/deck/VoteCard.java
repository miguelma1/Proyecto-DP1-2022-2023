package org.springframework.samples.petclinic.deck;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="vote_cards")
public class VoteCard {
    
    @Id
    @Enumerated(EnumType.STRING)
    private VCType type;

    public enum VCType {
        GREEN, RED, YELLOW;
    }

    public String getCard() {
        String res = null;
        if (this.type == VCType.GREEN) {
            res = "/resources/images/GreenVote.PNG";

        }
        if (this.type == VCType.RED) {
            res = "/resources/images/RedVote.PNG";

        }
        if (this.type == VCType.YELLOW) {
            res = "/resources/images/YellowVote.PNG";

        }
        return res;
    }
}
