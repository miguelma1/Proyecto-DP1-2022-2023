<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<petclinic:layout pageName="home">
    <sec:authorize access="!isAuthenticated()">
		<p>Log in or sing up to start playing</p>
	</sec:authorize>
    <sec:authorize access="hasAuthority('player')">
        
        <h3><c:out value="Number of friends online: ${numFriendsOnline}"/></h3>
        
        <a class="btn btn-default" href="/games/create">Create game</a>
        <a class="btn btn-default" href="/games/starting/find">Join a game</a>
        <a class="btn btn-default" href="/games/playerHistory/find">Your game history</a>
        <a class="btn btn-default" href="/players/edit">Change password</a>
    </sec:authorize>
    <sec:authorize access="hasAuthority('admin')">
        <a class="btn btn-default" href="/games/history/find">Games history</a>
        <a class="btn btn-default" href="/games/inProcess/find">Games in process</a>
        <a class="btn btn-default" href="/users/1">Manage players</a>
    </sec:authorize>
</petclinic:layout>
