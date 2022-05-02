package cmu.edu.kbhambha.dashboard.loggingdashboard;
/**
 * Name: Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * class DashboardServlet
 * The class is used to display the dashboard with analysis and logging to the user
 */
@WebServlet(name = "dashboard", value = "/dashboard")
public class DashboardServletTask2 extends HttpServlet {
    /**
     * function doGet
     * The get function gets the data required from the JokeProcessor
     * And sets the attributes to the jsp file
     * @param request the request sent to the processor
     * @param response the response from the processor
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        //display document gives us all the data cleaned
        List<Object> parameterList = JokeProcessorTask2.displayDocument();
        //if the list is not null
        if(parameterList != null)
        {
            //setting the category to map and then displaying the count on the ui
            Map<String,Integer> category = (Map<String, Integer>) parameterList.get(0);
            request.setAttribute("category", category);
            //type of joke
            request.setAttribute("single", parameterList.get(1));
            request.setAttribute("twoPart", parameterList.get(2));
            //number of jokes
            request.setAttribute("numOfJokes", parameterList.get(3));
            Map<String,Integer> flagCount = (Map<String, Integer>) parameterList.get(4);
            //count of the flags
            request.setAttribute("flagCount",flagCount);
            Set<String> userAgents = (HashSet<String>) parameterList.get(5);
            //the unique user agents
            request.setAttribute("userAgents",userAgents);
            //minimum and maximum content-length
            request.setAttribute("minContentLength", parameterList.get(6));
            request.setAttribute("maxContentLength", parameterList.get(7));
            //all the json documents
            List<JSONObject> jsonDocuments = (ArrayList<JSONObject>) parameterList.get(8);
            request.setAttribute("jsonDocuments", jsonDocuments);
            //setting the view
            String nextView = "dashboard.jsp";
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);
        }
    }

    public void destroy() {
    }
}
