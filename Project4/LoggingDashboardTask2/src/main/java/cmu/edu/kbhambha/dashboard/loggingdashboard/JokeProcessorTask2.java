/**
 * Name: Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package cmu.edu.kbhambha.dashboard.loggingdashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import okhttp3.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class JokeProcessorTask
 * The class is used to connect to the 3rd party API i.e the jokes API
 * The class is also used to clean the data from the API and then send it back to the android application
 * The server is deployed on Heroku
 * The task is also used to store the data in mongodb
 * Then retrieve the data and perform some analysis on the same and display the logs
 */
public class JokeProcessorTask2 {
    //Initializing global variables to use them for analysis and logging
    static Set<String> blacklistKeySet = new HashSet<>(Arrays.asList("nsfw", "religious", "political", "racist", "sexist", "explicit"));
    static Set<String> typeKeySet = new HashSet<>(Arrays.asList("single", "twoPart"));
    static Map<String,Integer> category = new HashMap();
    static List<String> userAgents = new ArrayList<>();
    static List<Integer> contentLength = new ArrayList<>();
    static int minContentLength = 0, maxContentLength = 0;
    static Set uniqueUserAgents = new HashSet();
    static Map<String,Integer> flags = new HashMap();
    static List<JSONObject> jsonDocuments = new ArrayList<>();

