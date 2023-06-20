<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix=
"spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix=
"petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="error404">
    <spring:url value="/resources/images/error.gif" var="errorImage"/>
    <img src="${errorImage}"/>
    <h2>404 Not found</h2>
    <p>This page does not exist</p>
    <a href="/">
        <span class="glyphicon glyphicon-home" aria-hidden="true"></span> Go home
    </a>
</petclinic:layout>