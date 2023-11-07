package pw.octane.manager.networking.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.UuidRepresentation;
import pw.octane.manager.OctaneManager;
import pw.octane.manager.threading.ThreadUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mongo {

    private OctaneManager manager;
    private MongoDatabase mongoDatabase;

    public Mongo(OctaneManager octaneManager) {
        this.manager = octaneManager;

        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).build());
        mongoDatabase = mongoClient.getDatabase("testing5");
    }

    public void createDocument(String collectionName, Object id, boolean async) {
        ThreadUtil.runTask(async, manager, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            Document document = new Document("_id", id);

            collection.insertOne(document);
        });
    }

    public void deleteDocument(boolean async, String collectionName, Object id) {
        ThreadUtil.runTask(async, manager, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            Document document = new Document("_id", id);

            collection.deleteMany(document);
        });
    }

    public void getOrCreateDocument(boolean async, String collectionName, Object id, MongoResult mongoResult) {
        ThreadUtil.runTask(async, manager, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            Document document = new Document("_id", id);

            try (final MongoCursor<Document> cursor = collection.find(document).iterator()) {
                if (cursor.hasNext()) {
                    mongoResult.call(cursor.next());
                } else {
                    collection.insertOne(document);
                    mongoResult.call(document);
                }
            }
        });
    }

    public void getDocument(boolean async, String collectionName, Object id, MongoResult mongoResult) {
        ThreadUtil.runTask(async, manager, () -> {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

            if (collection.find(Filters.eq("_id", id)).iterator().hasNext()) {
                mongoResult.call(collection.find(Filters.eq("_id", id)).first());
            } else {
                mongoResult.call(null);
            }
        });
    }

    public void massUpdate(final boolean async, final MongoUpdate mongoUpdate) {
        massUpdate(async, mongoUpdate.getCollectionName(), mongoUpdate.getId(), mongoUpdate.getUpdate());
    }

    public void massUpdate(final boolean async, final String collectionName, final Object id, final Map<String, Object> updates) {
        ThreadUtil.runTask(async, manager, () -> {
            final MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

            Document document = collection.find(new Document("_id", id)).first();
            if(document == null) {
                collection.insertOne(new Document("_id", id));
            }

            updates.forEach((key, value) -> collection.updateOne(Filters.eq("_id", id), Updates.set(key, value)));
        });
    }

    public void createCollection(boolean async, String collectionName) {
        ThreadUtil.runTask(async, manager, () -> {
            AtomicBoolean exists = new AtomicBoolean(false);
            mongoDatabase.listCollectionNames().forEach(s -> {
                if(s.equals(collectionName)) {
                    exists.set(true);
                }
            });

            if(!exists.get()) {
                mongoDatabase.createCollection(collectionName);
            }
        });
    }

    public void getOrCreateCollection(boolean async, String collectionName, MongoCollectionResult mcr) {
        ThreadUtil.runTask(async, manager, ()-> {
            mongoDatabase.listCollectionNames().forEach(s -> {
                if(s.equals(collectionName)) {
                    mcr.call(mongoDatabase.getCollection(s));
                }
            });

            mongoDatabase.createCollection(collectionName);
            mcr.call(mongoDatabase.getCollection(collectionName));
        });
    }

    public void getCollection(boolean async, String collectionName, MongoCollectionResult mcr) {
        ThreadUtil.runTask(async, manager, () -> mcr.call(mongoDatabase.getCollection(collectionName)));
    }

    public void getCollectionIterable(boolean async, String collectionName, MongoIterableResult mir) {
        ThreadUtil.runTask(async, manager, ()-> mir.call(mongoDatabase.getCollection(collectionName).find()));
    }
}
