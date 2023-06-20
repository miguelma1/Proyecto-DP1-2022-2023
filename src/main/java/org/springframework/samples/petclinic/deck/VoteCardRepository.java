package org.springframework.samples.petclinic.deck;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteCardRepository extends CrudRepository<VoteCard, VCType>{
    List<VoteCard> findAll();
   
}
