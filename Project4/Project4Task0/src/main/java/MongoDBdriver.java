import static com.mongodb.client.model.Filters.eq;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class MongoDBDriver
 * The class is used to connect to the mondo db service on ATlas
 * Where it stores a string provided by the user and displays the data from documents back to the user
 */
public class MongoDBdriver {

    /**
     * function main()
     * The main function is used to form the connection with the db
     * It calls functions to insert and display the documents from the user to db and vice versa
     * @param args
     */
    public static void main(String[] args) {
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
            MongoDatabase database = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = database.getCollection("string");
            //Insert the string given by the user
            insertDocument(collection);
            //display all the strings in the document to the user
            displayDocument(collection);
        }
    }

    /**
     * function insertDocument()
     * Prompt the user for a string, appends the string to the document and insert the document in the collection
     * @param collection the collection in which the document is to be inserted
     */
    public static void insertDocument(MongoCollection<Document> collection)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a string :");
        //User provided string
        String randomString = input.nextLine();
        Document insertDoc = new Document();
        //appending the string to the document
        insertDoc.append("string", randomString);
        //Inserting the string to the collection
        collection.insertOne(insertDoc);
    }

    /**
     * function displayDocument()
     * The function is used to display the documents
     * @param collection from which the documents are to be fetched and displayed
     */
    public static void displayDocument(MongoCollection<Document> collection)
    {
        //including just the string column of the document and excluding the id
        Bson projectionFields = Projections.fields(
                Projections.include("string"),
                Projections.excludeId());
        //Iterator for all the documents
        MongoCursor<Document> iterDoc = collection.find().projection(projectionFields).iterator();
        System.out.println("\nString Data in DB :\n");
        //if there are no documents in db
        if(iterDoc == null)
        {
            System.out.println("No document found");
        }
        else {
            //If there are documents in db
            while (iterDoc.hasNext()) {
                var doc = iterDoc.next();
                var listofStrings = new ArrayList<>(doc.values());
                //Displayig the string column of the document
                System.out.printf("%s %n", listofStrings.get(0));
            }
        }
    }
}
