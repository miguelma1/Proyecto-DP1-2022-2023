package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.samples.petclinic.comment.Comment;
import org.springframework.samples.petclinic.comment.CommentService;
import org.springframework.samples.petclinic.deck.Deck;
import org.springframework.samples.petclinic.deck.DeckService;
import org.springframework.samples.petclinic.deck.VoteCard;
import org.springframework.samples.petclinic.deck.VoteCardService;
import org.springframework.samples.petclinic.deck.FactionCard.FCType;
import org.springframework.samples.petclinic.deck.VoteCard.VCType;
import org.springframework.samples.petclinic.enums.CurrentRound;
import org.springframework.samples.petclinic.enums.CurrentStage;
import org.springframework.samples.petclinic.enums.State;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.playerInfo.PlayerInfo;
import org.springframework.samples.petclinic.playerInfo.PlayerInfoService;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCard;
import org.springframework.samples.petclinic.suffragiumCard.SuffragiumCardService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GameController {

	private static final String GAMES_STARTING_LIST = "/games/gamesStartingList";
	private static final String GAMES_IN_PROCESS_LIST = "/games/gamesInProcessList";
	private static final String GAMES_FINISHED_LIST = "/games/gamesFinishedList";
	private static final String GAMES_FINISHED_LIST_ADMIN = "/games/gamesFinishedListAdmin";
    private static final String FIND_GAMES_HISTORY = "/games/findGamesHistory";
	private static final String FIND_GAMES_PLAYER_HISTORY = "/games/findGamesPlayerHistory";
    private static final String FIND_GAMES_IN_PROCESS = "/games/findGamesInProcess";
    private static final String FIND_GAMES_STARTING = "/games/findGamesStarting";
    private static final String CREATE_GAME = "/games/createGame";
	private static final String GAME_LOBBY = "/games/gameLobby";
	private static final String GAME = "/games/game";
	private static final String PRETOR_SELECTION = "games/pretorCardSelection";
	private static final String ROLE_DESIGNATION = "games/rolesDesignation";
	private static final String SEND_COMMENT = "games/sendComment";

	private static final Integer MAX_PLAYERS = 8;

    @Autowired
    private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private PlayerInfoService playerInfoService;

	@Autowired
	private SuffragiumCardService suffragiumCardService;

	@Autowired
	private DeckService deckService;

	@Autowired
	private TurnService turnService;

	@Autowired
	private VoteCardService voteCardService;

	@Autowired
    private CommentService commentService;


    @Autowired
    public GameController(GameService service) {
        this.gameService = service;
    }

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

    @GetMapping(value = "/games/history/find")
	public String gamesHistoryForm(Map<String, Object> model) {
		model.put("game", new Game());
		return FIND_GAMES_HISTORY;
	}

    @GetMapping(value = "/games/history")
	public ModelAndView processGamesHistoryForm(Game game, BindingResult result) {

		// allow parameterless GET request for /games to return all records
		if (game.getName() == null) {
			game.setName(""); // empty string signifies broadest possible search
			log.warn("Null string input changed to empty string");
		}

		// find games by name
		List<Game> publicGames = this.gameService.getPublicGamesByNameAndState(game.getName(), State.FINISHED);
		List<Game> privateGames = this.gameService.getPrivateGamesByNameAndState(game.getName(), State.FINISHED);
		if (publicGames.isEmpty() && privateGames.isEmpty()) {
			log.warn("No games found");
			result.rejectValue("name", "notFound", "not found");
			return new ModelAndView(FIND_GAMES_HISTORY);
		}
		else {
			// games found
			ModelAndView res = new ModelAndView(GAMES_FINISHED_LIST_ADMIN);
			res.addObject("returnButton", "/games/history/find");
            res.addObject("publicGames", publicGames); 
			res.addObject("privateGames", privateGames); 
			return res;
		}
	}

    @GetMapping(value = "/games/playerHistory/find")
	public String gamesHistoryByPlayerForm(Map<String, Object> model) {
		model.put("game", new Game());
		return FIND_GAMES_PLAYER_HISTORY;
	}

    @GetMapping(value = "/games/playerHistory")
	public ModelAndView processGamesHistoryByPlayerForm(@AuthenticationPrincipal UserDetails user, Game game, BindingResult result) {
		if (game.getName() == null) {
			game.setName("");
			log.warn("Null string input changed to empty string");
		}

		Player player = playerService.getPlayerByUsername(user.getUsername());
		List<Game> publicGames = gameService.getPlayerGamesHistory(game.getName(), player, true);
		List<Game> privateGames = gameService.getPlayerGamesHistory(game.getName(), player, false);
		if (publicGames.isEmpty() && privateGames.isEmpty()) {
			log.warn("No games found");
			result.rejectValue("name", "notFound", "not found");
			return new ModelAndView(FIND_GAMES_PLAYER_HISTORY);
		}
		else {
			ModelAndView res = new ModelAndView(GAMES_FINISHED_LIST);
			res.addObject("gamesWinners", gameService.winnersByGame());
			res.addObject("player", player);
			res.addObject("returnButton", "/games/playerHistory/find");
            res.addObject("publicGames", publicGames); 
			res.addObject("privateGames", privateGames);
			return res;
		}
	}

    @GetMapping(value = "/games/inProcess/find")
	public String gamesInProcessForm(Map<String, Object> model) {
		model.put("game", new Game());
		return FIND_GAMES_IN_PROCESS;
	}

    @GetMapping(value = "/games/inProcess")
	public ModelAndView processGamesInProcessForm(Game game, BindingResult result) {
		if (game.getName() == null) {
			game.setName("");
			log.warn("Null string input changed to empty string");
		}

		List<Game> publicGames = this.gameService.getPublicGamesByNameAndState(game.getName(), State.IN_PROCESS);
		List<Game> privateGames = this.gameService.getPrivateGamesByNameAndState(game.getName(), State.IN_PROCESS);
		if (publicGames.isEmpty() && privateGames.isEmpty()) {
			log.warn("No games found");
			result.rejectValue("name", "notFound", "not found");
			return new ModelAndView(FIND_GAMES_IN_PROCESS);
		}
		else {
			ModelAndView res = new ModelAndView(GAMES_IN_PROCESS_LIST);
			res.addObject("returnButton", "/games/inProcess/find");
            res.addObject("publicGames", publicGames); 
			res.addObject("privateGames", privateGames);
			return res;
		}
	}

    @GetMapping(value = "/games/starting/find")
	public String gamesStartingForm(Map<String, Object> model) {
		model.put("game", new Game());
		return FIND_GAMES_STARTING;
	}

    @GetMapping(value = "/games/starting")
	public ModelAndView processGamesStartingForm(Game game, BindingResult result, @AuthenticationPrincipal UserDetails user) {
		if (game.getName() == null) {
			game.setName("");
			log.warn("Null string input changed to empty string");
		}

		Player player = playerService.getPlayerByUsername(user.getUsername());
		List<Game> publicGames = this.gameService.getPublicGamesByNameAndState(game.getName(), State.STARTING);
		List<Game> friendsGames = this.gameService.getFriendGamesByNameAndState(game.getName(), State.STARTING, player);

		if (publicGames.isEmpty() && friendsGames.isEmpty()) {
			log.warn("No games found");
			result.rejectValue("name", "notFound", "not found");
			return new ModelAndView(FIND_GAMES_STARTING);
		}
		else {
			ModelAndView res = new ModelAndView(GAMES_STARTING_LIST);
			res.addObject("returnButton", "/games/starting/find");
            res.addObject("publicGames", publicGames); 
			res.addObject("friendsGames", friendsGames); 
			return res;
		}
	}

    @GetMapping("/games/create")
    public ModelAndView createGameForm() {
        ModelAndView res = new ModelAndView(CREATE_GAME);
        Game game = new Game();       
        res.addObject("game", game);                                
        return res;
    }

	@PostMapping("/games/create")
	public String createGame(@AuthenticationPrincipal UserDetails user, @Valid PlayerInfo creatorInfo, 
	@Valid Game game, BindingResult br, ModelMap model) {
		if(br.hasErrors()) {
			log.error("Input value error");
			return CREATE_GAME;
		} else {
			Turn turn = new Turn();
			turnService.save(turn);

			gameService.saveGame(game, turn);

			Player creator = playerService.getPlayerByUsername(user.getUsername());
			playerInfoService.saveCreatorInfo(creatorInfo, game, creator);
			log.info("Game created");
			model.put("game", game);
        	model.put("playerInfos", playerInfoService.getPlayerInfosByGame(game));
			
        	return "redirect:/games/" + game.getId() + "/lobby";
		}
	}

    @GetMapping("/games/{gameId}/lobby")
    public ModelAndView showLobby(@PathVariable("gameId") Integer gameId, @AuthenticationPrincipal UserDetails user, HttpServletResponse response){
		response.addHeader("Refresh", "3");
        ModelAndView res=new ModelAndView(GAME_LOBBY);
        Game game=gameService.getGameById(gameId);
		Player currentPlayer = playerService.getPlayerByUsername(user.getUsername());
		PlayerInfo currentPlayerInfo = playerInfoService.getPlayerInfoByGameAndPlayer(game, currentPlayer);
		if(game.getState() == State.IN_PROCESS) {
			log.info("Redirecting players from game " + game.getId());
			return new ModelAndView("redirect:/games/" + game.getId().toString());
		}
        res.addObject("game", game);
        res.addObject("playerInfos", playerInfoService.getPlayerInfosByGame(game));
		res.addObject("currentPlayerInfo", currentPlayerInfo);
        return res;
    }

	@GetMapping("/games/{gameId}/join")
    public String joinGame(@AuthenticationPrincipal UserDetails user, @PathVariable("gameId") Integer gameId, @Valid PlayerInfo joinedInfo, ModelMap model){
		Game game=gameService.getGameById(gameId);
		Player player=playerService.getPlayerByUsername(user.getUsername());
		if(playerInfoService.getAllUsersByGame(game).contains(player)) {
			log.warn("Player was already in the game");
			model.put("message", "You are already in this game!");
			return gamesStartingForm(model);
		}
		if(game.getNumPlayers() == MAX_PLAYERS) {
			log.warn("Couldn't join because maximum number of players has been reached");
			model.put("message", "This game has reached the maximum number of players!");
			return gamesStartingForm(model);
		}
		gameService.joinGame(game);
		playerInfoService.savePlayerInfo(joinedInfo, game, player);
		log.info("Player joined");
		model.put("game", game);
        model.put("playerInfos", playerInfoService.getPlayerInfosByGame(game));
        return "redirect:/games/" + gameId.toString() + "/lobby";
    }

	@GetMapping("/games/{gameId}/spectate")
    public String spectateGame(@AuthenticationPrincipal UserDetails user, @PathVariable("gameId") Integer gameId, @Valid PlayerInfo spectatorInfo, ModelMap model){
		Game game=gameService.getGameById(gameId);
		Player player=playerService.getPlayerByUsername(user.getUsername());
		if(playerInfoService.getAllUsersByGame(game).contains(player)) {
			log.warn("Player was already in the game");
			model.put("message", "You are already in this game!");
			return gamesStartingForm(model);
		}
		playerInfoService.saveSpectatorInfo(spectatorInfo, game, player);
		log.info("Spectator joined");
		model.put("game", game);
        model.put("playerInfos", playerInfoService.getPlayerInfosByGame(game));
        return "redirect:/games/" + gameId.toString() + "/lobby";
    }

	@GetMapping("/games/{gameId}/exit")
    public String exitGame(@AuthenticationPrincipal UserDetails user, @PathVariable("gameId") Integer gameId, ModelMap model){
		Game game = gameService.getGameById(gameId);
		Player player = playerService.getPlayerByUsername(user.getUsername());
		PlayerInfo playerInfo = playerInfoService.getPlayerInfoByGameAndPlayer(game, player);
		try{
            gameService.exitGame(playerInfo, game);
            log.info("PlayerInfo deleted");   
            model.put("message", "You left the game successfully!");     
        } catch(EmptyResultDataAccessException e) {
            log.warn("Not existing playerInfo");
            model.put("message", "You are not in that game");
        }
        return "redirect:/";
    }
 
    @GetMapping("/games/{gameId}")
    public ModelAndView showGame(@PathVariable("gameId") Integer gameId, @AuthenticationPrincipal UserDetails user, HttpServletResponse response) throws DataAccessException {
        response.addHeader("Refresh", "2");
		ModelAndView res=new ModelAndView(GAME);
        Game game=gameService.getGameById(gameId);
        SuffragiumCard suffragiumCard = suffragiumCardService.createSuffragiumCardIfNeeded(game);
		Player currentPlayer = playerService.getPlayerByUsername(user.getUsername());
		Game gameStarted = gameService.startGameIfNeeded(game, suffragiumCard);
		Turn currentTurn = gameStarted.getTurn();
		List<PlayerInfo> gamePlayerInfos = playerInfoService.getActivePlayersPlayerInfosByGame(game);
    	deckService.assingDecksIfNeeded(game);

		Integer roleCardNumber = gameService.gameRoleCardNumber(game);

		if(!playerInfoService.isSpectator(currentPlayer, gameStarted)){
			Deck playerDeck = deckService.getDeckByPlayerAndGame(currentPlayer, game);
			res.addObject("playerDeck", playerDeck);
		}

		if (game.getState() == State.FINISHED) {
			gameService.winnerFaction(game);
			List<Player> winnerPlayers = deckService.winnerPlayers(game, game.getWinners());
			List<Player> losePlayers = deckService.loserPlayers(gameStarted, winnerPlayers);
			res.addObject("winnerPlayers", winnerPlayers);
			res.addObject("loserPlayers", losePlayers);

		}

		res.addObject("currentPlayerInfo", playerInfoService.getPlayerInfoByGameAndPlayer(game, currentPlayer));
		res.addObject("activePlayers", gameService.activePlayers(game));
		res.addObject("votesAssigned", deckService.votesAsigned(game));
		res.addObject("roleCardNumber", roleCardNumber);
		res.addObject("turn", currentTurn);
		res.addObject("currentPlayer", currentPlayer);
		res.addObject("currentPlayerInfo", playerInfoService.getPlayerInfoByGameAndPlayer(gameStarted, currentPlayer));
        res.addObject("game", gameStarted);
        res.addObject("playerInfos", gamePlayerInfos);
		res.addObject("suffragiumCard", suffragiumCardService.getSuffragiumCardByGame(gameId));
		res.addObject("comment", commentService.getCommentsByGame(gameId));
        return res;

    }

	@GetMapping("/games/{gameId}/pretorSelection/{voteType}")
	public ModelAndView pretorSelection(@PathVariable("gameId") Integer gameId, @PathVariable("voteType") VCType voteType) {
		ModelAndView res = new ModelAndView(PRETOR_SELECTION);
		VoteCard selectedCard = voteCardService.getById(voteType);
		List <VoteCard> changeOptions = voteCardService.getChangeOptions(gameService.getGameById(gameId), selectedCard);
		
		res.addObject("game", gameService.getGameById(gameId));
		res.addObject("selectedCard", selectedCard);
		res.addObject("changeOptions", changeOptions);
		
		return res;
	}

	@GetMapping("/games/{gameId}/forcedVoteChange/{playerId}")
	public String forcedVoteChange(@PathVariable("gameId") Integer gameId,@PathVariable("playerId") Integer playerId){
		Game actualGame = gameService.getGameById(gameId);
		Player voter = playerService.getPlayerById(playerId);
		
		voteCardService.forcedVoteChange(actualGame, voter);
		return "redirect:/games/" + gameId.toString();

	}

	@GetMapping("/games/{gameId}/pretorSelection/{voteType}/{changedVoteType}")
	public String pretorChange(@PathVariable("gameId") Integer gameId, @PathVariable("voteType") VCType voteType, 
				@PathVariable("changedVoteType") VCType changedVoteType) {
		Game game = gameService.getGameById(gameId);
		turnService.pretorVoteChange(voteType, changedVoteType, gameService.getGameById(gameId));
		gameService.changeStage(game, CurrentStage.SCORING);
		updateSuffragiumCard(gameId);
		return "redirect:/games/" + gameId.toString();
		
	}

	@GetMapping("/games/{gameId}/updateSuffragium")
	public String updateSuffragiumCard(@PathVariable("gameId") Integer gameId) {
		Game currentGame = gameService.getGameById(gameId);
		Turn currentTurn = currentGame.getTurn();
		suffragiumCardService.updateVotes(currentGame.getSuffragiumCard(), currentTurn, gameId);

		if (currentTurn.getCurrentTurn() == 1 && currentGame.getRound() == CurrentRound.FIRST ) {
			deckService.deckRotation(currentGame);
			gameService.changeStage(currentGame, CurrentStage.VOTING);
		}

		else if (currentTurn.getCurrentTurn() != 1 && currentGame.getRound() == CurrentRound.SECOND) {
			deckService.clearEdilVoteCards(currentGame);
			deckService.consulRotation(currentGame);
			gameService.changeStage(currentGame, CurrentStage.VOTING);
		}
		else {
			gameService.changeStage(currentGame, CurrentStage.END_OF_TURN);
		}
		return "redirect:/games/" + gameId.toString();
	}

	@GetMapping("/games/{gameId}/updateVotes/{voteType}")
	public String updateTurnVotes(@PathVariable("gameId") Integer gameId, @PathVariable("voteType") VCType voteType, @AuthenticationPrincipal UserDetails user) {
		Game currentGame = gameService.getGameById(gameId);
		Turn currentTurn = currentGame.getTurn();
		Player currentPlayer = playerService.getPlayerByUsername(user.getUsername());
		Deck deck = deckService.getDeckByPlayerAndGame(currentPlayer, currentGame);

		turnService.updateTurnVotes(currentTurn, voteType);
		gameService.changeStageIfVotesCompleted(currentGame);
		deckService.updateVotesDeck(deck, voteType);
		if (currentGame.getStage() == CurrentStage.SCORING) {
			updateSuffragiumCard(gameId);
		}
		return "redirect:/games/" + gameId.toString();
	}

    @GetMapping("/games/{gameId}/edit/{factionType}")
    public String selectFaction (@PathVariable("gameId") Integer gameId, @PathVariable("factionType") FCType factionType, @AuthenticationPrincipal UserDetails user){
        Game game = gameService.getGameById(gameId);
		Player player = playerService.getPlayerByUsername(user.getUsername());
        Deck deck = deckService.getDeckByPlayerAndGame(player, game);

        deckService.updateFactionDeck(deck, factionType);

		if (game.getRound() == CurrentRound.FIRST && game.getTurn().getCurrentTurn() != game.getNumPlayers()) {
			deckService.deckRotation(game);
		}
		
		else {
			deckService.clearEdilVoteCards(game);
			deckService.consulRotation(game);
		}
		gameService.changeStage(game, CurrentStage.VOTING);
		
        return "redirect:/games/" + gameId.toString();
    }

	@GetMapping("/games/{gameId}/rolesDesignation")
    public ModelAndView rolesDesignation(@PathVariable("gameId") Integer gameId) {
		ModelAndView res = new ModelAndView(ROLE_DESIGNATION);
		List<Player> pretorCandidates = deckService.pretorCandidates(gameService.getGameById(gameId));
		List<Player> edil1Candidates = deckService.edil1Candidates(gameService.getGameById(gameId));
		List<Player> edil2Candidates = deckService.edil2Candidates(gameService.getGameById(gameId));
		Game currentGame = gameService.getGameById(gameId);

		deckService.clearDecks(currentGame);

		res.addObject("currentGame", currentGame);
		res.addObject("pretorCandidates", pretorCandidates);
		res.addObject("edil1Candidates", edil1Candidates);
		res.addObject("edil2Candidates", edil2Candidates);
		return res;
	}

	@GetMapping("/games/{gameId}/rolesDesignation/{pretorId}/{edil1Id}/{edil2Id}")
    public String finalRolesDesignation(@PathVariable("gameId") Integer gameId, @PathVariable("pretorId") Integer pretorId,
											@PathVariable("edil1Id") Integer edil1Id, @PathVariable("edil2Id") Integer edil2Id) {
		
		Game actualGame = gameService.getGameById(gameId);

		deckService.rolesDesignationSecondRound(actualGame, pretorId, edil1Id, edil2Id);


		return "redirect:/games/" + gameId;
		
	}	


	@GetMapping("/games/{gameId}/chat")
    public ModelAndView sendComment(@PathVariable("gameId") Integer gameId) {
        Comment comment=new Comment();
		ModelAndView result = new ModelAndView(SEND_COMMENT);
		result.addObject("gameId", gameId);
        result.addObject("comment", comment);
        return result;
    }

	@PostMapping("/games/{gameId}/chat")
    public ModelAndView saveComment(@PathVariable("gameId") Integer gameId, @Valid Comment comment, BindingResult br, @AuthenticationPrincipal UserDetails user) {
		Player player = playerService.getPlayerByUsername(user.getUsername());
		Game game = gameService.getGameById(gameId);
		PlayerInfo playerInfo = playerInfoService.getPlayerInfoByGameAndPlayer(game, player);
		if(br.hasErrors()) {
			ModelAndView res = new ModelAndView(SEND_COMMENT, br.getModel());
			res.addObject("gameId", gameId);
			res.addObject("comment", comment);
            return res;
        } else {
            commentService.saveComment(comment, playerInfo);
        }
        return new ModelAndView("redirect:/games/" + gameId.toString());
    }

	@GetMapping("/gamesResult/{gameId}")
    public ModelAndView showGameResult(@PathVariable("gameId") Integer gameId, @AuthenticationPrincipal UserDetails user) throws DataAccessException {
		ModelAndView res=new ModelAndView(GAME);
        Game game=gameService.getGameById(gameId);
        SuffragiumCard suffragiumCard = suffragiumCardService.createSuffragiumCardIfNeeded(game);
		Player currentPlayer = playerService.getPlayerByUsername(user.getUsername());
		Game gameStarted = gameService.startGameIfNeeded(game, suffragiumCard);
		Turn currentTurn = gameStarted.getTurn();
		List<PlayerInfo> gamePlayerInfos = playerInfoService.getPlayerInfosByGame(game);
    	deckService.assingDecksIfNeeded(game);
		Integer roleCardNumber = gameService.gameRoleCardNumber(game);
		gameService.winnerFaction(game);
		List<Player> winnerPlayers = deckService.winnerPlayers(game, game.getWinners());
		List<Player> losePlayers = deckService.loserPlayers(gameStarted, winnerPlayers);

		res.addObject("winnerPlayers", winnerPlayers);
		res.addObject("loserPlayers", losePlayers);
		res.addObject("activePlayers", gameService.activePlayers(game));
		res.addObject("votesAssigned", deckService.votesAsigned(game));
		res.addObject("roleCardNumber", roleCardNumber);
		res.addObject("turn", currentTurn);
		res.addObject("currentPlayer", currentPlayer);
        res.addObject("game", gameStarted);
        res.addObject("playerInfos", gamePlayerInfos);
		res.addObject("suffragiumCard", suffragiumCardService.getSuffragiumCardByGame(gameId));
        return res;

    }

}
