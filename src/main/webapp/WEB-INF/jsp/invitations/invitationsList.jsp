<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="invitations">
    <h2>Friendship invitations</h2>
    <div>
        <a class="btn btn-default" href="/invitations/send">Send friendship invitation</a>
    </div>
    <table id="invitationsTable" class="table table-striped">
        <thead>
        <tr>
            <th>From</th>
            <th>Message</th>
            <th>Accept</th>
            <th>Reject</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${invitations}" var="invitation">
            <c:if test="${!invitation.accepted}">
                <tr>
                    <td>
                        <c:out value="${invitation.sender.user.username}"/>
                    </td>
                    <td>
                        <c:out value="${invitation.message}"/>
                    </td>
                    <td>
                        <a href="/invitations/${invitation.id}/accept">
                            <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                        </a>
                    </td>
                    <td>
                        <a href="/invitations/${invitation.id}/reject">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                        </a>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>

    <h2>Game player invitations</h2>
    <table id="playerInvitationsTable" class="table table-striped">
        <thead>
        <tr>
            <th>From</th>
            <th>Game</th>
            <th>Message</th>
            <th>Accept</th>
            <th>Reject</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${playerInvitations}" var="invitation">
            <c:if test="${!invitation.accepted}">
                <tr>
                    <td>
                        <c:out value="${invitation.sender.user.username}"/>
                    </td>
                    <td>
                        <c:out value="${invitation.game.name}"/>
                    </td>
                    <td>
                        <c:out value="${invitation.message}"/>
                    </td>
                    <td>
                        <a href="/gameInvitations/${invitation.game.id}/${invitation.id}/acceptPlayer">
                            <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                        </a>
                    </td>
                    <td>
                        <a href="/invitations/${invitation.id}/reject">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                        </a>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>

    <h2>Game spectator invitations</h2>
    <table id="spectatorInvitationsTable" class="table table-striped">
        <thead>
        <tr>
            <th>From</th>
            <th>Game</th>
            <th>Message</th>
            <th>Accept</th>
            <th>Reject</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${spectatorInvitations}" var="invitation">
            <c:if test="${!invitation.accepted}">
                <tr>
                    <td>
                        <c:out value="${invitation.sender.user.username}"/>
                    </td>
                    <td>
                        <c:out value="${invitation.game.name}"/>
                    </td>
                    <td>
                        <c:out value="${invitation.message}"/>
                    </td>
                    <td>
                        <a href="/gameInvitations/${invitation.game.id}/${invitation.id}/acceptSpectator">
                            <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                        </a>
                    </td>
                    <td>
                        <a href="/invitations/${invitation.id}/reject">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                        </a>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
