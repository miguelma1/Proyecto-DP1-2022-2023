package org.springframework.samples.petclinic.deck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.Faction;
import org.springframework.samples.petclinic.enums.RoleCard;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeckService {

    @Autowired
    private DeckRepository rep;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerInfoRepository playerInfoRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FactionCardRepository factionCardRepository;

    @Autowired
    private VoteCardRepository voteCardRepository;

    @Autowired
    public DeckService(DeckRepository rep, FactionCardRepository factionCardRepository, VoteCardRepository voteCardRepository) {
        this.rep = rep;
        this.factionCardRepository = factionCardRepository;
        this.voteCardRepository = voteCardRepository;
    }

    @Transactional(readOnly = true)
    public Deck getDeckByPlayerAndGame(Player player, Game game) {
        return rep.findDeckByPlayerAndGame(player, game);
    }

    @Transactional
    public void saveDeck (Deck deck) {
        rep.save(deck);
    }

    @Transactional
    public void updateFactionDeck (Deck deck, FCType factionCard) {
        List<FactionCard> chosenFaction = new ArrayList<>();
        FactionCard cardChosen = factionCardRepository.findById(factionCard).get();
        chosenFaction.add(cardChosen);
        Deck deckToUpdate = rep.findById(deck.getId()).get();
        deckToUpdate.setFactionCards(chosenFaction);
        rep.save(deckToUpdate);
    }
    
    @Transactional
    public void updateVotesDeck (Deck deck, VCType voteCard) {
        List<VoteCard> chosenVote = new ArrayList<>();
        VoteCard cardChosen = voteCardRepository.findById(voteCard).get();
        chosenVote.add(cardChosen);
        Deck deckToUpdate = rep.findById(deck.getId()).get();
        deckToUpdate.setVoteCards(chosenVote);
        rep.save(deckToUpdate);
    } 

    @Transactional
    public List<Deck> getDecks() {
        return rep.findAll();
    }

    @Transactional(readOnly = true)
    public List<FactionCard> getFactionCards(Integer numPlayers) {
        List<FactionCard> factions = new ArrayList<>();
        for(int i=0; i<numPlayers-1; i++) {
            factions.add(factionCardRepository.findById(FCType.LOYAL).get());
            factions.add(factionCardRepository.findById(FCType.TRAITOR).get());
        }
        factions.add(factionCardRepository.findById(FCType.MERCHANT).get());
        factions.add(factionCardRepository.findById(FCType.MERCHANT).get());
        return factions;
    }

    @Transactional(readOnly = true)
    public List<FactionCard> getPlayerFactionCards(List<FactionCard> factions) {
        List<FactionCard> res = new ArrayList<>();
        int faction1 = (int) (Math.random() * (factions.size()-1));
        res.add(factions.get(faction1));
        factions.remove(faction1);
        int faction2 = (int) (Math.random() * (factions.size()-1));
        res.add(factions.get(faction2));
        factions.remove(faction2);
        return res;
    }

    public void clearDeckVoteCards (Deck deck) {
        deck.setVoteCards(new ArrayList<>());
        rep.save(deck);

    }

    @Transactional
    public void clearEdilVoteCards (Game game) {
        List <Deck> edilsDeck = rep.findAll().stream()
            .filter(x -> x.getGame()  == game).filter(y -> y.getRoleCard() == RoleCard.EDIL).collect(Collectors.toList());
        
        edilsDeck.forEach(deck ->{
            clearDeckVoteCards(deck);
            rep.save(deck);

        } );
    }

    @Transactional(readOnly = true)
    public List<VoteCard> getFirstRoundVoteCards() {
        List<VoteCard> res = new ArrayList<>();
        res.add(voteCardRepository.findById(VCType.GREEN).get());
        res.add(voteCardRepository.findById(VCType.RED).get());
        return res;
    }

    private static final Integer ANY_PLAYER = 0;

    @Transactional
    public void assingDecksIfNeeded(Game game) {
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        if(rep.findDeckByPlayerAndGame(players.get(ANY_PLAYER), game) == null) {
            List<FactionCard> factions = getFactionCards(players.size());
            List<VoteCard> votes = getFirstRoundVoteCards();
            Integer consul = (int) (Math.random() * (players.size()-1));
            Integer pretor = (consul+1) % (players.size());
            Integer edil1 = (pretor+1) % (players.size());
            Integer edil2 = (edil1+1) % (players.size());
            
            for(int i=0; i<players.size(); i++) {
                Deck deck = new Deck();
                deck.setGame(game);
                deck.setPlayer(players.get(i));
                if(i == consul) {
                    deck.setRoleCard(RoleCard.CONSUL);
                    List<FactionCard> playerFactions = getPlayerFactionCards(factions);
                    deck.setFactionCards(playerFactions);
                    deck.setVoteCards(new ArrayList<>());
                } 
                else if(i == pretor) {
                    deck.setRoleCard(RoleCard.PRETOR);
                    List<FactionCard> playerFactions = getPlayerFactionCards(factions);
                    deck.setFactionCards(playerFactions);
                    deck.setVoteCards(new ArrayList<>());
                }
                
                else if(i == edil1 || i == edil2) {
                    deck.setRoleCard(RoleCard.EDIL);
                    List<FactionCard> playerFactions = getPlayerFactionCards(factions);
                    deck.setFactionCards(playerFactions);
                    deck.setVoteCards(votes);
                } 
                else {
                    deck.setRoleCard(RoleCard.NO_ROL);
                    List<FactionCard> playerFactions = getPlayerFactionCards(factions);
                    deck.setFactionCards(playerFactions);
                    deck.setVoteCards(new ArrayList<>());
                } 
                rep.save(deck);
            }
        }
        
    }

    
    public void deckRotation (Game game) {
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        Player consulPlayer = getDecks().stream()
            .filter(x -> x.getGame() == game).filter(y -> y.getRoleCard() == RoleCard.CONSUL).findFirst().get().getPlayer();
        Integer consulId = players.indexOf(consulPlayer);

        for(int i=0; i<5; i++) {
            Deck deckToUpdate =  getDeckByPlayerAndGame(players.get((consulId + i) % (players.size())), game);
            if (i == 0) { //consul pasa a noRol
                deckToUpdate.setRoleCard(RoleCard.NO_ROL);
            }
            else if (i == 1) { //pretor pasa a consul
                deckToUpdate.setRoleCard(RoleCard.CONSUL);
                
            }
            else if (i == 2) { //edil1 pasa a pretor
                clearDeckVoteCards(deckToUpdate);
                deckToUpdate.setRoleCard(RoleCard.PRETOR);

            }
            else { //los dos nuevos ediles
                List<VoteCard> newVotes = new ArrayList<>();
                if (deckToUpdate.getVoteCards().size() != 0) { //si tiene algun voto se lo quitamos
                    clearDeckVoteCards(deckToUpdate);
                }
                if (deckToUpdate.getRoleCard() != RoleCard.EDIL) { //si no es ya edil le damos edil
                    deckToUpdate.setRoleCard(RoleCard.EDIL);
                }
                if (game.getRound() == CurrentRound.SECOND) {
                    newVotes.add(voteCardRepository.findById(VCType.YELLOW).get());
                }
                newVotes.add(voteCardRepository.findById(VCType.GREEN).get());
                newVotes.add(voteCardRepository.findById(VCType.RED).get());
                deckToUpdate.setVoteCards(newVotes);
            }
            rep.save(deckToUpdate);
        }
    }

    @Transactional
    public void consulRotation (Game game) { //rota solo el consul (para segunda ronda, lo comentado creo que sobra)
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        Player consulPlayer = getDecks().stream()
            .filter(x -> x.getGame() == game).filter(y -> y.getRoleCard() == RoleCard.CONSUL).findFirst().get().getPlayer();
        Integer consulId = players.indexOf(consulPlayer);
        Deck oldConsulDeck = getDeckByPlayerAndGame(consulPlayer, game);
        Deck newConsulDeck = getDeckByPlayerAndGame(players.get((consulId + 1) % (players.size())), game);
        /*Deck oldEdil1 = getDeckByPlayerAndGame(players.get((consulId + 2) % (players.size())), game);
        Deck oldEdil2 = getDeckByPlayerAndGame(players.get((consulId + 3) % (players.size())), game);*/ //esto creo que al final sobra
        oldConsulDeck.setRoleCard(RoleCard.NO_ROL);
        newConsulDeck.setRoleCard(RoleCard.CONSUL);

        clearEdilVoteCards(game);

        rep.save(oldConsulDeck);
        rep.save(newConsulDeck);

    }

    @Transactional
    public List<Player> pretorCandidates (Game actualGame) {
        
        List<Player> candidates = playerInfoRepository.findPlayersByGame(actualGame);

        Deck consulDeck = playerInfoRepository.findPlayersByGame(actualGame)
            .stream().map(x -> getDeckByPlayerAndGame(x, actualGame))
            .filter(y -> y.getRoleCard().equals(RoleCard.CONSUL)).findFirst().get();
            
            candidates.remove(consulDeck.getPlayer());
            return candidates;
    }

    @Transactional
    public List<Player> edil1Candidates (Game actualGame) {

        List<Player> candidates = playerInfoRepository.findPlayersByGame(actualGame);

        Deck consulDeck = playerInfoRepository.findPlayersByGame(actualGame)
            .stream().map(x -> getDeckByPlayerAndGame(x, actualGame))
            .filter(y -> y.getRoleCard().equals(RoleCard.CONSUL)).findFirst().get();

        List<Deck> edilDecks = candidates.stream()
            .map(x -> getDeckByPlayerAndGame(x, actualGame))
            .filter(y -> y.getRoleCard().equals(RoleCard.EDIL)).collect(Collectors.toList());

        candidates.remove(consulDeck.getPlayer());
        edilDecks.forEach(x -> candidates.remove(x.getPlayer()));
    
        return candidates;
    }

    @Transactional
    public List<Player> edil2Candidates (Game actualGame) {

        if (actualGame.getNumPlayers() == 5) {
            return pretorCandidates(actualGame);
        }

        else {
        return edil1Candidates(actualGame);
    }
    }

    @Transactional //NO SE QUE HACE ESTO AQUI, creo que sobra
    public List<Player> pretorRotation (Game game, Player newPretor, List<Player> candidates) {
        
        Player oldPretor = getDecks().stream()
            .filter(x -> x.getGame() == game).filter(y -> y.getRoleCard() == RoleCard.PRETOR).findFirst().get().getPlayer();

        Deck  oldPretorDeck = getDeckByPlayerAndGame(oldPretor, game);
        Deck newPretorDeck = getDeckByPlayerAndGame(newPretor, game);

        oldPretorDeck.setRoleCard(RoleCard.NO_ROL);
        newPretorDeck.setRoleCard(RoleCard.PRETOR);

        saveDeck(oldPretorDeck);
        saveDeck(newPretorDeck);

        candidates.remove(newPretor);
        return candidates;

    }

    @Transactional
    public void rolesDesignationSecondRound (Game game, Integer pretorId, Integer edil1Id, Integer edil2Id) {
        Deck newPretorDeck = getDeckByPlayerAndGame(playerRepository.findById(pretorId).get(), game);
        Deck newEdil1Deck = getDeckByPlayerAndGame(playerRepository.findById(edil1Id).get(), game);
        Deck newEdil2Deck = getDeckByPlayerAndGame(playerRepository.findById(edil2Id).get(), game);
        List<VoteCard> votes = new ArrayList<>();
        votes.add(voteCardRepository.findById(VCType.GREEN).get());
        votes.add(voteCardRepository.findById(VCType.RED).get());
        votes.add(voteCardRepository.findById(VCType.YELLOW).get());
        
        newPretorDeck.setRoleCard(RoleCard.PRETOR);
        newEdil1Deck.setRoleCard(RoleCard.EDIL);
        newEdil1Deck.setVoteCards(votes);
        newEdil2Deck.setRoleCard(RoleCard.EDIL);
        newEdil2Deck.setVoteCards(votes);

        saveDeck(newPretorDeck);
        saveDeck(newEdil1Deck);
        saveDeck(newEdil2Deck);

    }

    @Transactional
    public void clearDecks (Game game) {
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        players.removeIf(x -> getDeckByPlayerAndGame(x, game).getRoleCard() == RoleCard.CONSUL);

        players.forEach(x -> getDeckByPlayerAndGame(x, game).setRoleCard(RoleCard.NO_ROL));
        players.forEach(x -> clearDeckVoteCards(getDeckByPlayerAndGame(x, game)));

    }

    @Transactional
    public List<Player> winnerPlayers (Game game, Faction winnerFaction) {
        FactionCard winnerFactionCard;

        if (winnerFaction == Faction.LOYALS) {
            winnerFactionCard = factionCardRepository.findById(FCType.LOYAL).get();
        }
        else if (winnerFaction == Faction.TRAITORS) {
            winnerFactionCard = factionCardRepository.findById(FCType.TRAITOR).get();
        }
        else {
            winnerFactionCard = factionCardRepository.findById(FCType.MERCHANT).get();
        }

        List<Player> winnerPlayers = getDecks().stream()
			.filter(d -> d.getGame() == game).filter(d -> d.getFactionCards().contains(winnerFactionCard))
            .map(d -> d.getPlayer()).collect(Collectors.toList());

        if (winnerPlayers.size() == 0) {
            game.setWinners(Faction.MERCHANTS); //si no hay ganadores ganan merchants y obtengo winner player de merchants
            gameRepository.save(game);
            winnerPlayers = getDecks().stream() //decks de un game se podria hacer por query (de hecho creo que se deberia)
			.filter(d -> d.getGame() == game).filter(d -> d.getFactionCards().contains(factionCardRepository.findById(FCType.MERCHANT).get()))
            .map(d -> d.getPlayer()).collect(Collectors.toList());
        }

        return winnerPlayers;
    
    }

    @Transactional
    public List<Player> loserPlayers (Game game, List<Player> winnerPlayers) {
        List<Player> loserPlayers = playerInfoRepository.findPlayersByGame(game);
        winnerPlayers.forEach(p -> loserPlayers.remove(p));
        return loserPlayers;
    }

    
    @Transactional
    public boolean votesAsigned (Game game) {
        List<Player> players = playerInfoRepository.findPlayersByGame(game);
        List<Deck> gameDecks= players.stream().map(x -> getDeckByPlayerAndGame(x, game)).collect(Collectors.toList());
        return gameDecks.stream().anyMatch(x -> x.getVoteCards().size() != 0);

    }

}
