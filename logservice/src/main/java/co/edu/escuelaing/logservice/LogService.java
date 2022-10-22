package co.edu.escuelaing.logservice;

import static spark.Spark.*;

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
    private static MongoCollection<Document> collection;

    public static void main( String[] args ) {
        String connString = System.getenv("MONGODB_CONNSTRING");
        collection = initMongoConnection(connString);
        port(getPort());

        get("messages", (request, response) -> {
            FindIterable<Document> docs = collection.find().sort(descending("createdAt")).limit(10);
            if (docs == null) {
                return "No messages found...";
            }
            String messages = "";
            for (Document doc : docs) {
                messages += doc.toJson();
            }
            return messages;
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

    private static MongoCollection<Document> initMongoConnection(String connString) {
        mongoClientURI = new MongoClientURI(connString);
        mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase database = mongoClient.getDatabase("database");
        database.createCollection("messages");
        MongoCollection<Document> collection = database.getCollection("messages");
        return collection;
    }
}
