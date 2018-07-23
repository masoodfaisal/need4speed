package mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBConnectionHolder {

    private MongoDatabase database;


    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public DBConnectionHolder(){
        try{
        if(database == null){
            readWriteLock.writeLock().lock();
            if(database == null) {
                MongoClient mongoClient = MongoClients.create("mongodb://" + System.getenv("MONGO_IP"));
                database = mongoClient.getDatabase("mydb");
            }

        }}
        finally{
            readWriteLock.writeLock().unlock();;
        }

    }





    public MongoDatabase getDatabase(){
        try{
            readWriteLock.readLock().lock();
            return database;
        }

        finally{
            readWriteLock.readLock().unlock();
        }
    }
}
