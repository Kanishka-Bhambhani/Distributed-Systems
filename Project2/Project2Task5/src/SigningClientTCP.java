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
import java.util.Random;
import java.util.Scanner;

/**
 * class RemoteVariableClientTCP
 * TCP Client for sending requests to the server
 */
public class SigningClientTCP {
    /**
     * Connecting and parsing requests to the server
     * @param args
     */
    public static void main(String args[]) {
        BigInteger[] rsa_result = calculateRSA(); //Calculation for public and private key
        String public_key = rsa_result[0].toString()+rsa_result[2].toString(); //Public key - concatenation of e+n
        byte[] hashed_public_key = new byte[0];
        try {
            hashed_public_key = compute_hash(public_key); //hashing the public key
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); //error if algorithm does not exist
        }
        //getting the client id i.e least 20 significant bytes of the hash
        byte[] clientId = Arrays.copyOfRange(hashed_public_key, hashed_public_key.length-20, hashed_public_key.length);
        BigInteger client_id = new BigInteger(clientId); //converting to BigInteger
        //storing e, n and d values
        BigInteger e = rsa_result[0];
        BigInteger d = rsa_result[1];
        BigInteger n = rsa_result[2];
        //calling the menu method were data is sent to server
        menu(client_id, e, d, n);
        }

    public static void menu(BigInteger client_id, BigInteger e, BigInteger d, BigInteger n) {
        int choice = 0, sum_value = 0; //operation and operand
        String result; //result returned by server computation
        String message_encrypt = null; //message that needs to be encrypted
        Scanner input = new Scanner(System.in);
        do {
            Socket clientSocket = null;
            try {
                //server connection establishment
                int serverPort = 7777;
                clientSocket = new Socket("localhost", serverPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                //menu choices
                System.out.println("1. Add a value to your sum");
                System.out.println("2. Subtract a value from your sum");
                System.out.println("3. Get your sum");
                System.out.println("4. Exit client");
                choice = Integer.parseInt(input.nextLine());
                switch (choice) {
                    case 1: //The message to send if the choice is add
                        System.out.println("Enter value to add:");
                        sum_value = Integer.parseInt(input.nextLine());
                        message_encrypt = client_id + "" + e + "" + n +
                                "" + choice + "" + sum_value;
                        break;
                    case 2: //The message to send if the choice is subtract
                        System.out.println("Enter value to subtract:");
                        sum_value = Integer.parseInt(input.nextLine());
                        message_encrypt = client_id + "" + e + "" + n +
                                "" + choice + "" + sum_value;
                        break;
                    case 3: //The message to send if the choice is get
                        message_encrypt = client_id + "" + e + "" + n +
                                "" + choice;
                        break;
                    case 4: //quitting if user wants to exit
                        System.out.println("Client side quitting. The remote variable server is still running.");
                        System.exit(0);
                        break;
                    default: System.out.println("Wrong Choice.Try again");
                        continue;
                }
                //hashing and then encrypting the message
                BigInteger encrypted_sign = sign(message_encrypt, d, n);
                //Sending encrypted sign, client id, e, n, choice and value if choice is 1 or 2 to the server
                out.println(encrypted_sign);
                out.flush();
                out.println(client_id);
                out.flush();
                out.println(e);
                out.flush();
                out.println(n);
                out.flush();
                out.println(choice);
                out.flush();
                if(choice == 1 || choice == 2) {
                    out.println(sum_value);
                    out.flush();
                }
                //recieving the result from the server
                result = in.readLine();
                if(!(result.equalsIgnoreCase("Error in request.")))
                    System.out.println("The result is " + result + "\n");
                else
                    System.out.println("Error in Request.");

            } catch (IOException | NoSuchAlgorithmException ex) { //Exception for input output
                System.out.println("IO Exception:" + ex.getMessage());
            } finally { //If operation is done and client is not null/closed
                try {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException ex) {
                    // ignore exception on close
                }
            }

        }while(choice < 4);

    }

    /**
     * function calculateRSA()
     * Used the one given in the lab
     * To calculate public and private keys
     * @return e,d,n - to create public and private keys
     */
    public static BigInteger[]  calculateRSA()
    {
        BigInteger n; // n is the modulus for both the private and public keys
        BigInteger e; // e is the exponent of the public key
        BigInteger d; // d is the exponent of the private key
        BigInteger[] rsa = new BigInteger[3];
        Random rnd = new Random();

        //Generating two large random primes.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Select a small odd integer e that is relatively prime to phi(n).
        e = new BigInteger("65537");

        //Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);

        System.out.println("Public Key = " + e+n);  // (e,n) is the RSA public key
        System.out.println("Private Key = " + d+n);  // (d,n) is the RSA private keySystem.out.println(" n = " + n);  // Modulus for both keys
        rsa[0] = e;
        rsa[1] = d;
        rsa[2] = n;

        return rsa;
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
        digest = MessageDigest.getInstance("SHA-256");
        //encoding with SHA-256
        byte[] encodedhash = digest.digest(
                public_key_hash.getBytes(StandardCharsets.UTF_8));
        return encodedhash;
    }

    /**
     * function sign()
     * Creating a signature from the client
     * @param message_encrypt message to be encrypted
     * @param d exponent of private key
     * @param n modulus of public and private keys
     * @return the sign of the message almong with the private key
     * @throws NoSuchAlgorithmException
     */
    public static BigInteger sign(String message_encrypt, BigInteger d, BigInteger n) throws NoSuchAlgorithmException {
        byte[] signed_hash = compute_hash(message_encrypt); //Hashing the message
        byte[] positive_signed_hash = new byte[signed_hash.length + 1];
        positive_signed_hash[0] = 0;
        //Adding a byte 0 in the start to not have -ve values for RSA
        for(int i = 0, j = 1; i < signed_hash.length; i++, j++)
        {
            positive_signed_hash[j] = signed_hash[i];
        }
        BigInteger m = new BigInteger(positive_signed_hash);
        return  m.modPow(d,n); //Encrypting the message
    }
}