    /**
     * function getJokes
     * The function is used to get the jokes from the 3rd party API
     * The parameters received by the user from the android application is cleaned first
     * and then sent to the api
     * @param jsonObject the jsonObject received from the android application
     * @return the result of the api
     */
    public static JSONObject getJokes(JSONObject jsonObject, JSONObject jsonHeaders) {
        //parameters required for the API
        String blacklistFlags="", category = "", numberOfJokes="", type="";
        String responseText = "";
        String url = "";
        try {
            //Iterating over the json object
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String field = it.next();
                String value = String.valueOf(jsonObject.get(field));
                //Iterating for the flags to find the ones that need to avoided by the user
                if (blacklistKeySet.contains(field) && Boolean.valueOf(value)) {
                    blacklistFlags += field + ",";
                }
                //the category requested by the user
                else if (field.equals("category")) {
                    category = value;
                }
                //the number of jokes needed by the user
                else if (field.equals("amountOfJokes")) {
                    numberOfJokes = value;
                }
                //the type of joke required by the user
                else if (typeKeySet.contains(field) && Boolean.valueOf(value))
                {
                    type += field + ",";
                }
            }
            //If there is atleast one flag that is avoided
            if(!blacklistFlags.equals("")) {
                blacklistFlags = blacklistFlags.substring(0, blacklistFlags.length() - 1);
            }
            //If one of the types is selected
            if(!type.equals("")) {
                type = type.substring(0, type.length() - 1);
            }

            //URL to the 3rd party api depending on the addition of number of flags and type of joke
            url = "https://v2.jokeapi.dev/joke/"+category+"?amount="+numberOfJokes + "&";
            if (!blacklistFlags.isEmpty())
                url += "blacklistFlags=" + blacklistFlags + "&";
            if (!type.isEmpty())
                url += "type=" + type;
            //OkHttpClient
            OkHttpClient client = new OkHttpClient();
            //Get Request to the api
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            //Get the response in the body
            responseText = response.body().string();
            dbStorage(new JSONObject(responseText),jsonHeaders);
            //Return the json object of the response
            return new JSONObject(responseText);
        } catch (Exception e){ //Exception is the response is incorrect or has no value
            e.printStackTrace();
            System.out.println("Response received: " + responseText);
            return null;
        } finally { //printing the url endpoint as a check
            System.out.println("API endpoint: " + url);
        }
    }

    /**
     * function processJokes()
     * The function is used to process the data recieved from the api
     * The data is cleaned of all the extra values and only the jokes are sent back to the user/android application
     * @param jsonObject the response from the API
     * @return array of jokes
     */
    public static JSONArray processJokes(JSONObject jsonObject)
    {
        JSONArray jsonResponse = new JSONArray();
        //If response is not null
        if(jsonObject != null) {
            //If there is only one joke and of type single, add it in the array
            if (jsonObject.has("joke")) {
                jsonResponse.put(jsonObject.getString("joke"));
                //If there is only one joke and of type two part, concatenate both the parts
            } else if (jsonObject.has("setup")) {
                jsonResponse.put(jsonObject.getString("setup") + "\n" + jsonObject.getString("delivery"));
                //If there are multiple jokes requested and have data for all of them
            } else if (jsonObject.has("jokes")) {
                //iterate over data of all the jokes in the array
                for (int i = 0; i < jsonObject.getJSONArray("jokes").length(); i++) {
                    //If it is of type single then add it to the jsonArray
                    if (jsonObject.getJSONArray("jokes").getJSONObject(i).has("joke"))
                        jsonResponse.put(jsonObject.getJSONArray("jokes").getJSONObject(i).getString("joke"));
                        //If it is two part then concatenate both the parts of joke and add it to the array
                    else
                        jsonResponse.put(jsonObject.getJSONArray("jokes").getJSONObject(i).getString("setup") + "\n" + jsonObject.getJSONArray("jokes").getJSONObject(i).getString("delivery"));
                }
                //If neither single not multiple jokes are found i.e the selections made by user do not have the jokes
            } else {
                jsonResponse.put("No jokes found. Please try with other parameters.");
            }
        }
        //If the json object received by 3rd party api is null
        else
        {
            jsonResponse.put("No jokes found.  Please try with other parameters.");
        }
        //return response
        return jsonResponse;
    }

    /**
     * function dbStorage()
     * The function is used to store the json data in the db whenever the aPI is with a post call
     * @param jsonObject the api data
     * @param jsonHeaders the request headers
     */
    public static void dbStorage(JSONObject jsonObject, JSONObject jsonHeaders) {
        //The logger is used set the level of logs for this particular task
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        //The connection string that is used to connect to the db
        ConnectionString connectionString = new ConnectionString("mongodb+srv://Project4:Project4@cluster0.yzi86.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        //To build the connection
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        //If the client is connected to the db
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            //Get the database and the collection
            MongoDatabase database = mongoClient.getDatabase("APILogs");
            MongoCollection<Document> collection = database.getCollection("DashboardData");
            //Insert the string given by the user
            Document doc = Document.parse( jsonObject.toString() );
            doc.append("headers", jsonHeaders.toString());
            //appending the string to the document
            //doc.append("jsonResponse", doc);
            //Inserting the string to the collection
            collection.insertOne(doc);
        }
    }

    /**
     * function displayDocument()
     * The function is used to display the data to the user.
     * After the analytics and clearing the logs
     * @return
     */
    public static List<Object> displayDocument()
    {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        //The connection string that is used to connect to the db
        ConnectionString connectionString = new ConnectionString("mongodb+srv://Project4:Project4@cluster0.yzi86.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        //To build the connection
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            //Get the database and the collection
            MongoDatabase database = mongoClient.getDatabase("APILogs");
            MongoCollection<Document> collection = database.getCollection("DashboardData");
            //including just the string column of the document and excluding the id
            Bson projectionFields = Projections.fields(
                    Projections.excludeId());
            //Iterator for all the documents
            MongoCursor<Document> iterDoc = collection.find().projection(projectionFields).iterator();
            //if there are no documents in db
            if (iterDoc == null) {
                System.out.println("No document found");
                return null;
            } else {
                List<JSONObject> documents = new ArrayList<>();
                //If there are documents in db
                while (iterDoc.hasNext()) {
                    Document stringResult = iterDoc.next();
                    JSONObject jsonDocument = new JSONObject(stringResult);
                    jsonDocuments.add(jsonDocument);
                    documents.add(jsonDocument);
                }
                //Cleaning the data and returning it to the servlet
                return cleanData(documents);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * function cleanData
     * The function is used to clean the data recieved from the db
     * Display it to user in understandable format and with some analysis
     * @param documents the documents of the db
     * @return a list of data back to the servlet
     * @throws IOException
     */
    public static List<Object> cleanData(List<JSONObject> documents) throws IOException {
        //Number of single, two part and total jokes in the db
        int num_of_single = 0, num_of_twoPart = 0, numOfJokes = 0;
        //For all the documents
        for(JSONObject jsonObject : documents)
        {
                //If a document has only one joke of type single
                if (jsonObject.has("joke")) {
                    num_of_single += 1; //add the count of type
                    numOfJokes += 1; //add the num of jokes
                    addCategoryCount(jsonObject); //check the category
                    addFlagCount(jsonObject); //check the flag count
                //If a document has one joke of type two part
                } else if (jsonObject.has("setup")) {
                   num_of_twoPart += 1;
                   numOfJokes += 1;
                   addCategoryCount(jsonObject);
                    addFlagCount(jsonObject);
                //if a document has multiple jokes
                } else if (jsonObject.has("jokes")) {
                    //loop through all the jokes
                    for (int i = 0; i < jsonObject.getJSONArray("jokes").length(); i++) {
                        //If the joke is of single type
                        if (jsonObject.getJSONArray("jokes").getJSONObject(i).has("joke")) {
                            num_of_single += 1; //add the joke count
                            addCategoryCount(jsonObject.getJSONArray("jokes").getJSONObject(i)); //check category
                            addFlagCount(jsonObject.getJSONArray("jokes").getJSONObject(i)); //check the flags set and their count
                        }
                        //If the joke is two part
                        else
                        {
                            num_of_twoPart += 1; //increment the two part type
                            addCategoryCount(jsonObject.getJSONArray("jokes").getJSONObject(i));
                            addFlagCount(jsonObject.getJSONArray("jokes").getJSONObject(i));
                        }
                    }
                    numOfJokes += jsonObject.getInt("amount"); //get the number of jokes in the document
                }
                //If the object has headers then read the headers and convert them to a map
                if(jsonObject.has("headers"))
                {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = mapper.readValue(jsonObject.getString("headers"), Map.class);
                    getHeaders(map);
                }
            }

            //Sort the categories and flags based on their count in ascending order
            List<Object> listOfParameters = new ArrayList<>();
            Map<String,Integer> sortedCategories = category.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));;
            Map<String,Integer> sortedFlags = (Map<String, Integer>) flags.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            //Add the parameters to the list
            listOfParameters.add(sortedCategories); //category counts
            listOfParameters.add(num_of_single); //number of jokes of type single
            listOfParameters.add(num_of_twoPart); //number of jokes of type two part
            listOfParameters.add(numOfJokes); //total number of jokes
            listOfParameters.add(sortedFlags); //count of times flags have been avoided
            listOfParameters.add(uniqueUserAgents); //User-Agents using the app
            listOfParameters.add(minContentLength); //minimum content length that passed
            listOfParameters.add(jsonDocuments); //all the documents for logging
            return listOfParameters;
        }

    /**
     * function addCategory
     * The function is used to check the category of a joke and increment the count of that category
     * @param jsonObject
     */
    public static void addCategoryCount(JSONObject jsonObject)
        {
            //Check if map has the said category, if yes increment the count
            if(category.get(jsonObject.getString("category")) != null) {
                category.put(jsonObject.getString("category"),category.get(jsonObject.getString("category")) + 1);
            }
            //if the map does not have the category then add the said category to the map
            else {
                category.put(jsonObject.getString("category"),1);
            }
        }

    /**
     * function addFlagCount
     * Check the flags set for a joke and incrementing the count of those flags
     * @param jsonObject
     * @throws IOException
     */
    public static void addFlagCount(JSONObject jsonObject) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            //converting the flag string to a map class
            Map<String, Boolean> map = mapper.readValue(jsonObject.get("flags").toString(), Map.class);
            //for every flag
            for (Map.Entry<String, Boolean> entry: map.entrySet())
            {
                //checking the value and if it exists in map. if it does increment by 1
                if(entry.getValue().equals(true) && flags.get(entry.getKey()) != null)
                {
                    flags.put(entry.getKey(),flags.get(entry.getKey())+1);
                }
                //if it does not, add the flag and make the count as 1
                else if(entry.getValue().equals(true) && flags.get(entry.getKey()) == null)
                {
                    flags.put(entry.getKey(),1);
                }
            }

        }

    /**
     * function getHeaders
     * the function is used to get the headers of a json request sent
     * The headers that have been used are user-agent and content-length
     * @param map
     */
    public static void getHeaders(Map<String, String> map)
        {
            //Header map
                for (Map.Entry<String, String> entry: map.entrySet())
                {
                    //if there is a entry with user-agent, store it and later take the unique values
                    if(entry.getKey().equals("user-agent"))
                    {
                        userAgents.add(entry.getValue());
                    }
                    //if there is a entry with user-agent, store it
                    if(entry.getKey().equals("content-length"))
                    {
                        contentLength.add(Integer.parseInt(entry.getValue()));
                    }
                }
            uniqueUserAgents = new HashSet(userAgents); //converting to hash set for unique values
            minContentLength = Collections.min(contentLength); //minimum of content length stored
            maxContentLength = Collections.max(contentLength);//maximum of content length stored
        }

}
