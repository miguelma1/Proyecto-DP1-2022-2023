<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="achievements">
    <jsp:body>
        <h2>
            <c:if test="${achievement['new']}">New </c:if> Achievement
        </h2>
        <form:form modelAttribute="achievement"
                   class="form-horizontal">
            <input type="hidden" name="id" value="${achievement.id}"/>
            <div class="form-group has-feedback">                
                <petclinic:inputField label="Name" name="name"/>
                <petclinic:selectField label="Type" name="type" names="${types}" size="5"/>
                <petclinic:inputField label="Description" name="description"/>
                <petclinic:inputField label="Threshold" name="threshold"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <c:choose>
                        <c:when test="${achievement['new']}">
                            <button class="btn btn-default" type="submit">Create achievement</button>
                            <a class="btn btn-default" href="/achievements">Cancel</a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-default" type="submit">Update achievement</button>
                            <a class="btn btn-default" href="/achievements">Cancel</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </form:form>        
    </jsp:body>
</petclinic:layout>