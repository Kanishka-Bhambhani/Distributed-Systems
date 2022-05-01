package blockchaintask;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * class BlockChain
 * The class is used to create a chain of blocks
 * To see how chain gets corrupted, validate a chain and to repair a chain
 * We also check the time a particular function takes on the chain
 */
public class BlockChain {
    ArrayList<Block> blocks;
    private String chainHash;
    int hashesPerSecond;
    private String errorMessage = "";

    /**
     * Constructor BlockChain()
     * The constructor is used to initialize the list of blocks that will be connected to one another
     * The hash of the latest block and the hashes per second when there are 1 million hashes being computed
     */
    BlockChain()
    {
        this.blocks = new ArrayList<>();
        this.chainHash = "";
        this.hashesPerSecond = 0;
    }

    /**
     * function getTime 
     * The get time function is used to get the current time in milliseconds
     * @return timestamp (time)
     */
    public Timestamp getTime()
    {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * function getLatestBlock
     * the function is used to get the latest block of the chain. Recently added
     * @return
     */
    public Block getLatestBlock()
    {
        return blocks.get(blocks.size() - 1);
    }

    /**
     * function getChainSize 
     * The function gets the size of the blockchain
     * @return
     */
    public int getChainSize()
    {
        return blocks.size();
    }

    /**
     * function computeHashesPerSecond
     * The function is used the compute 1 million hashes and check how many hashes the system can compute in one second
     * @throws NoSuchAlgorithmException
     */
    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
        Timestamp startTime = getTime();
        String hash =  "00000000";
        for(int i = 0; i < 1000000; i++) {
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-256");
            //encoding with SHA-256
            byte[] encodedhash = digest.digest(
                    hash.getBytes(StandardCharsets.UTF_8));
        }
        Timestamp endTime = getTime();
        long totalTime = endTime.getTime() - startTime.getTime();
        this.hashesPerSecond = (int) ((1000000*1000)/totalTime);
    }

    /**
     * function get hashesPerSecond
     * return the hashes per second to display to the user
     * @return hashes per second
     */
    public int getHashesPerSecond()
    {
        return hashesPerSecond;
    }

