package pw.octane.manager.networking.mongo;

import com.mongodb.client.FindIterable;
import org.bson.Document;

public interface MongoIterableResult {
    void call(FindIterable<Document> iterable);
}
