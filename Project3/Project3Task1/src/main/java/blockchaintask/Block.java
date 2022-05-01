package blockchaintask;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * class Block:
 * The cass block is used to create a single block in the blockchain
 * It is also used to compute the proof of work and calculate the hash of a particular block
 * The block created stores the hash of the previous block
 */
public class Block {
    // Private variable of the block that are required to identify a block.
    // Like its index, the timestamp is which it was created
    // The nonce of the block, the transaction data and the previous hash
    private int index, difficulty;
    private BigInteger nonce;
    private Timestamp timestamp;
    private String data, previousHash;

    /**
     * Block Constructor
     * The constructor is used to create a block and initialize it with the values provided from the chain
     * @param index the number of the block in the blockchain
     * @param timestamp the timestamp at which the block was created
     * @param data the transaction data at the block
     * @param difficulty the difficulty to calculate the hash of the block
     */
    Block(int index, Timestamp timestamp, String data, int difficulty)
    {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * function setIndex
     * Sets the index of the block
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * function setDifficulty
     * Sets the difficulty of the block
     * @param difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * function setTimestamp
     * Sets the timestamp of the block
     * @param timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * function setData
     * Sets the data/transaction of the block
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * function setPreviousHash
     * set the hash of the block's parent
     * @param previousHash
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * function getIndex
     * gets the index of the block
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * function getDifficuty
     * gets the difficulty of the block
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * function getTimestamp
     * gets the timestamp of the block
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * function getData
     * gets the transaction of the block
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * function getPreviousHash
     * gets the parent's hash of the block
     * @return previous hash
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * function getNonce
     * gets the nonce of the block
     * @return nonce
     */
    public BigInteger getNonce()
    {
        return nonce;
    }

    /**
     * function calculate hash
     * calculates the hash of of a string
     * @return hashed String
     * @throws NoSuchAlgorithmException for the SHA calculation
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        //Our string contains the index, timestamp, data, previous hash, nonce and difficulty
        String hash = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        MessageDigest digest;
        //Getting the instance of SHA-256
        digest = MessageDigest.getInstance("SHA-256");
        //encoding with SHA-256
        byte[] encodedhash = digest.digest(
                hash.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    /**
     * function byteToHex
     * The function is used to convert byte to hex of the encoded string
     * @param hash takes the hash
     * @return
     */
    private static String bytesToHex(byte[] hash) {
        //The hexString is double the length of the hash
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        //Convert each value of string to a hex
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            //append the values together
            hexString.append(hex);
        }
        //return the string
        return hexString.toString().toUpperCase();
    }

    /**
     * function proofOfWork
     * The function does the proof of work of a block
     * It calculates the hash based on the difficulty and increases the  nonce till
     * the initial 0s are not equal to the difficulty
     * @return the hash value after the proof of work
     * @throws NoSuchAlgorithmException
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        //The nonce is initialized to 0
        this.nonce = BigInteger.ZERO;
        //calculate hash with 0 nonce
        String h = calculateHash();
        //string builder of number of 0s equal to the difficulty number
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < difficulty; i++)
        {
            sb.append("0");
        }
        //if the number of 0s in the stringbuilder match the start of the hash then we stop calculating, else we continue calculating
        while(!(h.substring(0,difficulty).equals(sb.toString()))) {
            this.nonce = nonce.add(BigInteger.ONE); //adding one to nonce everytime the string does not match
            h = calculateHash(); //calculating hash again
        }
        //returning the hash
        return h;
    }

    /**
     * function toString
     * The function is used to display the data of the blocks
     * @return String value
     */
    @Override
    public String toString() {
        return "{" +
                "\"index\" : \"" + index + "\"" +
                ",\"time stamp\" : \"" + timestamp + "\"" +
                ", \"Tx\" : \"" + data + "\"" +
                ", \"PrevHash\" : \"" + previousHash + "\"" +
                ", \"nonce\" : \"" + nonce + "\"" +
                ", \"difficulty\" : \"" + difficulty + "\"" +
                "}";
    }
}
