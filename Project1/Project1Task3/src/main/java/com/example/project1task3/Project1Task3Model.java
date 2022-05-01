/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package com.example.project1task3;

import java.util.Map;
import java.util.TreeMap;

/**
 *  class Project1Task3Model
 *  The class is used to compute the clicks of the particular option selected by the user
 *  The data is sent to the servlet to then display it to the user via a view
 */
public class Project1Task3Model {
    //static tree map to store the number of clicks
    static TreeMap<String,Integer> noOfClicks = new TreeMap<>();

    /**
     * function compute clicks
     * The function is used to compute the number of clicks performed by the user for a given option
     * @param ans returns a map of the values and their number of clicks in the session
     */
    public void computeClicks(String ans){
        if(ans.equals("A")) {
            computeValues(ans);
        }
        else if(ans.equals("B")) {
            computeValues(ans);
        }
        else if(ans.equals("C")) {
            computeValues(ans);
        }
        else {
            computeValues(ans);
        }

        for (Map.Entry<String,Integer> entry: noOfClicks.entrySet()) {
            if(entry.getValue() == 0)
            {
                noOfClicks.remove(entry);
            }
        }
    }

    /**
     * function computeValues
     * The compute values function is used to increment the count of a particular option
     * for every iteration that it is clicked.
     * @param ans takes the static map to store the increment of clicks in
     */
    private void computeValues(String ans) {
        if(noOfClicks.get(ans) != null) {
            noOfClicks.put(ans,noOfClicks.get(ans) + 1);
        }
        else
        {
            noOfClicks.put(ans,1);
        }
    }

    /**
     * function getMap
     * Getter function to return the map to the Servlet with the counts.
     * @return
     */
    public Map<String, Integer> getMap()
    {
        return noOfClicks;
    }
}
