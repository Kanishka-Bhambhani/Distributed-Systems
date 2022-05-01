/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * class EchoServerUDP
 * UDP Server for echoing and replying to the client
 */
public class RemoteVariableServerUDP {
    //HashMap to store the client id and the value
    static Map<Integer,Integer> store_message = new HashMap<>();
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
            while(true){
                //Recieving the request from client
                aSocket.receive(request);
                byte[] response = Arrays.copyOf(request.getData(),request.getLength());
                //converting byte array to int array
                int[] message = convertByteArrayToIntArray(response);
                //function to add, subtract or get the values
                int result = compute_values(message);
                System.out.println("The client " + message[2] + " requested " + operationId_to_operation(message[0]) + " and the result is: " + result);
                //converting result int to byte array
                byte[] m = ByteBuffer.allocate(4).putInt(result).array();
                //Replying back to the client with datagram packet
                DatagramPacket reply_client = new DatagramPacket(m, m.length, request.getAddress(), request.getPort());
                aSocket.send(reply_client);

            }
        }catch (SocketException e) //catch for checking the socket connection
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) //Checking for input output exceptions
        {System.out.println("IO: " + e.getMessage());
        }finally //If socket is not null and request is done, close the socket
        {if(aSocket != null) aSocket.close();}
    }

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
     * function convertByteArraytoIntArray()
     * Function to convert the array of integers to array of bytes
     * @param data the int array
     * @return the byte array
     */
    public static int[] convertByteArrayToIntArray(byte[] data) {
        if (data == null || data.length % 4 != 0) return null;
        // ----------
        int[] ints = new int[data.length / 4];
        for (int i = 0; i < ints.length; i++)
            ints[i] = ( convertByteArrayToInt(new byte[] {
                    data[(i*4)], //data and the conversion to 4 bits
                    data[(i*4)+1],
                    data[(i*4)+2],
                    data[(i*4)+3],
            } ));
        return ints;
    }

    /**
     * function convertByteArraytoInt
     * converting a sine 4 bit byte array to int
     * @param intBytes byte array
     * @return
     */
    private static int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    /**
     * function compute_values
     * The function is used to check whether an id already exists or not.
     * To add one if does not exists and add the sum or subtract
     * If exists then add or subtract to the existing values
     * @param message
     * @return
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
        //return result
        return result;

    }
}

