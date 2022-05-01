/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * class EchoServerUDP
 * UDP Server for echoing and replying to the client
 */
public class AddingServerUDP{
    static int sum = 0;
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
            //Creating a datagram packet to send the data
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            System.out.println("The server is running.");
            //while loop for checking the connection and printing the recie
            while(true){
                //Recieving the request from client
                aSocket.receive(request);
                byte[] response = Arrays.copyOf(request.getData(),request.getLength());
                String requestString = new String(response);
                if(!(requestString.equalsIgnoreCase("halt!")))
                {
                    //Converting byte array to int
                    int sum_value = ByteBuffer.wrap(response).getInt();
                    System.out.println( "Adding "+ sum_value + " to " + sum);
                    //add function to add the values
                    int result = add(sum_value);
                    //Converting int to byte array
                    byte[] m = ByteBuffer.allocate(4).putInt(result).array();
                    //Replying back to the client
                    DatagramPacket reply_client = new DatagramPacket(m, m.length, request.getAddress(), request.getPort());
                    //Sending the reply to the client
                    aSocket.send(reply_client);
                    //Printing the sum
                    System.out.println("Returning sum of " + result + " to client.");
                }
            }
        }catch (SocketException e)  //catch for checking the socket connection
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) //Checking for input output exceptions
        {System.out.println("IO: " + e.getMessage());
        }finally //If socket is not null and request is done, close the socket
        {if(aSocket != null) aSocket.close();}
    }

    /**
     * Adding the response by client to the existing addition of values
     * @param i response by client
     * @return the sum of the values sent by client
     * @throws IOException
     */
    public static int add(int i) throws IOException {
        sum = sum + i; //addition
        return sum;
    }
}

