<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix=
"spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix=
"petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="error500">
    <spring:url value="/resources/images/error.gif" var="errorImage"/>
    <img src="${errorImage}"/>
    <h2>500 Internal Server Error</h2>
    <p>An internal server error has ocurred</p>
</petclinic:layout>