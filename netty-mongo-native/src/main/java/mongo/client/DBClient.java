package mongo;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * A simple data holder class to hold mongod {@link MongoDatabase} object.
 *
 * @author fmasood@redhat.com
 */
public class DBClient {


    private MongoDatabase database;

    public void init(DBConnectionHolder dbConnectionHolder) {
        database = dbConnectionHolder.getDatabase();
    }


    public Mono<String> insertRecord() {


        Document document = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("time_value", LocalDateTime.now()));


        Mono<MongoCollection<Document>> mongoCollectionMono = Mono.fromSupplier(() -> database.getCollection(MONGO_COLLECTION_NAME));

        Mono<String> dataInserted = mongoCollectionMono.flatMap(monoCollection -> Mono.from(monoCollection.insertOne(document))
                .map(success -> {
                    StringBuilder s = new StringBuilder(30);
                    s.append(success.name());
                    s.append(LocalDateTime.now());
                    return s.toString();
                }));


        return dataInserted;

    }


    final static String MONGO_COLLECTION_NAME = "test";




}
