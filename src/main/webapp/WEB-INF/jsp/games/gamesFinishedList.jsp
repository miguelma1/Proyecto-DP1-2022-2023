<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="games">
    <a href="${returnButton}"><span style="font-size: 25px" class="glyphicon glyphicon-menu-left" aria-hidden="true"></span></a>
    <h2>Public games history</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Started</th>
            <th>Duration</th>
            <th>Winners</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${publicGames}" var="game">
            
            <c:if test = "${gamesWinners[game].contains(player)}">
                <tr style="background-color:#35c32291">
            </c:if>
            <c:if test = "${!gamesWinners[game].contains(player)}">
                <tr style="background-color:#fe25256e">
            </c:if>
                <td>
                    <c:out value="${game.name}"/>
                </td>
                <td>
                    <c:out value="${game.numPlayers}/8"/>
                </td>
                <td>
                    <c:out value="${game.startDate}"/>
                </td>
                <td>
                    <c:out value="${game.getDuration()} mins"/>
                </td>
                <td>
                    <c:out value="${game.winners}"/>
                </td>
                <td>
                    <a class="btn btn-default" href="/games/${game.id}" height="120">Show game</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <h2>Private games history</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Started</th>
            <th>Duration</th>
            <th>Winners</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${privateGames}" var="game">
            <c:if test = "${gamesWinners[game].contains(player)}">
                <tr style="background-color:#35c32291">
            </c:if>
            <c:if test = "${!gamesWinners[game].contains(player)}">
                <tr style="background-color:#fe25256e">
            </c:if>

                <td>
                    <c:out value="${game.name}"/>
                </td>
                <td>
                    <c:out value="${game.numPlayers}/8"/>
                </td>
                <td>
                    <c:out value="${game.startDate}"/>
                </td>
                <td>
                    <c:out value="${game.getDuration()} mins"/>
                </td>
                <td>
                    <c:out value="${game.winners}"/>
                </td>
                <td>
                    <a class="btn btn-default" href="/games/${game.id}" height="120">Show Game</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
