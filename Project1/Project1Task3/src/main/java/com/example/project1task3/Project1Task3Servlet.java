/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package com.example.project1task3;

import java.io.*;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * class Project1Task3Servlet
 * The class if used to communicate between the two jsp files and the Model class
 * It takes two urlPatterns as the parameter
 * Based on the information and page it sends the data to the view
 */
@WebServlet(name = "DSClassClicker", urlPatterns = {"/DSClassClicker", "/getResults"})
public class Project1Task3Servlet extends HttpServlet {

    //Initialization of the class model to use throughout the session
    Project1Task3Model project1Task3Model;

    /**
     * function init
     * Init function to initialize the model constructor to use in the file
     */
    public void init() {
        project1Task3Model = new Project1Task3Model();;
    }

    /**
     * function doGet
     * The function is used to get the data from the view and send the data back to the view
     * The function also fits the data based on the size of the device
     * If the user is on the initial page, the data is displayed accordingly and vise versa for the result page
     * @param request HTTPServletRequest
     * @param response HTTPServletResponse
     * @throws IOException for parsing the data to the view
     * @throws ServletException to check the connection to the view
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        if(request.getParameter("ds_input") != null) {
            if (request.getServletPath().equals("/DSClassClicker")) {
                String ans = request.getParameter("ds_input");
                project1Task3Model.computeClicks(ans);
                request.setAttribute("ans", request.getParameter("ds_input"));
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
        }
        else
        {
            if (request.getServletPath().equals("/DSClassClicker")) {
                RequestDispatcher view = request.getRequestDispatcher("index.jsp");
                view.forward(request, response);
            }
            else {
                request.setAttribute("clicks", project1Task3Model.getMap());
                RequestDispatcher view = request.getRequestDispatcher("result.jsp");
                view.forward(request, response);
        }
        }
    }
}