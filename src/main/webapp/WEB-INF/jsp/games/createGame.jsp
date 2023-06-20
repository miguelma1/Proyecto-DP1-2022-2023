<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="games">
    <jsp:body>
        <h2>
            Create a new game
        </h2>
        <form:form modelAttribute="game"
                   class="form-horizontal">
            <input type="hidden" name="id" value="${game.id}"/>
            <div class="form-group has-feedback">
                <petclinic:inputField label="Name" name="name"/>
                <tr>
                    <td><form:label path="publicGame">Public:</form:label></td>
                    <td><form:checkbox path="publicGame"/></td>
                </tr>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit">Create game</button>
                </div>
            </div>
        </form:form>
    </jsp:body>
</petclinic:layout>
