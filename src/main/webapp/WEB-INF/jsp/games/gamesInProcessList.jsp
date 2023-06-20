<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="gamesInProcess">
    <a href="${returnButton}"><span style="font-size: 25px" class="glyphicon glyphicon-menu-left" aria-hidden="true"></span></a>
    <h2>Public games in process</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Started</th>
            <th>Round</th>
            <th>Turn</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${publicGames}" var="game">
                <tr>
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
                        <c:out value="${game.round}"/>
                    </td>
                    <td>
                        <c:out value="${game.turn.currentTurn}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h2>Private games in process</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Started</th>
            <th>Round</th>
            <th>Turn</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${privateGames}" var="game">
                <tr>
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
                        <c:out value="${game.round}"/>
                    </td>
                    <td>
                        <c:out value="${game.turn.currentTurn}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
