package cmu.edu.kbhambha.dashboard.loggingdashboard;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * class JokesterServlet
 * The Servlet is used to make a post request and recieve the data in the body from the 3rd party api
 * The cleaned response is sent to the android application
 * The get request is a dummy request ion the url to help guide the user
 */
@WebServlet(name = "jokes", value = "/jokes")
public class JokesterServletTask2 extends HttpServlet {
    /**
     * function doPost
     * The function is used to get and process the jokes
     * @param request request from the user
     * @param response to the user
     * @throws IOException if the input or output is wrong
     * @throws ServletException if there is an issue in the connection
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //setting the content type of the response
        response.setContentType("application/json");
        //getting the request in json object
        JSONObject jsonRequest = new JSONObject(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        //getting the raw response from the getJokes function in processor
        Map<String, String> map = new HashMap<>();
        //getting the headers of the request to add them to the database
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        JSONObject jsonHeaders = new JSONObject(map);
        //cleaning the response and then sending it to the user using out.
        JSONObject jsonResponse = JokeProcessorTask2.getJokes(jsonRequest, jsonHeaders);
        JSONArray jokes = JokeProcessorTask2.processJokes(jsonResponse);
        PrintWriter out = response.getWriter();
        out.print(jokes);
        out.flush();
    }

    /**
     * function doGet
     * The doGet function is used as a dummy interface to avoid a 405 error
     * @param request none
     * @param response none
     * @throws IOException if the input or output is wrong
     * @throws ServletException if there is an issue in the connection
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        RequestDispatcher view = request.getRequestDispatcher("jokes.jsp");
        view.forward(request, response);

    }

    public void destroy() {
    }
}