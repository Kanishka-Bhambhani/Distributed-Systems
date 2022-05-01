/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package ds.project1task2;

import java.io.*;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * class GDPCalculator
 * The class is used to fetch the data related to population, olympic medals and flags from the model
 * and parse it to the view
 * The data is sent based on the country selected by the user
 * And the user is redirected to first (index) page from here if it clicks continue
 */
@WebServlet(name = "GDPCalculator", value = "/GDPCalculator")
public class GDPCalculatorServlet extends HttpServlet {

    /**
     * function doGet
     * The function is used to get the data entered by the user - name of country in this case
     * And then parse the data to the model to web scrape based on the country and send the required data back.
     * The data is then set for the view and the view is called
     * @param request HTTP request
     * @param response HTTP respose back to the view
     * @throws IOException to handle the data forwarded to the view
     * @throws ServletException to handle the connection to the view
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String act = request.getParameter("act");
        if(act.equals("Submit")) {
            String inputCountry = request.getParameter("searchWord");
            GDPCalculatorModel gdpCalculatorModel = new GDPCalculatorModel();

            ArrayList<String> resultList = gdpCalculatorModel.doSearch(inputCountry);

            request.setAttribute("countryString", request.getParameter("searchWord"));
            request.setAttribute("population", resultList.get(0));
            request.setAttribute("gdp", resultList.get(1));
            request.setAttribute("gold", resultList.get(2));
            request.setAttribute("silver", resultList.get(3));
            request.setAttribute("bronze", resultList.get(4));
            request.setAttribute("weighted_sum", resultList.get(5));
            request.setAttribute("expected_count", resultList.get(6));
            request.setAttribute("flag_url", resultList.get(7));
            String nextView = "result.jsp";
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);
        }
        else
        {
            String nextView = "index.jsp";
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);
        }
    }
}