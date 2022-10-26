package co.edu.escuelaing.logservice;

import static spark.Spark.*;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Sorts.descending;

public class LogService {

    private static MongoClient mongoClient;
    private static MongoClientURI mongoClientURI;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    public static void main( String[] args ) {
        String connString = System.getenv("MONGODB_CONNSTRING");
        initMongoConnection(connString);
        port(getPort());

        get("messages", (request, response) -> {
            System.out.println("Received GET request");
            FindIterable<Document> docs = collection.find().sort(descending("createdAt")).limit(10);
            if (docs == null) {
                return "No messages found...";
            }
            ArrayList<String> messages = new ArrayList<>();
            for (Document doc : docs) {
                messages.add(doc.toJson());
            }
            return messages.toString();
        });

        post("messages", (request, response) -> {
            collection.insertOne(new Document().append("message", request.body()).append("createdAt", System.currentTimeMillis() + ""));
            return "Message stored...";
        });
    }

    private static Integer getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

    private static void initMongoConnection(String connString) {
        mongoClientURI = new MongoClientURI(connString);
        mongoClient = new MongoClient(mongoClientURI);
        database = mongoClient.getDatabase("database");
        try {
            collection = database.getCollection("messages");
        } catch (IllegalArgumentException ex) {
            database.createCollection("messages");
            collection = database.getCollection("messages");
        }
    }
}
