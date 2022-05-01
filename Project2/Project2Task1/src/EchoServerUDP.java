/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 * class EchoServerUDP
 * UDP Server for echoing and replying to the client
 */
public class EchoServerUDP{
    /**
     * Connection and parsing of messages using UDP
     * @param args no arguments
     */
    public static void main(String args[]){
        //Assigning null to UDP socket
        DatagramSocket aSocket = null;
        //Byte array with 1000 length
        byte[] buffer = new byte[1000];
        try{
            //connection to the socket with the port 6789
            aSocket = new DatagramSocket(6789);
            //Creating a datagram packet to sent the data
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            System.out.println("The server is running.");
            //while loop for checking the connection and printing the recieved data
            while(true){
                //Recieving the request from client
                aSocket.receive(request);
                //Replying back to the client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                //Converting the response to the length of the given response instead of 1000
                byte[] response = Arrays.copyOf(request.getData(),request.getLength());
                //Converting byte array to string
                String requestString = new String(response);
                //If the server receives halt message
                if(requestString.equalsIgnoreCase("halt!"))
                {
                    //Server quits
                    System.out.println("Server side quitting");
                    break;
                }
                else {
                    //Printing the port of the client
                    System.out.println("Client listening on: " + request.getPort());
                    //Echoing the request from the client
                    System.out.println("Echoing: " + requestString);
                    //Sending the reply to the client
                    aSocket.send(reply);
                }
            }
        }catch (SocketException e) //catch for checking the socket connection
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) //Checking for input output exceptions
        {System.out.println("IO: " + e.getMessage());
        }finally //If socket is not null and request is done, close the socket
        {if(aSocket != null) aSocket.close();}
    }
}

