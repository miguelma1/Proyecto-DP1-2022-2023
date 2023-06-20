package org.springframework.samples.petclinic.deck;

import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.stereotype.Repository;

@Repository
public interface FactionCardRepository extends CrudRepository <FactionCard, FCType> {
    
}
