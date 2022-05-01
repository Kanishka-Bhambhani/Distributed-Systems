/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * class RemoteVariableServerTCP
 * TCP Server for echoing and replying to the client
 */
public class RemoteVariableServerTCP {
    //HashMap to store the client id and the value
    static Map<Integer,Integer> store_message = new HashMap<>();
    /**
     * Connection and parsing of messages using UDP
     * @param args no arguments
     */
    public static void main(String args[]) {
        Socket clientSocket = null;
            try {
                int serverPort = 7777; // the server port we are using

                // Create a new server socket
                ServerSocket listenSocket = new ServerSocket(serverPort);

                while (true) {
                    //Checking if there is a client or not, if not then listening for a client
                        if((clientSocket == null)|| (clientSocket.getInputStream().read() == -1)) {
                            clientSocket = listenSocket.accept();
                        }

                    //"in" to read from the client socket
                    Scanner in;
                    in = new Scanner(clientSocket.getInputStream());

                    //"out" to write to the client socket
                    PrintWriter out;
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                    //converting the message recieved to string array and then to int array
                    String[] message_string = in.nextLine().replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ", "")
                            .split(",");
                    //Converting the message to int array
                    int[] message = new int[message_string.length];
                    for (int i = 0; i < message_string.length; i++) {
                        message[i] = Integer.valueOf(message_string[i]);
                    }
                    //getting the result after computing the values
                    String result = String.valueOf(compute_values(message));
                    //sending back the result
                    out.println(result);
                    //printing the result
                    System.out.println("The result of " + operationId_to_operation(message[0]) + " for id " + message[2] + " is " + result);
                    out.flush();
                }

                // Handle exceptions
            } catch (IOException e) { //Checking for input output exceptions
                System.out.println("IO Exception:" + e.getMessage());
            } finally { //If socket is not null and request is done, close the socket
                try {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    // ignore exception on close
                }
            }
    }

    /**
     * To return the value of the operation based on the choice
     * @param i the int choice for the operation
     * @return the value of the operation
     */
    private static String operationId_to_operation(int i) {
        if(i == 1)
        {
            return "addition";
        }
        else if(i==2){
            return "subtraction";
        }
        else
        {
            return "get";
        }
    }

    /**
     * function compute_values
     * The function is used to check whether an id already exists or not.
     * To add one if does not exists and add the sum or subtract
     * If exists then add or subtract to the existing values
     * @param message
     * @return result
     */
    public static int compute_values(int[] message) {
        //checking if Id exists
        if(store_message.get(message[2])!= null)
        {
            //if the choice is 1 then add
            if(message[0] == 1)
            {
                store_message.put(message[2],store_message.get(message[2]) + message[1]);
            }
            //else subtract
            else if(message[0] == 2)
            {
                store_message.put(message[2], store_message.get(message[2]) - message[1]);
            }
        }
        else //if if does not exists
        {
            //if choice is 1 add
            if(message[0] == 1)
            {
                store_message.put(message[2], message[1]);
            }
            //if choice is 2 add -ve
            else if(message[0] == 2)
            {
                store_message.put(message[2], -message[1]);
            }
            //if choice is 3 and id doesn't exist then return 0
            else
            {
                store_message.put(message[2], 0);
            }
        }
        //get the result of the id
        int result = store_message.get(message[2]);
        return result;

    }
}
