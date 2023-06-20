<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="game">
    <h1> Assing each role card to an available player.</h1>
    <table id="option cards" class="table table-striped">
            
        <thead>
        <tr>
            <th>PRETOR SELECTION</th>
            <th>EDIL SELECTION</th>
            <th>EDIL SELECTION</th>
        </tr>
        </thead>
        <tbody>
                    <td>
                        <img src="/resources/images/Pretor.png" width="200" height="300"/>
                        <select name ="player" id = "pretorElection" onchange="pretorSelect();" size = 7>
                            <c:forEach items="${pretorCandidates}" var="pCandidate">
                                <option id = "pC" value ="${pCandidate.id}">${pCandidate.user.username}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td>
                        <img src="/resources/images/Edil.png" width="200" height="300"/>
                        <select name ="player" id = "edil1Election" onchange="edil1Select();" size = 7 disabled = true>
                            <c:forEach items="${edil1Candidates}" var="e1Candidate">
                                <option id = "e1C" value ="${e1Candidate.id}">${e1Candidate.user.username}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td>
                        <img src="/resources/images/Edil.png" width="200" height="300"/>
                        <select name ="player" id = "edil2Election" onchange="edil2Select();" size = 7 disabled = true>
                            <c:forEach items="${edil2Candidates}" var="e2Candidate">
                                <option id = "e2C" value ="${e2Candidate.id}">${e2Candidate.user.username}</option>
                            </c:forEach>
                        </select>
                    </td>

                </tr>
        </tbody>
            
    </table>



    <script type="text/javascript">
        var x = document.getElementById("pretorElection");
        var y = document.getElementById("edil1Election");
        var z = document.getElementById("edil2Election");

        function pretorSelect() {
            console.log(x);
        var selectedValue = x.options[x.selectedIndex].value;
        
        for (var i=0; i<y.length; i++) {
            if (y.options[i].value == selectedValue)
                y.remove(i);
        }
        for (var i=0; i<z.length; i++) {
            if (z.options[i].value == selectedValue)
                z.remove(i);
        }
        x.disabled = true;
        y.disabled = false;
        return selectedValue;
        }
        
        function edil1Select() {
        var selectedValue = y.options[y.selectedIndex].value;
        for (var i=0; i<z.length; i++) {
            if (z.options[i].value == selectedValue)
                z.remove(i);
        }
        y.disabled = true;
        z.disabled = false;
        return selectedValue;
        }
        function edil2Select() {
        var selectedValue = z.options[z.selectedIndex].value;
        window.location.replace("rolesDesignation/" + pretorSelect() + "/" + edil1Select() + "/" + selectedValue);
        }
        </script>
</petclinic:layout>