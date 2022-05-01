<%--
    Created by IntelliJ IDEA.
    Name: Kanishka Bhambhani
    Andrew Id: kbhambha
    The jsp file is used to display the radio options to the user for a question
    The user selects an option and a string is prompted at the top to let him know which answer was selected.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%= request.getAttribute("doctype") %>
<!DOCTYPE html>
<html>
<head>
    <title>Class Clicker</title>
</head>
<body>
<h2><%= "Distributed Systems Class Clicker" %>
</h2>
<form action="DSClassClicker">
    <% if (request.getAttribute("ans") != null) { %>
    <label>Your "<%= request.getAttribute("ans") %>" has been registered.</label><br>
    <% }%>
    <label>Submit your answer to the current question:</label><br>
    <input type="radio" id="A" name="ds_input" value="A">
    <label for="A">A</label><br>
    <input type="radio" id="B" name="ds_input" value="B">
    <label for="B">B</label><br>
    <input type="radio" id="C" name="ds_input" value="C">
    <label for="C">C</label><br>
    <input type="radio" id="D" name="ds_input" value="D">
    <label for="D">D</label><br>
    <input type="submit" value="Submit">
</form>
</body>
</html>