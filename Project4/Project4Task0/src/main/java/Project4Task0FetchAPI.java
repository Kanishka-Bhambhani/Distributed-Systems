// Client side code making calls to an HTTP service
// The service provides GET, DELETE, and PUT
// Simple example client storing and deleting name, value pairs on the server
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

// A simple class to wrap an RPC result.

/**
 * class Result
 * The class is used to wrap the RPC result
 */
class Result {
        private int responseCode;
        private String responseText;

        //setting the response code
        public void setResponseCode(int code) { responseCode = code; }
        //getting the response text
        public String getResponseText() { return responseText; }
        //setting the response text to string
        public void setResponseText(String msg) { responseText = msg; }
        //getting the response code and text together
        public String toString() { return responseCode + ":" + responseText; }
    }

/**
 * class Project4Task0FetchAPI
 * The class is used to fetch the data from the JokesAPI and display the results to the user
 * The class prompts the user for the type of joke they want to read and returns the joke back to the user
 */
public class Project4Task0FetchAPI {

    /**
     * main function()
     * The function is used to create a Menu for the user regarding the joke categories.
     * Based on the category the api connection is formed and the joke is returned
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {

        //looping theough the type of categories till the user asks to stop
        while(true) {
            Scanner input = new Scanner(System.in);
            System.out.println("\nJoke Categories :");
            System.out.println(" 1. Any \n 2. Miscellaneous \n 3. Programming \n 4. Dark \n 5. Pun \n 6. Spooky \n 7. Christmas \n 8. Quit \n");
            System.out.println("Enter the category of the joke:");
            int choice = Integer.parseInt(input.nextLine());
            //Functionalities depending on the user's choice
            if(choice > 0 && choice < 8) {
                // read them from the server
                System.out.println(read(choice));
            }
            else if (choice == 8){
                break;
            }
            //If wrong choice
            else{
                System.out.println("Please pick from the choices given above");
            }
        }
    }

    /**
     * function read()
     * The function is used to call the doGet function
     * and it is also used to fetch the strings based on the type of the joke
     * @param choice
     * @return
     */
    public static String read(int choice) {
        Result r = doGet(choice);
        //Getting the response and converting it to JSON Object
        JSONObject result = new JSONObject(r.getResponseText());
        //Displaying the joke based on the type of the joke
        if(result.getString("type").equals("single"))
        {
            return result.getString("joke");
        }
        else
        {
            return result.getString("setup") + "\n" + result.getString("delivery");
        }
    }

    /**
     * function doGet()
     * The function is used to make an HTTP request
     * @param choice of the type of joke the user wants
     * @return the joke that user will read
     */
    public static Result doGet(int choice) {

        HttpURLConnection conn;
        int status = 0;
        Result result = new Result();
        try {
            // GET wants us to pass the type to the url line. The type is converted to the string based on the options below
            String type = "";
            if(choice == 1)
            {
                type = "Any";
            }
            else if(choice == 2)
            {
                type = "Misc";
            }
            else if(choice == 3)
            {
                type = "Programming";
            }
            else if(choice == 4)
            {
                type = "Dark";
            }
            else if(choice == 5)
            {
                type = "Pun";
            }
            else if(choice == 6)
            {
                type = "Spooky";
            }
            else if(choice == 7)
            {
                type = "Christmas";
            }
            URL url = new URL("https://v2.jokeapi.dev/joke/" + type + "?blacklistFlags=nsfw,racist,sexist,explicit,religious,political");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // we are sending plain text
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // set http response code
            result.setResponseCode(status);
            // set http response message - this is just a status message
            // and not the body returned by GET
            result.setResponseText(conn.getResponseMessage());
            //Setting the response text
            if (status == 200) {
                String responseBody = getResponseBody(conn);
                result.setResponseText(responseBody);
            }
            //Disconnecting from the connection
            conn.disconnect();

        }
        // handle exceptions
        catch (MalformedURLException e) {
            System.out.println("URL Exception thrown" + e);
        } catch (IOException e) {
            System.out.println("IO Exception thrown" + e);
        } catch (Exception e) {
            System.out.println("IO Exception thrown" + e);
        }
        return result;
    }

    /**
     * function getResponseBody()
     * Gather a response body from the connection
     * and to close the connection.
     */
    public static String getResponseBody(HttpURLConnection conn) {
        String responseText = "";
        try {
            String output = "";
            //read the response and convert it to a string
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                responseText += output;
            }
            //disconnect from the api
            conn.disconnect();
        } catch (IOException e) { //Exception if the response is incorrect
            System.out.println("Exception caught " + e);
        }
        //return the response text
        return responseText;
    }
}
