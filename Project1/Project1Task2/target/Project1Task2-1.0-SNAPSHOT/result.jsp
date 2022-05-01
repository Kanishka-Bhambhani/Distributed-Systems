<%--
  Created by IntelliJ IDEA.
  Name: Kanishka Bhambhani
  Andrew Id: kbhambha
  This jsp file is used to post the data required by the user.
  The population, gdp of a country
  The number of gold, silver, bronze medal recieved by the country
  The weighted sum and expected weight count of the medals
  The flag of the country and credits to the websites scraped
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Country : <%= request.getAttribute("countryString")%></title>
</head>
<body>
<form action="GDPCalculator" >
    <h1>Country : <%= request.getAttribute("countryString")%></h1>
    <h2>Population: <%= request.getAttribute("population") %>
    </h2>
    <h2>GDP: <%= request.getAttribute("gdp") %>
    </h2>
    <h4>Credit: https://www.worldometers.info/gdp/gdp-by-country
    </h4>
    <h2>Gold: <%= request.getAttribute("gold") %>
    </h2>
    <h2>Silver: <%= request.getAttribute("silver") %>
    </h2>
    <h2>Bronze: <%= request.getAttribute("bronze") %>
    </h2>
    <h2>Weighted Medal Count: <%= request.getAttribute("weighted_sum") %>
    </h2>
    <h4>Credit: https://olympics.com/tokyo-2020/olympic-games/en/results/all-sports/medal-standings.htm
    </h4>
    <h2>Expected Medal Count: <%= request.getAttribute("expected_count") %>
    </h2>
    <h4>Credit: "Towing Icebergs, Falling Dominoes" by Robert B.Banks
    </h4>
    <h4>Flag
    </h4>
    <img src="<%= request.getAttribute("flag_url")%>"><br><br>
    <h4>https://commons.wikimedia.org/wiki/Animated_GIF_flags
    </h4>
    <br/>
    <input type="submit" name="act" value="Continue" />
</form>
</body>
</html>
