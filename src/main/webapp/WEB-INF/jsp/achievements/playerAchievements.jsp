<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="achievements/player">
    <h2>Achievements</h2>
    <table id="progressTable" class="table table-striped">
        <thead>
        <tr>
            <th>Achievement</th>
            <th>Description</th>
            <th>Completed Percentage</th>
            <th>Completed</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${progress}" var="progress">
            <tr>
                <td>
                    <c:out value="${progress.achievement}"/>
                </td>
                <td>
                    <c:out value="${progress.achievement.actualDescription}"/>
                </td>
                <td>
                    <c:forEach items="${progressions}" var="progression"> 
                    <c:if test= "${progress.achievement == progression.getFirst()}">
                        <fmt:formatNumber type="number" maxFractionDigits="2" value="${progression.getSecond()}" />%
                    </c:if>
                </c:forEach>
                </td>
                <td>
                    <c:forEach items="${progressions}" var="progression"> 
                    <c:if test= "${progress.achievement == progression.getFirst()}">
                        <c:if test= "${progression.getSecond() == 100.0}">
                            <span style="color:rgb(0, 165, 33);font-weight:bold"><c:out value="Completed"/> </span>
                        </c:if>
                        <c:if test= "${progression.getSecond() != 100.0}">
                            <span style="color:rgb(204, 147, 4);font-weight:bold"><c:out value="In progress"/> </span>
                        </c:if>
                    </c:if>
                </c:forEach>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
 
</petclinic:layout>