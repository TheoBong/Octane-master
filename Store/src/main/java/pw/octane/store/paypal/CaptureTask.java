package pw.octane.store.paypal;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import pw.octane.manager.OctaneManager;
import pw.octane.manager.networking.mongo.MongoIterableResult;

public class CaptureTask implements Runnable {
    private OctaneManager instance;

    public CaptureTask(OctaneManager instance) {
        this.instance = instance;
    }


    @Override
    public void run() {
        System.out.println("Debugger: it ran the task!");

        //need to call the call method and test that

        instance.getMongo().getCollectionIterable(true, "paypal", new MongoIterableResult() {
            @Override
            public void call(FindIterable<Document> iterable) {
                iterable.forEach(document -> {
                    String id = document.getString("_id");
                    long expiry = document.getLong("expiry_time");
                    String player = document.getString("player");
                    double amount = document.getDouble("amount");

                    if (expiry < System.currentTimeMillis()) {
                        removePending(id);
                        return;
                    }

                    CaptureOrder.main(id, player, amount, expiry);
                });
            }
        });
    }

    private void removePending(String key) { //changed the key to the player UUID instead of order id
        this.instance.getMongo().deleteDocument(true, "paypal", key);
    }

}
