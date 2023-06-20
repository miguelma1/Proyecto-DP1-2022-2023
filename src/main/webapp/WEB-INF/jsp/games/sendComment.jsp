<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="comments">
    <jsp:body>
        <h2>
            Send a message
        </h2>
        <form:form modelAttribute="comment"
                   class="form-horizontal">
            <input type="hidden" name="id" value="${comment.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Message" name="message"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit">Send</button>
                    <a class="btn btn-default" href="/games/${gameId}">Cancel</a>
                </div>
            </div>
        </form:form>
    </jsp:body>
</petclinic:layout>
