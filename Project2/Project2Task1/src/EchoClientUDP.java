/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 * class EchoClientUDP
 * UDP Client for sending requests to the server
 */
public class EchoClientUDP{
    /**
     * Connecting and parsing requests to the server
     * @param args
     */
    public static void main(String args[]){
        // Initializing DatagramSocket
        DatagramSocket aSocket = null;
        try {
            //Getting the address of the server
            InetAddress aHost = InetAddress.getByName("localhost");
            //Server port
            int serverPort = 6789;
            //Socket initialization
            aSocket = new DatagramSocket();
            String nextLine;
            //BufferedReader to get the input from the user
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("The client is running.");
            //While loop till the user keeps sending data
            while ((nextLine = typed.readLine()) != null) {
                //converting the data in byte array
                byte [] m = nextLine.getBytes();
                //DatagramPacket to the server
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                //Sending the datagram packet to the server
                aSocket.send(request);
                //Size of the byte array
                byte[] buffer = new byte[1000];
                //Replying back to the server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                //Getting the data from the server
                String requestString = new String(request.getData());
                //If the user did not send halt
                if(!(requestString.equalsIgnoreCase("halt!")))
                {
                    //Receive reply from the server
                    aSocket.receive(reply);
                    //Byte array in the length og the data that was recieved - truncating the extra length
                    byte[] reply_bytes = Arrays.copyOf(reply.getData(), reply.getLength());
                    //Converting byte array to string
                    String replyString = new String(reply_bytes);
                    //Printing the reply for the user
                    System.out.println("Reply: " + replyString);
                }
                else {
                    //Client quits if user sends halt
                    System.out.println("Client side quitting");
                    break; //breaking out of the code
                }
            }

        }catch (SocketException e) //Socket Exception if connection issues
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) //input output exception for the user
        {System.out.println("IO: " + e.getMessage());
        }finally  //after the request is done, if socket if not null then close it
        {if(aSocket != null) aSocket.close();}
    }
}

