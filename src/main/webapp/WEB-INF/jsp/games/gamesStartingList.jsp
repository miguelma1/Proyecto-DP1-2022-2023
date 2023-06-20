<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="gamesStarting">
    <a href="${returnButton}"><span style="font-size: 25px" class="glyphicon glyphicon-menu-left" aria-hidden="true"></span></a>
    <h2>Public games</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Join</th>
            <th>Spectate</th>
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
                        <sec:authorize access="hasAuthority('player')">
                            <a type="submit" class="glyphicon glyphicon-plus" href="/games/${game.id}/join"></a>
                        </sec:authorize>
                    </td>
                    <td>
                        <sec:authorize access="hasAuthority('player')">
                            <a type="submit" class="glyphicon glyphicon-eye-open" href="/games/${game.id}/spectate"></a>
                        </sec:authorize>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h2>Friend's games</h2>
    <table id="gamesTable" class="table table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Number of players</th>
            <th>Join</th>
            <th>Spectate</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${friendsGames}" var="game">
                <tr>
                    <td>
                        <c:out value="${game.name}"/>
                    </td>
                    <td>
                        <c:out value="${game.numPlayers}/8"/>
                    </td>
                    <td>
                        <sec:authorize access="hasAuthority('player')">
                            <a type="submit" class="glyphicon glyphicon-plus" href="/games/${game.id}/join"></a>
                        </sec:authorize>
                    </td>
                    <td>
                        <sec:authorize access="hasAuthority('player')">
                            <a type="submit" class="glyphicon glyphicon-eye-open" href="/games/${game.id}/spectate"></a>
                        </sec:authorize>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
