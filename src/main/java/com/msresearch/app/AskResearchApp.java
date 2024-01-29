package com.msresearch.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;
import java.util.List;


public class AskResearchApp {
    private static final String MONGODB_URI = System.getProperty("mongodb.uri");
    private static final String MONGODB_DB = System.getProperty("mongodb.database");
    private static final String MONGODB_COL = System.getProperty("mongodb.collection");
    private static final String QUERY_STRING = System.getProperty("query.string");

    public static void main(String[] args) {

        try (MongoClient mongoClient = MongoClients.create(MONGODB_URI)) {
            // set namespace
            MongoDatabase database = mongoClient.getDatabase(MONGODB_DB);
            MongoCollection<Document> collection = database.getCollection(MONGODB_COL);

            Bson fuzzy = new Document()
                .append("maxEdits", 1);

            Bson autocomplete = new Document()
                .append("query", QUERY_STRING)
                .append("path", "title")
                .append("tokenOrder", "any")
                .append("fuzzy", fuzzy);

            Bson searchStage = new Document("$search", 
                new Document("index", "research-reports")
                    .append("autocomplete", autocomplete)
                    .append("sort", new Document("date", -1))
            );

            Bson limitStage = new Document("$limit", 5);

            List<Bson> pipeline = Arrays.asList(searchStage, limitStage);

            collection.aggregate(pipeline).forEach(doc -> System.out.println(doc.toJson()));

        }
    }
}