    /**
     * function addBlock
     * It adds the new block provided to the user, after performing certain checks on it
     * @param newBlock a new block to be added to the chain
     * @throws NoSuchAlgorithmException
     */
    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        //To check whether the block is valid or not before adding the block 
        StringBuilder sb = new StringBuilder();
        //String builder to get the number of 0s compared to difficulty
        for(int i = 0; i < newBlock.getDifficulty(); i++)
        {
            sb.append("0");
        }
        //Calculate the hash of the new block created
        String hash = newBlock.calculateHash();
        //If the substring till the difficulty of the hash if equal to the string builder's number of 0s
        //and the previous hash of the new block is equal to the lastest block's hash only then add the block
        //and assign the chainHash the new block's hash
        if(hash.substring(0,newBlock.getDifficulty()).equals(sb.toString()) && newBlock.getPreviousHash().equals(getLatestBlock().calculateHash())) {
            blocks.add(newBlock); //add the block
            this.chainHash = hash; //chainHash's new hash is the latest block's hash
        }
        else
        {
            //If verification fails
            System.out.println("Addition failed due to failed verification or wrong previous hash.");
        }
    }

    /**
     * function toString
     * The function is used to print the blockchain in the string
     * @return
     */
    public String toString()
    {
        //sb to append the entire blockchain in a String
        StringBuilder sb = new StringBuilder();
        sb.append("{" + "\"ds_chain{\" : [ ");
        //appending the block's to string to the block chain
        for(int i = 0; i < blocks.size(); i++)
        {
            sb.append(getBlock(i).toString()+"\n");
        }
        sb.append("],\"chainHash\":\"" + chainHash + "\"}");
        //return the string
        return sb.toString();
    }

    /**
     * function getBlock
     * To get the block at ith position
     * @param i the position of the block
     * @return the block
     */
    public Block getBlock(int i)
    {
        return blocks.get(i);
    }

    /**
     * function getTotalDifficulty
     * Get the total difficulty of the entire block chain
     * @return the total difficulty
     */
    public int getTotalDifficulty()
    {
        int totalDifficulty = 0;
        //get the difficulty of every block and add it
        for(int i = 0; i < blocks.size(); i++)
        {
            totalDifficulty += getBlock(i).getDifficulty();
        }
        return totalDifficulty;
    }

    /**
     * function getTotalExpectedHAshes
     * Get the total expected hashes of the blockchain
     * @return the expected hashes
     */
    public double getTotalExpectedHashes()
    {
        double totalExpectedHashes = 0.0;
        //Count the hashes based on the difficulty of every block and add them
        for(int i = 0; i < blocks.size(); i++)
        {
            totalExpectedHashes += Math.pow(16,getBlock(i).getDifficulty());
        }
        //return the total expected hashes
        return totalExpectedHashes;
    }

    /**
     * function isChainValid
     * Check if the chain is valid or not.
     * If it is corrupted at some point
     * @return the boolean value of true or false
     * @throws NoSuchAlgorithmException
     */
    public boolean isChainValid() throws NoSuchAlgorithmException {
        //If there is ony 1 block in the chain
        if(blocks.size() == 1)
        {
            //Get the block and create a string builder of 0s till its difficulty value
            Block block = blocks.get(0);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < block.getDifficulty(); i++)
            {
                sb.append("0");
            }
            //calculate the hash of the block
            String hash = block.calculateHash();
            //If the substring of hash matchs the sb's zeros and the chainHash is equal to it's hash then it is valid
            //Else the chain is not valid and return false with the 1st node being invalid
            if(!(hash.substring(0,block.getDifficulty()).equals(sb.toString()) && chainHash.equals(hash))) {
                errorMessage = "..Improper hash at node 1. Does not begin with " + sb;
                return false;
            }
        }
        //If the size of blockchain is more than 1
        else
        {
            String hash = "";
            //For every block in the chain
            for(int i = 1; i < blocks.size(); i++)
            {
                //Get the block and create a string builder of 0s till its difficulty value
                StringBuilder sb = new StringBuilder();
                for(int j = 0; j < getBlock(i).getDifficulty(); j++)
                {
                    sb.append("0");
                }
                //calculate the hash of the block
                hash = getBlock(i).calculateHash();
                //If the substring of hash matchs the sb's zeros and the previous hash of the block is equal to hash of the previous block
                //Else the chain is not valid and return false with the node that is invalid
                if(!(hash.substring(0,getBlock(i).getDifficulty()).equals(sb.toString()) && getBlock(i).getPreviousHash().equals(blocks.get(i-1).calculateHash()))) {
                    errorMessage = "..Improper hash at node" + i + ".Does not begin with " + sb;
                    return false;
                }
            }
            //Also check if the hash that we get at the end of the loop is valid to the chain hash. If not, return false
            if(!(hash.equals(chainHash)))
            {
                errorMessage = "..Improper value of chainHash";
                return false;
            }
        }
        return true; //return true if all cases pass
    }

    /**
     * function repairChain
     * The function is to repair the chain when it is corrupted.
     * A different message is set in place of the previous one for the block
     * @throws NoSuchAlgorithmException
     */
    public void repairChain() throws NoSuchAlgorithmException {
        //For every block
        for(int i= 0; i< blocks.size(); i++)
        {
            //Get the block and create a string builder of 0s till its difficulty value
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < getBlock(i).getDifficulty(); j++)
            {
                sb.append("0");
            }
            //calculate the hash of the block
            String hash = getBlock(i).calculateHash();
            //If the substring of hash matches the sb's zeros then the block needs no repairing,
            // if not then calculate the proof of work of the block all over again
            if(!(hash.substring(0,getBlock(i).getDifficulty()).equals(sb.toString()))) {
                getBlock(i).proofOfWork(); //Calculating the proof of work
                if(i+1 != blocks.size()) { // If it is not the last block then set the next block's previous hash as the hash just calculated
                    blocks.get(i + 1).setPreviousHash(getBlock(i).calculateHash());
                }
                else
                {
                    //If it is the last block then chainHash should be the block's hash
                    chainHash = getBlock(i).calculateHash();
                }
            }
        }
    }

    /**
     * function setChainHash
     * The function sets the chain hash of the block chain
     * @param chainHash String
     */
    public void setChainHash(String chainHash)
    {
        this.chainHash = chainHash;
    }

    /**
     * getChainHash
     * The function reti=urns the chain hash of the block chain
     * @return String chain hash
     */
    public String getChainHash()
    {
        return chainHash;
    }

    /**
     * function getErrorMessage()
     * sets the error message to the point of failure if the chain is valid or not
     * @return the error message of the chain being valid or not
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * function setErrorMessage
     * Setting the error message as back to empty once the chain verification failure point is known
     * @param errorMessage the message if the chain is valid or not
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * function main
     * The main function gives user the choices in order to manipulate the blockchain
     * A genisis block is created and the user is then given options to add a transaction
     * corrupt a chain or repair a chain
     * If the chain is valid and display the chain along with the time taken by each function
     * @param args
     */
    public static void main(String[] args) {
        //Taking the user input
        Scanner input = new Scanner(System.in);
        //Creating a blockchain object
        BlockChain blockChain = new BlockChain();
        //Creating a genesis block wth difficulty of 2
        Block block = new Block(0, blockChain.getTime(), "Genesis", 2);
        //Setting the previous hash of 1t block to 0
        block.setPreviousHash("");
        try {
            //Computing the proof of work of the 1st block
            block.proofOfWork();
            //Adding the block to the chain
            blockChain.blocks.add(block);
            //Computing a million hashes per second and displaying the number of hashes done by system in a second
            blockChain.computeHashesPerSecond();
            //Setting the value of chainHash variable to the 1st block's hash
            blockChain.setChainHash(block.calculateHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        while(true)
        {
            //Menu choices for the user to choose from
            System.out.println("\n0. View basic blockchain status");
            System.out.println("1. Add a transaction to blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain");
            System.out.println("6. Exit");

            int choice = Integer.parseInt(input.nextLine());

            switch(choice)
            {           //Displaying the data of the blockchain. The chain size, difficulty of the latest block, total difficulty
                        //the hashes per second computed above, total expected hashes by computing expected hashes of each block
                        //Nonce of the latest block and the chainHash i.e the hash of the latest block
                case 0: System.out.println("Current size of the chain: "+ blockChain.getChainSize());
                        System.out.println("Difficulty of the most recent block: "+ blockChain.getLatestBlock().getDifficulty());
                        System.out.println("Total difficulty for all blocks: "+ blockChain.getTotalDifficulty());
                        System.out.println("Approximate hashes per second on this machine: "+ blockChain.getHashesPerSecond());
                        System.out.println("Expected total hashes required for the whole chain: "+ blockChain.getTotalExpectedHashes());
                        System.out.println("Nonce for the most recent block: "+ blockChain.getLatestBlock().getNonce());
                        System.out.println("Chain hash: "+ blockChain.getChainHash());
                        break;
                        //Adding a transaction to the block, asking the user for the difficulty and the transaction data
                        //For adding a block with 2 difficulty, the computation is fast, and it takes 0 milliseconds.
                        //For adding a block with difficulty of 3 it takes about 5 milliseconds
                        //The block with difficulty of 4 takes about 173 milliseconds
                        //The block with difficulty of 5 took 232 milliseconds
                        //The block with difficulty of 6 took 68888 milliseconds, we can see an increase in computation of time
                        // And for difficulty 7 it took 758503 milliseconds, indicating that as we increase the difficulty, the computation time increases exponentially
                case 1: System.out.println("Enter difficulty > 0");
                        int difficulty = Integer.parseInt(input.nextLine());
                        System.out.println("Enter transaction");
                        String data = input.nextLine();
                        Timestamp startTime = blockChain.getTime();
                        Block block1 = new Block(blockChain.getChainSize(), blockChain.getTime(), data, difficulty);
                        block1.setPreviousHash(blockChain.getChainHash());
                        try {
                            block1.proofOfWork();
                            blockChain.addBlock(block1);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Timestamp endTime = blockChain.getTime();
                        System.out.println("Total execution time to add this block was " + (endTime.getTime() - startTime.getTime()) + " milliseconds");
                        break;
                        //If the chain is valid then the time taken by the isChainValid function to verify is 0 milliseconds
                        //The chain verification if the chain is invalid also takes 0 milliseconds, because as soon as the
                        //chain finds a failure point it returns false and let's us know the point of failure.
                        //The computation time is the same
                case 2: System.out.println("Verifying the entire chain");
                        Timestamp startTimeValid = blockChain.getTime();
                        try {
                            boolean isValid = blockChain.isChainValid();
                            if(blockChain.getErrorMessage().equals("")) {
                                System.out.println("Chain verification: " + isValid);
                            }
                            else
                            {
                                System.out.println(blockChain.getErrorMessage());
                                System.out.println("Chain verification: " + isValid);
                                blockChain.setErrorMessage("");
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Timestamp endTimeValid = blockChain.getTime();
                        System.out.println("Total execution time to verify the chain was " + (endTimeValid.getTime() - startTimeValid.getTime()) + " milliseconds");
                        break;
                        //This case is used to print the entire blockchain in the json format
                case 3: System.out.println("View the blockchain");
                        System.out.println(blockChain);
                        break;
                        //The case helps in corrupting the data by asking the user the block that it wants to corrupt and
                        //the new data for that block
                case 4: System.out.println("Corrupt the Blockchain");
                        System.out.println("Enter the block ID of block to corrupt");
                        int id = Integer.parseInt(input.nextLine());
                        System.out.println("Enter new data for block " + id);
                        String corruptData = input.nextLine();
                        blockChain.getBlock(id).setData(corruptData);
                        System.out.println("Block "+ id +" now holds " + corruptData);
                        break;
                        //Repairing the block of the chain with the difficulty of 3 when the data is corrupt takes about 248 milliseconds
                        //It takes more time to repair the chain if the difficulty is greater, for difficulty 5 it takes about 2142 milliseconds
                        //This indicates that the repair time depends on the block's difficulty and time taken to calculate the proof of work again.
                        //The time taken also depends on the number of blocks that are corrupt and need repairing
                case 5: System.out.println("Repairing the entire chain");
                        Timestamp startTimeRepair = blockChain.getTime();
                        try {
                            blockChain.repairChain();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Timestamp endTimeRepair = blockChain.getTime();
                        System.out.println("Total execution time required to repair the chain was " + (endTimeRepair.getTime() - startTimeRepair.getTime()) + " milliseconds");
                        break;
                        //Exiting the system if it is case 6
                case 6: System.exit(0);
                        break;
            }
        }
    }


}
