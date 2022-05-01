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
 * class AddingClientUDP
 * UDP Client for sending requests to the server
 */
public class RemoteVariableClientUDP {
    //Assigning null to datagram socket
    static DatagramSocket aSocket = null;

    /**
     * Connecting and parsing requests to the server
     * @param args
     */
    public static void main(String args[]) {
        try {
            //client is running
            System.out.println("The client is running.");
            //function to address concern of separation
            menu();
        } catch (SocketException e) //Socket Exception if connection issues
        {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) //input output exception for the user
        {
            System.out.println("IO: " + e.getMessage());
        } finally //after the request is done, if socket if not null then close it
        {
            if (aSocket != null) aSocket.close();
        }
    }

    /**
     * The menu function to provide the choice to the user
     * send those choices with the values to the server along with the client id
     * @throws IOException
     */
    public static void menu() throws IOException {
        int choice, sum_value, id; //choice, the value sent by user and the id of the user
        Scanner input = new Scanner(System.in);
        do {
            // Initializing DatagramSocket
            aSocket = new DatagramSocket();
            //Getting the address of the server
            InetAddress aHost = InetAddress.getByName("localhost");
            int serverPort = 6789;
            //Menu choices
            System.out.println("1. Add a value to your sum");
            System.out.println("2. Subtract a value from your sum");
            System.out.println("3. Get your sum");
            System.out.println("4. Exit client");
            choice = input.nextInt();
            //An array to store choice, operand and id sent to the user
            int[] message = new int[3];
            message[0] = choice;
            switch (choice) {
                case 1: //If the user wants to add
                    System.out.println("Enter value to add:");
                    sum_value = input.nextInt();
                    message[1] = sum_value; //value of addition
                    System.out.println("Enter your ID (between 1000-1999):");
                    id = input.nextInt(); //id of the user
                    message[2] = id;
                    send_operation(id, aHost, serverPort, message);
                    break;
                case 2: //If the user wants to subtract
                    System.out.println("Enter value to subtract:");
                    sum_value = input.nextInt();
                    message[1] = sum_value;
                    System.out.println("Enter your ID:");
                    id = input.nextInt();
                    message[2] = id;
                    send_operation(id, aHost, serverPort, message);
                    break;
                case 3: //If the user wants to get the sum
                    message[1] = 0;
                    System.out.println("Enter your ID:");
                    id = input.nextInt();
                    message[2] = id;
                    send_operation(id, aHost, serverPort, message);
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
     * @param id - client id
     * @param aHost - server host
     * @param serverPort - server port
     * @param message - the message array that needs to be sent
     * @throws IOException
     */
    private static void send_operation(int id, InetAddress aHost, int serverPort, int[] message) throws IOException {
        byte[] m;
        if (id < 1000 || id > 1999) {
            System.out.println("Wrong id. Please Enter data again.");
        } else {
            //function to conver int array to byte array
            m = convertIntArrayToByteArray(message);
            //DatagramPacket to the server
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            aSocket.send(request);
            //Size of the byte array
            byte[] buffer = new byte[1000];
            //Replying back to the server
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            //Receive reply from the server
            aSocket.receive(reply);
            byte[] reply_bytes = Arrays.copyOf(reply.getData(), reply.getLength());
            //Converting byte array result to int
            int result = ByteBuffer.wrap(reply_bytes).getInt();
            System.out.println("The result is " + result + "\n");
        }
    }

    /**
     * function convertIntArraytoByteArray()
     * Function to convert the array of integers to array of bytes
     * @param data the int array
     * @return the byte array
     */
    private static byte[] convertIntArrayToByteArray(int[] data) {
        if (data == null) return null;
        // ----------
        byte[] bytes = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++)
            System.arraycopy(ByteBuffer.allocate(4).putInt(data[i]).array(), 0, bytes, i * 4, 4);
        return bytes;
    }
}
