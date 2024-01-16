package com.msresearch.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;
import java.util.List;


public class AutoCompleteApp {
    private static final String MONGODB_URI = System.getProperty("mongodb.uri");
    private static final String MONGODB_DB = System.getProperty("mongodb.database");
    private static final String MONGODB_COL = System.getProperty("mongodb.collection");
    private static final String QUERY_STRING = System.getProperty("query.string");

    public static void main(String[] args) {

        try (MongoClient mongoClient = MongoClients.create(MONGODB_URI)) {
            // set namespace
            MongoDatabase database = mongoClient.getDatabase(MONGODB_DB);
            MongoCollection<Document> collection = database.getCollection(MONGODB_COL);

            Bson autocomplete1 = new Document("autocomplete", new Document("query", QUERY_STRING)
                .append("path", "firstName")
                );

            Bson autocomplete2 = new Document("autocomplete", new Document("query", QUERY_STRING)
                .append("path", "lastName")
                );

            Bson autocomplete3 = new Document("autocomplete", new Document("query", QUERY_STRING)
                .append("path", "nickName")
                );

            Bson searchStage = new Document("$search", 
                new Document("index", "names")
                    .append("compound", new Document("should", Arrays.asList(autocomplete1, autocomplete2, autocomplete3))
                        .append("minimumShouldMatch", 1))
                    .append("highlight", new Document("path", Arrays.asList("firstName", "lastName", "nickName")))
            );

            Bson projectStage = new Document("$project", 
                Projections.fields(
                    Projections.include("firstName", "lastName", "nickName"),
                    new Document("score", new Document("$meta", "searchScore")),
                    new Document("highlight", new Document("$meta", "searchHighlights"))
                )
            );

            Bson limitStage = new Document("$limit", 5);

            List<Bson> pipeline = Arrays.asList(searchStage, projectStage, limitStage);

            collection.aggregate(pipeline).forEach(doc -> System.out.println(doc.toJson()));

        }
    }
}
