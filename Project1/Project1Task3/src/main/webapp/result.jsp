<%--
  Created by IntelliJ IDEA.
  Name: Kanishka Bhambhani
  Andrew Id: kbhambha
  The jsp file is used to print out the number of clicks performed by the user
  The user gets known of the number of clicks performed.
  The file does not display options if the vote is 0 for an option
  The file prints No options were selected for when no voting is done and results are checked
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%= request.getAttribute("doctype") %>
<html>
<head>
    <title>Class Clicker Results</title>
</head>
<body>
<h2><%= "Distributed Systems Class Clicker" %>
</h2>
    <c:choose>
        <c:when test="${empty clicks}">
            There are currently no results
        </c:when>
        <c:otherwise>
            The results from the survey are as follows <br>
            <c:forEach var="entry" items="${clicks}">
                ${entry.key}: ${entry.value}<br>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</body>
</html>
