/**
 * Andrew id: kbhambha
 * Author : Kanishka Bhambhani
 */

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * class VerifyingServerTCP
 * TCP Server for echoing and replying to the client
 */

public class VerifyingServerTCP {
    //HashMap to store the client id and the value
    public static Map<BigInteger,Integer> store_message = new HashMap<>();
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
                    try {
                        clientSocket = listenSocket.accept();
                        //"in" to read from the client socket
                        Scanner in = new Scanner(clientSocket.getInputStream());
                        //"out" to write to the client socket
                        PrintWriter out;
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                        //verifying the message and returning the computation result
                        verify(in, out);
                    }
                    //In order for the client to continue listening
                    catch(Exception e)
                    {
                        main(null);
                    }
                }

                // Handle exceptions
            } catch (IOException e) { //Checking for input output exceptions
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
     * function compute_result()
     * The function is used to verify whether the public key's least 20 bits are equal to the client Id
     * The encrupted sign is also decrypted and the message is hashed to verify if the message is by the same person or not.
     * @param in client's input
     * @param out ouput to client
     * @throws NoSuchAlgorithmException
     */
    public static void verify(Scanner in,PrintWriter out) throws NoSuchAlgorithmException {
        int sum_value = 0;
        //Getting the message and encrypted sign from the client and storing them in BigInteger
        String encryption_string = in.nextLine();
        BigInteger encrypted_sign = new BigInteger(encryption_string);
        BigInteger clientId = new BigInteger(in.nextLine());
        String e_string = in.nextLine();
        String n_string = in.nextLine();
        int choice = Integer.parseInt(in.nextLine());
        BigInteger e = new BigInteger(e_string);
        BigInteger n = new BigInteger(n_string);
        //Concatenating e and n for Public key
        String public_key = e_string + n_string;
        //Hashing the public key
        byte[] hashed_public_key = compute_hash(public_key);
        //Substringing the least 20 bits of the public key
        byte[] computed_clientId = Arrays.copyOfRange(hashed_public_key, hashed_public_key.length - 20, hashed_public_key.length);
        //Storing it in BigInteger
        BigInteger client_id = new BigInteger(computed_clientId);
        String message_encrypt;
        //The message will have the value to be added to the operation based on the choice
        if (choice == 1 || choice == 2) {
            sum_value = Integer.parseInt(in.nextLine()); //If + or -
            message_encrypt = client_id + "" + e + "" + n +
                    "" + choice + "" + sum_value;
        } else {
            message_encrypt = client_id + "" + e + "" + n + //If get
                    "" + choice;
        }
        //Decrypting the sign
        BigInteger decrypted_sign = (encrypted_sign.modPow(e, n));
        //Hashing the message derived above
        BigInteger hashed_message = hash_message(message_encrypt);
        //Comparing the sign and hashed message, client Id and computed client ID
        if ((decrypted_sign.equals(hashed_message)) && (clientId.equals(client_id))) {
            //If true, performing computation
            int result = compute_values(client_id, choice, sum_value);
            //Sending it back to client
            out.println(result);
            System.out.println("The result of "+ operationId_to_operation(choice) + " for id " + clientId + " is " + result);
        } else { //if not, sending error in request
            out.println("Error in request.");
            System.out.println("Error in request.");
        }
        //flushing the data after sent
        out.flush();
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
     * @param clientId the id of the client
     * @param choice the operation chosen by client
     * @param sum_value the operand valye
     * @return
     */
    public static int compute_values(BigInteger clientId, Integer choice, Integer sum_value) {
        //checking if Id exists
        if(store_message.get(clientId)!= null)
        {
            //if the choice is 1 then add
            if(choice == 1)
            {
                store_message.put(clientId,store_message.get(clientId) + sum_value);
            }
            //else subtract
            else if(choice == 2)
            {
                store_message.put(clientId,store_message.get(clientId) - sum_value);
            }
        }
        else //if if does not exists
        {
            //if choice is 1 add
            if(choice  == 1)
            {
                store_message.put(clientId, sum_value);
            }
            //if choice is 2 add -ve
            else if(choice == 2)
            {
                store_message.put(clientId, -sum_value);
            }
            //if choice is 3 and id doesn't exist then return 0
            else
            {
                store_message.put(clientId, sum_value);
            }
        }
        //get the result of the id
        int result = store_message.get(clientId);
        return result;

    }

    /**
     * function compute_hash()
     * It is used to hash the values using SHA-256
     * @param public_key_hash
     * @return hash using SHA-256
     * @throws NoSuchAlgorithmException
     */
    public static byte[] compute_hash(String public_key_hash) throws NoSuchAlgorithmException {
        MessageDigest digest;
        //encoding with SHA-256
        digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                public_key_hash.getBytes(StandardCharsets.UTF_8));
        return encodedhash;
    }

    /**
     * function varify()
     * Creating a signature from the client
     * @param message_encrypt message to be hashed and then added the zero byte to
     * @return the hashed message
     * @throws NoSuchAlgorithmException
     */
    public static BigInteger hash_message(String message_encrypt) throws NoSuchAlgorithmException {
        byte[] signed_hash = compute_hash(message_encrypt); //Hashing the message
        byte[] positive_signed_hash = new byte[signed_hash.length + 1];
        positive_signed_hash[0] = 0;
        //Adding a byte 0 in the start to not have -ve values for RSA
        for(int i = 0, j = 1; i < signed_hash.length; i++, j++)
        {
            positive_signed_hash[j] = signed_hash[i];
        }
        BigInteger m = new BigInteger(positive_signed_hash);
        return m;
    }
}
