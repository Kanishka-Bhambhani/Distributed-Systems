/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * class AddingClientUDP
 * UDP Client for sending requests to the server
 */
public class AddingClientUDP{
    static DatagramSocket aSocket = null;
    /**
     * Connecting and parsing requests to the server
     * @param args
     */
    public static void main(String args[]){
        // args give message contents and server hostname
        try {
            String nextLine;
            //BufferedReader to get the input from the user
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("The client is running.");
            //While loop till the user keeps sending data
            while ((nextLine = typed.readLine()) != null) {
                //If the user sends halt
                if(nextLine.equalsIgnoreCase("halt!"))
                {
                    //Client quits if user sends halt
                    System.out.println("Client side quitting");
                    break; //breaking out of the code
                }
                else {
                    //Sending the integer to the server
                    int nextInt = Integer.parseInt(nextLine);
                    System.out.println("The server returned: " + add(nextInt));
                }
            }

        }catch (SocketException e) //Socket Exception if connection issues
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) //input output exception for the user
        {System.out.println("IO: " + e.getMessage());
        }finally //after the request is done, if socket if not null then close it
        {if(aSocket != null) aSocket.close();}
    }

    /**
     * function add()
     * To send the integer values to server, sent my user to add
     * and get back the reply
     * @param i int that needs to be added
     * @return result from the server
     * @throws IOException
     */
    public static int add(int i) throws IOException {
        // Initializing DatagramSocket
        aSocket = new DatagramSocket();
        //Getting the address of the server
        InetAddress aHost = InetAddress.getByName("localhost");
        //Server port
        int serverPort = 6789;
        //Converting int to byte array
        byte[] m = ByteBuffer.allocate(4).putInt(i).array();
        //DatagramPacket to the server
        DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
        //Sending the datagram packet to the server
        aSocket.send(request);
        //Size of the byte array
        byte[] buffer = new byte[1000];
        //Replying back to the server
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        //Receive reply from the server
        aSocket.receive(reply);
        //Byte array in the length og the data that was recieved - truncating the extra length
        byte[] reply_bytes = Arrays.copyOf(reply.getData(), reply.getLength());
        //Converting byte array result to int
        int result = ByteBuffer.wrap(reply_bytes).getInt();
        //returning result
        return result;
    }
}
