/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

/**
 * class RemoteVariableClientTCP
 * TCP Client for sending requests to the server
 */
public class RemoteVariableClientTCP {

    /**
     * Connecting and parsing requests to the server
     * @param args
     */
    public static void main(String args[]) {
        // arguments supply hostname
        Socket clientSocket = null;
        try {
            int serverPort = 7777;
            //connection with socket
            clientSocket = new Socket("localhost", serverPort);
            //Input from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //output from the server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            //function to address concern of separation
            menu(out, in);
        } catch (IOException e) { //input output exception for the user
            System.out.println("IO Exception:" + e.getMessage());
        } finally { //after the request is done, if socket if not null then close it
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
     * The menu function to provide the choice to the user
     * send those choices with the values to the server along with the client id
     * @throws IOException
     */
    public static void menu(PrintWriter out, BufferedReader in) throws IOException {
        int choice, sum_value, id;
        //input scanner for user
        Scanner input = new Scanner(System.in);
        do {
            //Menu choices
            System.out.println("1. Add a value to your sum");
            System.out.println("2. Subtract a value from your sum");
            System.out.println("3. Get your sum");
            System.out.println("4. Exit client");
            choice = input.nextInt();
            int[] message = new int[3];
            //An array to store choice, operand and id sent to the user
            message[0] = choice;
            switch (choice) {
                case 1: //If the user wants to add
                    System.out.println("Enter value to add:");
                    sum_value = input.nextInt();
                    message[1] = sum_value; //value of addition
                    System.out.println("Enter your ID (between 1000-1999):");
                    id = input.nextInt();
                    message[2] = id;  //id of the user
                    //operation to send the data to the server
                    send_operation(out, in, id, message);
                    break;
                case 2: //If the user wants to subtract
                    System.out.println("Enter value to subtract:");
                    sum_value = input.nextInt();
                    message[1] = sum_value;
                    System.out.println("Enter your ID:");
                    id = input.nextInt();
                    message[2] = id;
                    send_operation(out, in, id, message);
                    break;
                case 3: //If the user wants to get the sum
                    message[1] = 0;
                    System.out.println("Enter your ID:");
                    id = input.nextInt();
                    message[2] = id;
                    send_operation(out, in, id, message);
                    break;
                case 4: //When client quits
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    break;
                default: System.out.println("Wrong Choice.Try again");
                    break;
            }
        }while(choice < 4);

    }

    /**
     * function send_operation()
     * To send all the data required to compute to the server
     * @param out the out scanner for server
     * @param in the input scanner for server
     * @param id id of the client
     * @param message message that needs to be sent to the server
     * @throws IOException
     */
    private static void send_operation(PrintWriter out, BufferedReader in, int id, int[] message) throws IOException {
        if (id < 1000 || id > 1999) {
            System.out.println("Wrong id. Please Enter data again.");
        } else {
            //Sending message to the server
            out.println(Arrays.toString(message));
            out.flush();
            //Receive reply from the server
            String result = in.readLine();
            System.out.println("The result is " + result + "\n");
        }
    }
}
