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
@Table(name="faction_cards")
public class FactionCard {
    
    @Id
    @Enumerated(EnumType.STRING)
    private FCType type;

    public enum FCType {
        LOYAL,TRAITOR,MERCHANT;
    }
    
    public String getCard() {
        String res = null;
        if (this.type == FCType.LOYAL) {
            res = "/resources/images/Loyal.PNG";

        }
        if (this.type == FCType.TRAITOR) {
            res = "/resources/images/Traitor.PNG";

        }
        if (this.type == FCType.MERCHANT) {
            res = "/resources/images/Merchant.PNG";

        }
        return res;
    }
    
}
