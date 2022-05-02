<%@ page import="java.util.*" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%--
  Created by IntelliJ IDEA.
  Name: Kanishka Bhambhani
  Andrew Id: kbhambha
  This dashboard is used to display the analysis data to the user
  We add the data to the table and display it to the user
  It also takes into account the logs of the user interaction with the api
  That is displayed in another table along with the headers of the request
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Jokes API Dashboard</title>
</head>
<body>
    <h2>Jokes API Dashboard</h2>
    <h3>Data Analysis</h3>
    <table border = 1>
    <tr>
        <th>Categories Count</th>
        <th>Number of different types of joke</th>
        <th>Number of jokes</th>
        <th>Number of flags avoided</th>
        <th>User Agents</th>
        <th>Minimum Content Length</th>
        <th>Maximum Content Length</th>
    </tr>
    <tr>
    <td>
        <% HashMap<String,Integer> categoryMap = (HashMap<String, Integer>) request.getAttribute("category");
          for(Map.Entry<String, Integer> entry: categoryMap.entrySet()) { %>
                <p> <%= entry.getKey()%> : <%= entry.getValue()%> </p>
        <% } %>
    </td>


    <td>
        Single : <%= request.getAttribute("single")%> <br> <br>
        Two Part : <%= request.getAttribute("twoPart")%>
    </td>


    <td> <%= request.getAttribute("numOfJokes")%></td>


    <td>
        <% HashMap<String,Integer> flagMap = (HashMap<String, Integer>) request.getAttribute("flagCount");
            for(Map.Entry<String, Integer> entry: flagMap.entrySet()) { %>
        <p> <%= entry.getKey()%> : <%= entry.getValue()%> </p>
        <% } %>
    </td>


    <td>
        <% Set<String> userList = (HashSet<String>) request.getAttribute("userAgents");
            for(String string: userList) { %>
        <p> <%= string%> </p>
        <% } %>
    </td>


    <td><%= request.getAttribute("minContentLength")%></td>

    <td><%= request.getAttribute("maxContentLength")%></td>
        </tr>
    </table>
    <table border = 1>
        <tr>
            <th>User Agent</th>
            <th>Connection</th>
            <th>Content Length</th>
            <th>Jokes</th>
            <th>Flags</th>
            <th>Number of Jokes</th>
            <th>Safe</th>
            <th>Category</th>
            <th>Type</th>
        </tr>
    <h3>User Interaction Logs</h3>
    <% List<JSONObject> jsonDocuments = (ArrayList<JSONObject>) request.getAttribute("jsonDocuments");
        if(jsonDocuments != null) {
        for(JSONObject string: jsonDocuments) { %>
    <tr>

        <%if (string.has("headers")) {%>
        <% ObjectMapper mapper = new ObjectMapper();
         Map<String, String> map = mapper.readValue(string.getString("headers"), Map.class); %>
        <td>
        <%= map.get("user-agent") %>
        </td>
        <td>
        <%= map.get("connection")%>
        </td>
        <td>
        <%= map.get("content-length") %>
        </td>
        <%  } else { %>
        <td>
            <%= ""%>
        </td>
        <td>
            <%= ""%>
        </td>
        <td>
            <%= ""%>
        </td>
        <%}%>
    <td>
        <%if (string.has("joke")) {%>
        <%= string.getString("joke")%>
        <%  } if (string.has("setup")) { %>
        <%= string.getString("setup") + string.getString("delivery")%>
        <% } if(string.has("jokes")) {
            for(int i = 0; i < string.getJSONArray("jokes").length(); i++)
            {
            if (string.getJSONArray("jokes").getJSONObject(i).has("joke")) {%>
            <%= string.getJSONArray("jokes").getJSONObject(i).getString("joke") + "\n\n"%>  <br> <br>
            <%  } if (string.getJSONArray("jokes").getJSONObject(i).has("setup")) { %>
            <%= string.getJSONArray("jokes").getJSONObject(i).getString("setup") + "\n" + string.getJSONArray("jokes").getJSONObject(i).getString("delivery") + "\n\n" %>  <br> <br>
            <% } } }%>
    </td>
            <%Map<String, Boolean> map = new HashMap<>();
            if (string.has("joke") || string.has("setup")) {%>
            <% ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(string.get("flags").toString(), Map.class); %>
            <% } if(string.has("jokes")) { %>
            <% ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(string.getJSONArray("jokes").getJSONObject(0).get("flags").toString(), Map.class); %>
            <%  } %>
        <td>
            <%
                for (Map.Entry<String, Boolean> entry: map.entrySet())
                { %>
            <%= entry.getKey() + ":" + entry.getValue() %>  <br> <br>
            <% } %>
        </td>
        <td>
            <%if (string.has("amount")) {%>
            <%= string.getInt("amount")%>
            <%  } else { %>
            <%=1%>
            <%}%>
        </td>
        <td>
            <%if (string.has("joke") || string.has("setup")) {%>
            <%= string.getBoolean("safe")%>
            <% } if(string.has("jokes")) {
                for(int j = 0; j < string.getJSONArray("jokes").length(); j++)
                { %>
            <%= string.getJSONArray("jokes").getJSONObject(j).getBoolean("safe") + "\n"%> <br><br>
            <%  } } %>
        </td>
        <td>
            <%if (string.has("joke") || string.has("setup")) {%>
            <%= string.getString("category")%>
            <% } if(string.has("jokes")) {
                for(int j = 0; j < string.getJSONArray("jokes").length(); j++)
                {%>
            <%= string.getJSONArray("jokes").getJSONObject(j).getString("category")%> <br><br>
            <%  } }%>
        </td>
        <td>
            <%if (string.has("joke") || string.has("setup")) {%>
            <%= string.getString("type")%>
            <% } if(string.has("jokes")) {
                for(int j = 0; j < string.getJSONArray("jokes").length(); j++)
                { %>
            <%= string.getJSONArray("jokes").getJSONObject(j).getString("type") + "\n"%> <br><br>
            <%  }  }%>
        </td>
    <% } }%>
    </tr>
    </table>
</body>
</html>
