<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix=
"spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix=
"petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="error">
    <spring:url value="/resources/images/error.gif" var="errorImage"/>
    <img src="${errorImage}"/>
    <h2>Oops!</h2>
    <p>Something went wrong</p>
</petclinic:layout>