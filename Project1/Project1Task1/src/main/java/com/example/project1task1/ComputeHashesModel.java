/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */

package com.example.project1task1;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * class ComputeHashesModel
 * The class is used to compute the data required to be sent to the view
 * The encryptions of the string are computed in this class and sent based on the radio selection of the user
 */
public class ComputeHashesModel {

    /**
     * function compute_hash
     * The function is used to compute the hash of the given string
     * The string is then returned in two formats Base64 and Hex format to the controller/servlet
     * @param originalString The string entered by the user in the view
     * @param radio The radio button selected by the user
     * @return returning the list of string values for base64 and hex strings
     */
    public List<String> compute_hash(String originalString, String radio) {
        MessageDigest digest = null;
        List<String> hashValues = new ArrayList<>();
        if (radio != null) {
            if (radio.equals("SHA256")) {
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    digest = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No radio button selected");
        }
        // Hello
        byte[] encodedhash = digest.digest(
                originalString.getBytes(StandardCharsets.UTF_8));
        String base64Binary = DatatypeConverter.printBase64Binary(encodedhash);
        String hexBinary = DatatypeConverter.printHexBinary(encodedhash);

        hashValues.add(base64Binary);
        hashValues.add(hexBinary);

        return hashValues;
    }
}
