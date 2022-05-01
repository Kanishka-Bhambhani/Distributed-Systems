<%--
  Created by IntelliJ IDEA.
  Name: Kanishka Bhambhani
  Andrew Id: kbhambha
  This file is used to display the ui to the user.
  The user is asked to enter a string that needs to be encrypted
  The values of the string encryptions are then printed on the same page along with the string
  Default radio button for encryption is given as SHA-256
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Compute Hash</title>
</head>
<body>
<h2><%= "Computing Hash" %>
</h2>
<form action="ComputeHashes">
    <label for="string">Input String:</label><br>
    <input type="text" id="string" name="string_value" value=""><br>
    <input type="radio" id="sha256" name="hash_input" value="SHA256" CHECKED>
    <label for="sha256">SHA-256</label><br>
    <input type="radio" id="md5" name="hash_input" value="MD5">
    <label for="md5">MD5</label><br>
    <input type="submit" value="Submit">
</form>
<br>
<c:choose>
<c:when test="${og_string != \"\" && og_string != null}">
String:  ${og_string} <br>
Hash in base 64:  ${base64} <br>
Hash in hex binary:  ${hex} <br>
</c:when>
<c:otherwise>
  Please enter a string.
</c:otherwise>
</c:choose>
</body>
</html>