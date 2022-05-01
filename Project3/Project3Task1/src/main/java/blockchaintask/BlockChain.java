package blockchaintask;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * class ServerBlockChain
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
     * Constructor ServerBlockChain()
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

}
