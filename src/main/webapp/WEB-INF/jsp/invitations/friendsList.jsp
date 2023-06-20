<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="friends">
    <h2>Friends</h2>
    <div>
        <a class="btn btn-default" href="/invitations/send">Send friendship invitation</a>
    </div>
    <table id="invitationsTable" class="table table-striped">
        <thead>
        <tr>
            <th>Username</th>
            <th>Online</th>
            <th>Playing</th>
            <th>Cancel friendship</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${friendsInvitations}" var="friendInvitation">
            <tr>
                <td>
                    <c:out value="${friendInvitation.getFirst().user.username}"/>
                </td>
                <td>
                    <c:out value="${friendInvitation.getFirst().online}"/>
                </td>
                <td>
                    <c:out value="${friendInvitation.getFirst().playing}"/>
                </td>
                <td>
                    <a href="/invitations/${friendInvitation.getSecond().id}/cancelFriendship">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
