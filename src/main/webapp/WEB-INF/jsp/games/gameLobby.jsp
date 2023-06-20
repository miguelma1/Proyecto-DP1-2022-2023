<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="lobbies">
    <h2><c:out value="${game.name}"/> lobby</h2>
    <td>
        <c:out value="Number of players: ${game.numPlayers}/8"/>
    </td>
    <c:if test="${game.numPlayers >= 5 && game.numPlayers <= 8}">
        <c:choose>
            <c:when test="${currentPlayerInfo.creator}">
                <a class="btn btn-default" href="/games/${game.id}">Start game</a>
            </c:when>
            <c:otherwise>
                <p>Waiting for the creator to start the game</p>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${game.numPlayers < 5}">
        <p>Waiting for more players to start the game</p>
    </c:if>
    <p></p>
    <a class="btn btn-default" href="/gameInvitations/${game.id}/send">Invite friend</a>
    <c:if test="${!currentPlayerInfo.creator}">
        <a class="btn btn-default" href="/games/${game.id}/exit">Exit game</a>
    </c:if>
    <table id="creatorTable" class="table table-striped">
        <thead>
        <tr>
            <th>Creator</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${playerInfos}" var="playerInfo">
            <c:if test="${playerInfo.creator}">
                <tr>
                    <td>
                        <c:out value="${playerInfo.player.user.username}"/>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
    <table id="creatorTable" class="table table-striped">
        <thead>
        <tr>
            <th>Players</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${playerInfos}" var="playerInfo">
            <c:if test="${playerInfo.spectator == false}">
                <tr>
                    <td>
                        <c:out value="${playerInfo.player.user.username}"/>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
    <table id="creatorTable" class="table table-striped">
        <thead>
        <tr>
            <th>Spectators</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${playerInfos}" var="playerInfo">
            <c:if test="${playerInfo.spectator}">
                <tr>
                    <td>
                        <c:out value="${playerInfo.player.user.username}"/>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
