package org.springframework.samples.petclinic.deck;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FactionCardService {
    
    FactionCardRepository rep;

    @Autowired
    public FactionCardService(FactionCardRepository rep) {
        this.rep = rep;
    }

    @Transactional(readOnly = true)
    public FactionCard getByFaction (FCType type) {
        return rep.findById(type).get();
    }
 
}
