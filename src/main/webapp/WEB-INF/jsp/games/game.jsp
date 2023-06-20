<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>


<petclinic:layout pageName="game">
    
    <h1><c:out value="${game.name}"/></h1>
    <c:if test = "${game.state == 'IN_PROCESS'}">
        <h1>${game.round} ROUND | TURN ${turn.currentTurn} | ${game.stage} STAGE</h1>
    </c:if>
    <c:if test = "${game.state == 'IN_PROCESS' && currentPlayerInfo.spectator}">
        <a class="btn btn-default" href="/games/${game.id}/exit" height="120">Stop watching</a>
    </c:if>
    <c:if test = "${game.state == 'FINISHED'}">
        <br>
        <c:if test = "${winnerPlayers.contains(currentPlayer)}">
            <h1 style="color:rgb(45, 125, 4); font-size:75px;"> <b>WINNER<b> </h1>
        </c:if>
    
        <c:if test = "${loserPlayers.contains(currentPlayer)}">
            <h1 style="color:red; font-size:75px;"> <b>LOSER</b> </h1>
        </c:if>
        <br>
    </c:if>

    <table id="suffragium" class="table table-striped">
        <h4>Suffragium</h4>
        <thead>
        <tr>
            <th>Loyals votes</th>
            <th>Traitor votes</th>
            <th>Vote limit</th>
        </tr>
        </thead>
        <tbody>
            <tr>
                <td>
                    <b><c:out value="${suffragiumCard.loyalsVotes}"/></b>
                    <img src="/resources/images/LoyalCrownIcon.PNG" width="30" height="30"/> 
                </td>
                <td>
                    <b><c:out value="${suffragiumCard.traitorsVotes}"/></b>
                    <img src="/resources/images/TraitorSwordIcon.jpg" width="30" height="20"/> 
                </td>
                <td>
                    <b><c:out value="${suffragiumCard.voteLimit}"/></b>
                </td>
            </tr>   
        </tbody>
    </table>
    <c:if test = "${game.state == 'IN_PROCESS'}">
        <h1>Waiting for: 
        <c:forEach items="${activePlayers}" var="activePlayer">
            <c:out value="${activePlayer} "/>
        </c:forEach>
        </h1>
        <c:if test = "${game.stage =='VOTING' && playerDeck.roleCard =='CONSUL' && votesAssigned == false}">
            <a class="btn btn-default" href="/games/${game.id}/rolesDesignation" height="120">Role designation</a>
        </c:if>
        <table id="decks" class="table table-striped">
            <thead>
            <tr>
                <th>Player</th>
                <th>Role Card</th>
                <th>Faction Cards</th>
                <th>Vote Cards</th>
            </tr>
            </thead>
            <tbody>
                <c:forEach items="${playerInfos}" var="playerInfo">
                        <tr>
                            <td>
                                <c:out value="${playerInfo.player.user.username}"/><br>
                            </td>
                            <td>
                                <c:forEach var="deck" items="${playerInfo.player.decks}">
                                    <c:if test="${deck.game.id == game.id}">
                                        <c:if test="${deck.roleCardImg != NO_ROLE}">
                                                <img src="${deck.getRoleCardImg()}" width="80" height="120" />                            
                                        </c:if>
                                    </c:if>
                                </c:forEach>
                                
                            </td>
                            <td>
                                <c:forEach var="deck" items="${playerInfo.player.decks}">
                                    <c:if test="${deck.game.id == game.id}">
                                        <c:choose>
                                            <c:when test="${deck.player.id == currentPlayer.id}">
                                                <c:forEach var="factions" items="${deck.factionCards}">
                                                    <c:if test="${game.stage =='END_OF_TURN' && playerDeck.roleCard =='CONSUL'}">
                                                        <a href="/games/${game.id}/edit/${factions.type}"> 
                                                            <img src="${factions.card}" width="80" height="120"/>                            
                                                        </a>
                                                    </c:if>
                                                    <c:if test="${game.stage != 'END_OF_TURN' || playerDeck.roleCard != 'CONSUL'}">
                                                        <img src="${factions.card}" width="80" height="120"/>
                                                    </c:if>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="factions" items="${deck.factionCards}">
                                                    <c:if test="${currentPlayerInfo.spectator==false}"> 
                                                        <img src="/resources/images/reverse_card.PNG" width="80" height="120"/>  
                                                    </c:if>
                                                    <c:if test="${currentPlayerInfo.spectator==true}"> 
                                                        <img src="${factions.card}" width="80" height="120"/>  
                                                    </c:if>                            
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                    </c:if>
                                </c:forEach>
                                
                            </td>
                            <td>
                                <c:forEach var="deck" items="${playerInfo.player.decks}">
                                    <c:if test="${deck.game.id == game.id}">
                                        <c:choose>
                                            <c:when test="${deck.player.id == currentPlayer.id}">
                                                <c:forEach var="votes" items="${deck.voteCards}">
                                                    <c:if test="${game.stage =='VOTING' && deck.voteCardsNumber > 1}">
                                                        <a href="/games/${game.id}/updateVotes/${votes.type}"> 
                                                            <img src="${votes.card}" width="80" height="120"/>                            
                                                        </a>
                                                    </c:if>
                                                    <c:if test="${game.stage != 'VOTING' || deck.voteCardsNumber == 1}">
                                                        <img src="${votes.card}" width="80" height="120"/>
                                                    </c:if>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <c:if test="${turn.voteCount == 2 && game.stage == 'VOTING' && deck.voteCards.size() > 1}">
                                                    <img src="/resources/images/SelectedYellowVote.PNG" width="80" height="120"/>
                                                </c:if>
                                                <c:forEach var="votes" items="${deck.voteCards}">
                                                    <c:if test="${playerDeck.roleCard =='PRETOR' && game.stage =='VETO'}">
                                                        <c:choose>
                                                            <c:when test="${votes.type == 'YELLOW'}">
                                                                <a href="/games/${game.id}/forcedVoteChange/${deck.player.id}"> 
                                                                    <img src="/resources/images/reverse_card.PNG" width="80" height="120"/>                            
                                                                </a>  
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="/games/${game.id}/pretorSelection/${votes.type}"> 
                                                                    <img src="/resources/images/reverse_card.PNG" width="80" height="120"/>                            
                                                                </a>  
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${playerDeck.roleCard !='PRETOR' || game.stage !='VETO'}">
                                                        <c:if test="${currentPlayerInfo.spectator==false}"> 
                                                            <img src="/resources/images/reverse_card.PNG" width="80" height="120"/>  
                                                        </c:if>
                                                        <c:if test="${currentPlayerInfo.spectator==true}"> 
                                                            <img src="${votes.card}" width="80" height="120"/>  
                                                        </c:if>
                                                    </c:if>                          
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                    
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test = "${game.state == 'FINISHED'}">
        <table id="decks" class="table table-striped">
            <thead>
                <tr>
                    <th>WINNER FACTION</th>
                    <th>WINNERS</th>
                    <th>LOSERS</th>
                    
                </tr>
                </thead>
            <body>
                <td>
                <h1>${game.winners}</h1><br>
                <c:if test = "${game.winners == 'LOYALS'}">
                    <img src="/resources/images/Loyal.PNG" width="200" height="300"/>          
                </c:if>
                <c:if test = "${game.winners == 'TRAITORS'}">
                    <img src="/resources/images/Traitor.PNG" width="200" height="300"/>          
                </c:if>
                <c:if test = "${game.winners == 'MERCHANTS'}">
                    <img src="/resources/images/Merchant.PNG" width="200" height="300"/>          
                </c:if>
                </td>
            <td>
                <c:forEach items="${winnerPlayers}" var="winner">
                    <b>${winner.user.username}</b> <span class="glyphicon glyphicon-arrow-right"></span> Selected faction: 
                    <c:forEach items="${winner.decks}" var="deck">
                        <c:if test = "${deck.game == game}">
                            <b>${deck.factionCards[0].type}</b><br>
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </td>
            <td>
                <c:forEach items="${loserPlayers}" var="loser">
                    <b>${loser.user.username}</b><span class="glyphicon glyphicon-arrow-right"></span> Selected faction: 
                    <c:forEach items="${loser.decks}" var="deck">
                        <c:if test = "${deck.game == game}">
                            <b>${deck.factionCards[0].type}</b><br>
                        </c:if>
                    </c:forEach>
                </c:forEach>

            </td>
        </body>
        </table>
        <br>
        <a class="btn btn-default" href="/">HOME</a>
    </c:if>
    <h4>Chat</h4>
    <c:if test = "${!currentPlayerInfo.spectator}">
        <a class="btn btn-default" href="/games/${game.id}/chat">Send message</a>
    </c:if>
    <table id="commentsTable" class="table table-striped">
        <thead>
            <tr>
                <th>User</th>
                <th>Message</th>
                <th>Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${comment}" var="comment">  
                <tr>
                    <td>
                        <c:out value="${comment.playerInfo.player.user.username}"/>
                    </td>
                    <td>
                        <c:out value="${comment.message}"/>
                    </td>
                    <td>
                        <c:out value="${comment.date}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</petclinic:layout>