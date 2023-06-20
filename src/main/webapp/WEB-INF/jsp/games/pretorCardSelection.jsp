<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="game">
    <h4>VOTE CARD SELECTION:</h4>
    <h4>In case you want to change the vote, press the option you desire. Otherwise, if you prefer to mantain the vote, press the selected card.</h4>
    <table id="option cards" class="table table-striped">
        
        <thead>
        <tr>
            <th>Card selected</th>
            <th>Possible changes</th>
        </tr>
        </thead>
        <tbody>
            <tr>
                <td>
                    <a href="/games/${game.id}/pretorSelection/${selectedCard.type}/${selectedCard.type}"> 
                        <img src="${selectedCard.card}" width="100" height="150"/>                            
                    </a>
                    
                </td>

                <td>
                    <c:forEach items="${changeOptions}" var="option">
                        <a href="/games/${game.id}/pretorSelection/${selectedCard.type}/${option.type}" > 
                            <img src="${option.card}" width="100" height="150"/>                            
                        </a>
                    </c:forEach>
                </td>
            </tr>
            
        </tbody>
    </table>
</petclinic:layout>