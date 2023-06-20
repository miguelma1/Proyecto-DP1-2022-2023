<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="playerAudit">
    <h2><c:out value="Player ${player.user.username} audit"></c:out></h2>
    <c:if test = "${revs.isEmpty()}">Nothing to audit yet</c:if>
    <c:forEach items="${revs}" var="rev">
        <c:out value = "${rev}"></c:out>
        <p></p>
    </c:forEach>
</petclinic:layout>
