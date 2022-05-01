<%--
  Created by IntelliJ IDEA.
  Name: Kanishka Bhambhani
  Andrew Id: kbhambha
  This file is used to display the ui to the user.
  The user is asked to select a country from the given list
  And press the submit button in order to see the data related to that country
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Olympic Medal Prediction</title>
</head>
<body>
<%--<h1><%= "Hello World!" %>--%>
<%--</h1>--%>
<%--<br/>--%>
<%--<a href="GDPCalculator">Hello Servlet</a>--%>
<h1>Olympic Medal Prediction</h1>
<h3>Created By Kanishka Bhambhani</h3>
<h1>20 Largest Countries By GDP</h1>
<form action="GDPCalculator" >
    <label for="country">Choose the name of a country</label><br>
    <select id="country" name = "searchWord" value="">
        <option value="United States">United States</option>
        <option value="China">China</option>
        <option value="Japan">Japan</option>
        <option value="Germany">Germany</option>
        <option value="India">India</option>
        <option value="United Kingdom">United Kingdom</option>
        <option value="France">France</option>
        <option value="Brazil">Brazil</option>
        <option value="Italy">Italy</option>
        <option value="Canada">Canada</option>
        <option value="Russia">Russia</option>
        <option value="South Korea">South Korea</option>
        <option value="Australia">Australia</option>
        <option value="Spain">Spain</option>
        <option value="Mexico">Mexico</option>
        <option value="Indonesia">Indonesia</option>
        <option value="Turkey">Turkey</option>
        <option value="Netherlands">Netherlands</option>
        <option value="Saudi Arabia">Saudi Arabia</option>
        <option value="Switzerland">Switzerland</option>
    </select>
    <br/><br/>
    <input type="submit" name = "act" value="Submit" />
</form>
</body>
</html>