package com.serli.oracle.of.bacon.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Optional;

public class MongoDbRepository {

    private final MongoClient mongoClient;

    public MongoDbRepository() {
        mongoClient = new MongoClient("localhost", 27017);
    }

    public Optional<Document> getActorByName(String name) {
        // TODO implement actor fetch
        Document doc = mongoClient.getDatabase("workshop")
                .getCollection("actors")
                .find(new Document("name", name))
                .first();

        return Optional.ofNullable(doc);
    }

    public static void main(String[] args) {
        MongoDbRepository mongo = new MongoDbRepository();
        Optional<Document> doc = mongo.getActorByName("Tom Hanks");
        System.out.println(doc.toString());
    }
}
