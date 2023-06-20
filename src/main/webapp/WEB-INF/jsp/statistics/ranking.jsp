<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%! int x = 5; %>

<petclinic:layout pageName="ranking">
    <h2>Ranking</h2>
    <h4>Top 10 players by number of victories</h4>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Player</th>
                <th>Victories</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${rankingMap}" var="entry">
                <tr>
                    <td>
                        <c:out value="${entry.key.user.username}"/>
                    </td>
                    <td>
                        <c:out value="${entry.value}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</petclinic:layout>