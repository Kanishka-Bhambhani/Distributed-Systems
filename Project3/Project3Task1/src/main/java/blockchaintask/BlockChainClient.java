package blockchaintask;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * class BlockChainClient
 * The class is to form a TCP connection with the server where the blockchain resides
 */
public class BlockChainClient {
    /**
     * function main
     * It calls the menu function that has all the options for the user to choose from
     * @param args
     */
    public static void main(String args[]) {
        menu();
    }

    /**
     * function menu
     * The function is used by the client to send the user data to the server based on the choices.
     * Whether to add the block, verify the chain, repair the chain, etc
     */
    public static void menu() {
        Scanner input = new Scanner(System.in);
        while (true) {
            Socket clientSocket = null;
            try {
                //server connection establishment
                int serverPort = 7777;
                //Forming connection with the server
                clientSocket = new Socket("localhost", serverPort);
                Scanner in = new Scanner(clientSocket.getInputStream());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                //Menu choice for the user
                System.out.println("\n0. View basic blockchain status");
                System.out.println("1. Add a transaction to blockchain.");
                System.out.println("2. Verify the blockchain.");
                System.out.println("3. View the blockchain.");
                System.out.println("4. Corrupt the chain.");
                System.out.println("5. Hide the corruption by repairing the chain");
                System.out.println("6. Exit");

                //The user's choice
                int choice = Integer.parseInt(input.nextLine());
                JSONObject json; //Json object to send the data
                JSONObject jsonResponse; //getting response from the server
                Map<String, String> map; //Map the data
                switch (choice) {
                    case 0:
                        //If the choice is 0 the client just sends the choice in Json Object to the server.
                        //Used a map in order form the key value pair in the JSON Object
                        map = new HashMap<>();
                        map.put("choice", String.valueOf(choice));
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //Getting the json response from the server
                        jsonResponse = new JSONObject(in.nextLine());
                        //The chainSize, difficulty, total difficulty, hashes per second, total expected hashes, nonce and the chain hash
                        System.out.println("Current size of the chain: "+ jsonResponse.getString("chainSize"));
                        System.out.println("Difficulty of the most recent block: "+ jsonResponse.getString("difficulty"));
                        System.out.println("Total difficulty for all blocks: "+ jsonResponse.getString("totalDifficulty"));
                        System.out.println("Approximate hashes per second on this machine: "+jsonResponse.getString("hashesPerSecond"));
                        System.out.println("Expected total hashes required for the whole chain: "+ jsonResponse.getString("totalExpectedHashes"));
                        System.out.println("Nonce for the most recent block: "+ jsonResponse.getString("nonce"));
                        System.out.println("Chain hash: "+ jsonResponse.getString("chainHash"));
                        break;
                    case 1: //To add a block, sending the difficulty and the transaction data to the server in a map and then sending the json object
                        System.out.println("Enter difficulty > 0");
                        int difficulty = Integer.parseInt(input.nextLine());
                        System.out.println("Enter transaction");
                        String data = input.nextLine();
                        map = new HashMap<>();
                        map.put("choice", String.valueOf(choice));
                        map.put("difficulty", String.valueOf(difficulty));
                        map.put("data", data);
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //Receiving the time taken by the server to add the block
                        jsonResponse = new JSONObject(in.nextLine());
                        System.out.println("Total execution time to add this block was " + jsonResponse.getString("time") + " milliseconds");
                        break;
                    case 2: //In order to verify the entire chain, sending the choice to server
                        System.out.println("Verifying the entire chain");
                        map = new HashMap<>();
                        map.put("choice", String.valueOf(choice));
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //getting the chain is valid or not response along with the time taken to verify the chain
                        jsonResponse = new JSONObject(in.nextLine());
                        //If the chain is invalid then the error message should be displayed as well, about the node that is corrupt
                        System.out.println("Chain verification: " + jsonResponse.getString("isValid"));
                        if(jsonResponse.getString("isValid").equals("false"))
                        {
                            System.out.println(jsonResponse.getString("errorMessage"));
                        }
                        System.out.println("Total execution time to verify the chain was " + jsonResponse.getString("time") + " milliseconds");
                        break;
                    case 3: //Sending the request to send the entire blockchain to the server
                        map = new HashMap<>();
                        map.put("choice", String.valueOf(choice));
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //receiving the blockchain in string format from the server
                        jsonResponse = new JSONObject(in.nextLine());
                        System.out.println(jsonResponse.getString("blockchain"));
                        break;
                        //Asking the server to corrupt the blockchain, sending the block id and data for sorruption
                    case 4: System.out.println("Corrupt the BlockChain");
                        System.out.println("Enter the block ID of block to corrupt");
                        int id = Integer.parseInt(input.nextLine());
                        System.out.println("Enter new data for block " + id);
                        String corruptData = input.nextLine();
                        //Adding the choice, block id and data to the map
                        map = new HashMap<>();
                        map.put("choice", String.valueOf(choice));
                        map.put("id", String.valueOf(id));
                        map.put("data", corruptData);
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //Getting json response of the data being corrupted
                        jsonResponse = new JSONObject(in.nextLine());
                        System.out.println("Block "+ jsonResponse.getString("id") +" now holds " + jsonResponse.get("corruptData"));
                        break;
                        //Asking the server to repair the chain if corrupted
                    case 5: System.out.println("Repairing the entire chain");
                        map = new HashMap<>();
                        //sending the choice
                        map.put("choice", String.valueOf(choice));
                        json = new JSONObject(map);
                        out.println(json);
                        out.flush();
                        //getting the response of time taken taken to repair from the server
                        jsonResponse = new JSONObject(in.nextLine());
                        System.out.println("Total execution time required to repair the chain was " + jsonResponse.getString("time") + " milliseconds");
                        break;
                    case 6:
                        //exitting the loop, closing the client
                        System.exit(0);
                        break;
                }
            }                  catch (IOException ex) { //Exception for input output
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
        }
    }
}
