package pw.octane.manager.networking.mongo;

import pw.octane.manager.OctaneManager;

import java.util.HashMap;

public class MongoRequest {
    private final String collectionName;
    private final Object id;
    private final HashMap<String, Object> updatePairs = new HashMap<>();

    private MongoRequest(final String collectionName, final Object id) {
        this.collectionName = collectionName;
        this.id = id;
    }

    public static MongoRequest newRequest(final String collectionName, final Object id) {
        return new MongoRequest(collectionName, id);
    }

    public MongoRequest put(final String key, final Object value) {
        this.updatePairs.put(key, value);
        return this;
    }

    public void run(final boolean async) {
        OctaneManager.get().getMongo().massUpdate(async, this.collectionName, this.id, this.updatePairs);
    }
}