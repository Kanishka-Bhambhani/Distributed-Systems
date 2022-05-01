/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package com.example.project1task1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import javax.xml.bind.DatatypeConverter;

/**
 * Class ComputeHashesServlet
 * The class acts as a Servlet to communicate between the model and view
 * It helps in computing hashes of the string in SHA-256 and MD5 encryption algorithms
 */
@WebServlet(name = "ComputeHashes", value = "/ComputeHashes")
public class ComputeHashesServlet extends HttpServlet {

    /**
     * function doGet
     * The function is used to Get the response from the view/user and call the function
     * from the model to fetch the data that is asked. It this case it is two types of encrypted strings
     * @param request  The request from the HTTP Server
     * @param response The response from the HTTP Server
     * @throws IOException The exception is raised if the request or response fowarded to view isn't correct
     * @throws ServletException The exception is raised if there is an issue with the connection to Servlet
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        String originalString = request.getParameter("string_value");
        String radio = request.getParameter("hash_input");
        ComputeHashesModel computeHashesModel = new ComputeHashesModel();
        List<String> hashValues = computeHashesModel.compute_hash(originalString, radio);
        request.setAttribute("base64",hashValues.get(0));
        request.setAttribute("hex",hashValues.get(1));
        request.setAttribute("og_string",originalString);

        String nextView ="index.jsp";
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}